package com.albar.computerstore.data.repository

import android.content.SharedPreferences
import android.net.Uri
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.data.remote.entity.ComputerStoreProduct
import com.albar.computerstore.others.Constants
import com.albar.computerstore.others.Constants.COMPUTER_STORE_PRODUCT_TABLE
import com.albar.computerstore.others.Constants.COMPUTER_STORE_TABLE
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ComputerStoreRepositoryImp(
    private val database: FirebaseFirestore,
    private val storageRef: FirebaseStorage,
    private val sharedPref: SharedPreferences,
    private val gson: Gson
) : ComputerStoreRepository {

    // get data from firebase
    override fun getComputerStore(
        result: (Result<List<ComputerStore>>) -> Unit
    ) {
        val document = database.collection(COMPUTER_STORE_TABLE)
        document.whereNotEqualTo("isAdmin", true).whereEqualTo("isVerified", true)
            .get()
            .addOnSuccessListener { data ->
                val computerStoreList = arrayListOf<ComputerStore>()
                data.forEach { document ->
                    //convert document from firebase to our data class
                    val computerStore = document.toObject(ComputerStore::class.java)
                    // then adding to our array list
                    computerStoreList.add(computerStore)
                }
                result.invoke(Result.Success(computerStoreList.sortedBy { it.name }))
            }
            .addOnFailureListener {
                result.invoke(
                    Result.Error(it.localizedMessage!!)
                )
            }
    }

    override fun getComputerStoreByName(
        name: String,
        result: (Result<List<ComputerStore>>) -> Unit
    ) {
        val databaseRef = database.collection(COMPUTER_STORE_TABLE)
        databaseRef.whereNotEqualTo("isAdmin", true).whereEqualTo("isVerified", true)
            .get()
            .addOnSuccessListener { data ->
                val computerStoreList = arrayListOf<ComputerStore>()
                data.forEach { document ->
                    val computerStore =
                        document.toObject(ComputerStore::class.java)
                    if (computerStore.name.lowercase()
                            .contains(name.lowercase())
                    ) {
                        computerStoreList.add(computerStore)
                        result.invoke(Result.Success(computerStoreList.sortedBy { it.name }))
                    }
                    if (computerStoreList.isEmpty()) {
                        result.invoke(Result.Error("No data Available"))
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(Result.Error(it.localizedMessage!!))
            }
    }

    override fun updateComputerStore(
        computerStore: ComputerStore,
        result: (Result<String>) -> Unit
    ) {
        val document = database.collection(COMPUTER_STORE_TABLE).document(computerStore.id)
        document
            .set(computerStore)
            .addOnSuccessListener {
                storeSession(computerStore.id) { objComputerStore ->
                    if (objComputerStore == null) {
                        result.invoke(Result.Error("Failed to restore local data"))
                    } else {
                        result.invoke(Result.Success("Data has been updated successfully"))
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(Result.Error(it.localizedMessage!!))
            }
    }

    override fun updateVerifiedOrUnVerifiedStore(
        computerStore: ComputerStore,
        result: (Result<String>) -> Unit
    ) {
        val document = database.collection(COMPUTER_STORE_TABLE).document(computerStore.id)
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
        val databaseRef = database.collection(COMPUTER_STORE_TABLE)
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

    override fun deleteComputerStore(
        computerStore: ComputerStore,
        result: (Result<String>) -> Unit
    ) {
        val document = database.collection(COMPUTER_STORE_TABLE).document(computerStore.id)
        deleteProductInComputerStore(computerStore)
        document
            .delete()
            .addOnSuccessListener {

                result.invoke(
                    Result.Success("Computer Store has been deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(Result.Error(it.localizedMessage!!))
            }
    }

    private fun deleteProductInComputerStore(computerStore: ComputerStore) {
        val product = ComputerStoreProduct()
        product.idComputerStore = computerStore.id

        val document = database.collection(COMPUTER_STORE_PRODUCT_TABLE)
        document
            .document(product.idComputerStore)
            .delete()
            .addOnSuccessListener {
                Result.Success("Computer Store has been deleted successfully")
            }
            .addOnFailureListener {
                Result.Error(it.localizedMessage!!)
            }
    }

    override suspend fun uploadImage(fileUri: Uri, onResult: (Result<Uri>) -> Unit) {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val filename = formatter.format(now)
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storageRef.reference.child("Images/$filename")
                    .putFile(fileUri).await()
                    .storage
                    .downloadUrl.await()
            }
            onResult.invoke(Result.Success(uri))
        } catch (e: FirebaseException) {
            onResult.invoke(Result.Error(e.message.toString()))
        } catch (e: Exception) {
            onResult.invoke(Result.Error(e.message.toString()))
        }
    }

    private fun storeSession(id: String, result: (ComputerStore?) -> Unit) {
        val document = database.collection(COMPUTER_STORE_TABLE).document(id)
        document.get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val computerStore = it.result.toObject(ComputerStore::class.java)
                    sharedPref.edit()
                        .putString(Constants.COMPUTER_STORE_SESSION, gson.toJson(computerStore))
                        .apply()
                    result.invoke(computerStore)
                } else {
                    result.invoke(null)
                }
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }
}
