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
        database.collection(FIRESTORE_TABLE)
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

    override fun insertComputerStore(
        computerStore: ComputerStore,
        result: (Result<String>) -> Unit
    ) {
        val document = database.collection(FIRESTORE_TABLE).document()
        computerStore.id = document.id
        document.set(computerStore)
            .addOnSuccessListener {
                result.invoke(
                    Result.Success("Note has been created successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Result.Error(it.localizedMessage!!)
                )
            }
    }
}

