package com.albar.computerstore.data.repository

import android.content.SharedPreferences
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStoreProduct
import com.albar.computerstore.others.Constants.COMPUTER_STORE_PRODUCT_TABLE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson

class ComputerStoreProductRepositoryImp(
    private val database: FirebaseFirestore,
    private val storageRef: StorageReference,
    private val sharedPref: SharedPreferences,
    private val gson: Gson
) : ComputerStoreProductRepository {

    override fun addComputerStoreProduct(
        computerStoreProduct: ComputerStoreProduct,
        result: (Result<String>) -> Unit
    ) {
        val document =
            database.collection(COMPUTER_STORE_PRODUCT_TABLE).document()
        computerStoreProduct.id = document.id
        document.set(computerStoreProduct)
            .addOnSuccessListener {
                result.invoke(Result.Success("Data has been inserted successfully"))
            }
            .addOnFailureListener {
                result.invoke(Result.Error("Failed to insert local data"))
            }
    }
}
