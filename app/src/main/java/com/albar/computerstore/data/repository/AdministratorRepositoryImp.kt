package com.albar.computerstore.data.repository

import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.others.Constants
import com.google.firebase.firestore.FirebaseFirestore

class AdministratorRepositoryImp(
    private val database: FirebaseFirestore
) : AdministratorRepository {
    override fun getUnverifiedOrVerifiedList(
        isVerified: Boolean,
        result: (Result<List<ComputerStore>>) -> Unit
    ) {
        val document = database.collection(Constants.FIRESTORE_TABLE)
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
}