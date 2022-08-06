package com.albar.computerstore.data.repository

import android.content.SharedPreferences
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStoreProduct
import com.albar.computerstore.others.Constants.COMPUTER_STORE_PRODUCT_TABLE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson

class ComputerStoreProductRepositoryImp(
    private val database: FirebaseFirestore,
    private val storageRef: FirebaseStorage,
    private val sharedPref: SharedPreferences,
    private val gson: Gson
) : ComputerStoreProductRepository {
    override fun getAllProductByIdComputerStore(
        idComputerStore: String,
        result: (Result<List<ComputerStoreProduct>>) -> Unit
    ) {
        val document = database.collection(COMPUTER_STORE_PRODUCT_TABLE)
        document.whereEqualTo("idComputerStore", idComputerStore)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    result.invoke(Result.Error(""))
                }
                val computerStoreList = arrayListOf<ComputerStoreProduct>()
                it.forEach { document ->
                    //convert document from firebase to our data class
                    val computerStore = document.toObject(ComputerStoreProduct::class.java)
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

    override fun getProductByType(
        idComputerStore: String,
        type: String,
        result: (Result<List<ComputerStoreProduct>>) -> Unit
    ) {
        val document = database.collection(COMPUTER_STORE_PRODUCT_TABLE)
        document.whereEqualTo("idComputerStore", idComputerStore).whereEqualTo("productType", type)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    result.invoke(Result.Error(""))
                }
                val computerStoreProductList = arrayListOf<ComputerStoreProduct>()

                it.forEach { document ->
                    //convert document from firebase to our data class
                    val computerStoreProduct = document.toObject(ComputerStoreProduct::class.java)
                    // then adding to our array list
                    computerStoreProductList.add(computerStoreProduct)
                    result.invoke(Result.Success(computerStoreProductList))
                }
            }
            .addOnFailureListener {
                result.invoke(
                    Result.Error(it.localizedMessage!!)
                )
            }
    }

    override fun getProductById(id: String, result: (Result<ComputerStoreProduct>) -> Unit) {
        val document = database.collection(COMPUTER_STORE_PRODUCT_TABLE)
        document.whereEqualTo("id", id)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    result.invoke(Result.Error(""))
                }

                it.forEach { document ->
                    val computerStoreProduct = document.toObject(ComputerStoreProduct::class.java)
                    result.invoke(Result.Success(computerStoreProduct))
                }
            }
            .addOnFailureListener {
                result.invoke(
                    Result.Error(it.localizedMessage!!)
                )
            }
    }

    override fun getProductByName(
        idComputerStore: String,
        productName: String,
        result: (Result<List<ComputerStoreProduct>>) -> Unit
    ) {
        val document = database.collection(COMPUTER_STORE_PRODUCT_TABLE)
        document.whereEqualTo("idComputerStore", idComputerStore)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    result.invoke(Result.Error(""))
                }

                val computerStoreProductList = arrayListOf<ComputerStoreProduct>()

                it.forEach { document ->
                    val computerStoreProduct =
                        document.toObject(ComputerStoreProduct::class.java)
                    if (computerStoreProduct.productName.lowercase()
                            .contains(productName.lowercase())
                    ) {
                        computerStoreProductList.add(computerStoreProduct)
                        result.invoke(Result.Success(computerStoreProductList))
                    }
                    if (computerStoreProductList.isEmpty()) {
                        result.invoke(Result.Error("No data Available"))
                    }

                }
            }
            .addOnFailureListener {
                result.invoke(
                    Result.Error(it.localizedMessage!!)
                )
            }
    }

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

    override fun deleteComputerStoreProduct(
        computerStoreProduct: ComputerStoreProduct,
        result: (Result<String>) -> Unit
    ) {
        val document =
            database.collection(COMPUTER_STORE_PRODUCT_TABLE).document(computerStoreProduct.id)
        document.delete()
            .addOnSuccessListener {
                result.invoke(Result.Success("Data has been deleted successfully"))
            }
            .addOnFailureListener {
                result.invoke(Result.Error("Failed to deleted data"))
            }
    }

    override fun updateComputerStoreProduct(
        computerStoreProduct: ComputerStoreProduct,
        result: (Result<String>) -> Unit
    ) {
        val document =
            database.collection(COMPUTER_STORE_PRODUCT_TABLE).document(computerStoreProduct.id)
        document
            .set(computerStoreProduct)
            .addOnSuccessListener {
                result.invoke(Result.Success("Data has been updated successfully"))
            }
            .addOnFailureListener {
                result.invoke(Result.Error("Failed to restore local data"))
            }
    }
}