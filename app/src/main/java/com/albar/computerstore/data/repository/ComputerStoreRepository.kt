package com.albar.computerstore.data.repository

import android.net.Uri
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore

interface ComputerStoreRepository {
    fun getComputerStore(result: (Result<List<ComputerStore>>) -> Unit)
    fun updateComputerStore(computerStore: ComputerStore, result: (Result<String>) -> Unit)
    fun updateVerifiedOrUnVerifiedStore(computerStore: ComputerStore, result: (Result<String>) -> Unit)
    fun isUsernameUsed(username: String, result: (Result<Boolean>) -> Unit)
    fun deleteComputerStore(computerStore: ComputerStore, result: (Result<String>) -> Unit)
    suspend fun uploadImage(fileUri: Uri, onResult: (Result<Uri>) -> Unit)
}