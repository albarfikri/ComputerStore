package com.albar.computerstore.data.repository

import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.others.Constants.FIRESTORE_TABLE
import com.google.firebase.firestore.FirebaseFirestore

class ComputerStoreRepositoryImp(private val database: FirebaseFirestore) :
    ComputerStoreRepository {

    // get data from firebase
    override fun getComputerStore(
        result: (Result<List<ComputerStore>>) -> Unit
    ) {
        val document = database.collection(FIRESTORE_TABLE)
        document.whereNotEqualTo("isAdmin", true).whereEqualTo("isVerified", true)
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

    override fun updateComputerStore(
        computerStore: ComputerStore,
        result: (Result<String>) -> Unit
    ) {
        val document = database.collection(FIRESTORE_TABLE).document(computerStore.id)

        document
            .set(computerStore)
            .addOnSuccessListener {
                result.invoke(Result.Success("Data has been updated successfully"))
            }
            .addOnFailureListener {
                result.invoke(Result.Error(it.localizedMessage!!))
            }
    }

    override fun isUsernameUsed(username: String, result: (Result<Boolean>) -> Unit) {
        val databaseRef = database.collection(FIRESTORE_TABLE)
        databaseRef.whereEqualTo("username", username).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    result.invoke(Result.Success(true))
                } else {
                    result.invoke(Result.Success(false))
                }
            }
            .addOnFailureListener {
                result.invoke(Result.Error(it.localizedMessage!!))
            }
    }
}
