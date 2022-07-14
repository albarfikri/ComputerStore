package com.albar.computerstore.data.repository

import android.net.Uri
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.data.remote.entity.ComputerStoreProduct

interface ComputerStoreProductRepository {
    fun addComputerStoreProduct(computerStoreProduct: ComputerStoreProduct, result: (Result<String>) -> Unit)
}