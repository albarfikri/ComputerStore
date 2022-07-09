package com.albar.computerstore.data.repository

import android.net.Uri
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.others.Constants.FIRESTORE_TABLE
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ComputerStoreRepositoryImp(
    private val database: FirebaseFirestore,
    private val storageRef: StorageReference
) : ComputerStoreRepository {

    // get data from firebase
    override fun getComputerStore(
        result: (Result<List<ComputerStore>>) -> Unit
    ) {
        val document = database.collection(FIRESTORE_TABLE)
        document.whereNotEqualTo("isAdmin", true).whereEqualTo("isVerified", true)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    result.invoke(Result.Error("Data is empty"))
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

    override suspend fun uploadImage(fileUri: Uri, onResult: (Result<Uri>) -> Unit) {
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storageRef
                    .putFile(fileUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            onResult.invoke(Result.Success(uri))
        } catch (e: FirebaseException) {
            onResult.invoke(Result.Error(e.message.toString()))
        } catch (e: Exception) {
            onResult.invoke(Result.Error(e.message.toString()))
        }
    }
}
