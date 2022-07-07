package com.albar.computerstore.data.repository

import android.content.SharedPreferences
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.others.Constants.FIRESTORE_TABLE
import com.albar.computerstore.others.Constants.UNVERIFIED_NUMBERS
import com.albar.computerstore.others.Constants.VERIFIED_NUMBERS
import com.google.firebase.firestore.FirebaseFirestore

class AdministratorRepositoryImp(
    private val database: FirebaseFirestore,
    private val sharedPref: SharedPreferences
) : AdministratorRepository {
    override fun getUnverifiedOrVerifiedList(
        isVerified: Boolean,
        result: (Result<List<ComputerStore>>) -> Unit
    ) {
        val document = database.collection(FIRESTORE_TABLE)
        document.whereEqualTo("isVerified", isVerified)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    result.invoke(Result.Error("No data Available"))
                }
                val computerStoreList = arrayListOf<ComputerStore>()

                it.forEach { document ->
                    //convert document from firebase to our data class
                    val computerStore = document.toObject(ComputerStore::class.java)
                    // then adding to our array list
                    computerStoreList.add(computerStore)
                    result.invoke(Result.Success(computerStoreList))
                }
            }
            .addOnFailureListener {
                result.invoke(
                    Result.Error(it.localizedMessage!!)
                )
            }
    }

    override fun getSessionNumber(result: (Result<List<Int>>) -> Unit) {
        verified()
        unverified()
        val verified = sharedPref.getInt(VERIFIED_NUMBERS, 0)
        val unverified = sharedPref.getInt(UNVERIFIED_NUMBERS, 0)
        val data = listOf(verified, unverified)
        result.invoke(Result.Success(data))
    }

    private fun verified() {
        val document = database.collection(FIRESTORE_TABLE)
        document.whereEqualTo("isVerified", true)
            .get()
            .addOnSuccessListener {
                setSharedPref(VERIFIED_NUMBERS, it.size())
            }
    }

    private fun unverified() {
        val document = database.collection(FIRESTORE_TABLE)
        document.whereEqualTo("isVerified", false)
            .get()
            .addOnSuccessListener {
                setSharedPref(UNVERIFIED_NUMBERS, it.size())
            }
    }

    private fun setSharedPref(key: String, value: Int) {
        sharedPref.edit()
            .putInt(key, value)
            .apply()
    }
}