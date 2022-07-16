package com.albar.computerstore.data.repository


import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStoreProduct

interface ComputerStoreProductRepository {
    fun getComputerStoreProduct(result: (Result<List<ComputerStoreProduct>>) -> Unit)

    fun getProductByType(
        idComputerStore: String,
        type: String,
        result: (Result<List<ComputerStoreProduct>>) -> Unit
    )

    fun getProductById(
        id: String,
        result: (Result<ComputerStoreProduct>) -> Unit
    )

    fun addComputerStoreProduct(
        computerStoreProduct: ComputerStoreProduct,
        result: (Result<String>) -> Unit
    )

    fun deleteComputerStoreProduct(
        computerStoreProduct: ComputerStoreProduct,
        result: (Result<String>) -> Unit
    )

    fun updateComputerStoreProduct(
        computerStoreProduct: ComputerStoreProduct,
        result: (Result<String>) -> Unit
    )
}