package com.albar.computerstore.data.repository

import android.content.SharedPreferences
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.others.Constants
import com.google.firebase.firestore.FirebaseFirestore

class AdministratorRepositoryImp(
    private val database: FirebaseFirestore,
    private val sharedPref: SharedPreferences
) :
    AdministratorRepository {
    override fun getUnverifiedOrVerifiedList(
        isVerified: Boolean,
        result: (Result<List<ComputerStore>>) -> Unit
    ) {
        val document = database.collection(Constants.FIRESTORE_TABLE)
        document.whereEqualTo("isVerified", isVerified)
            .get()
            .addOnSuccessListener {
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
}