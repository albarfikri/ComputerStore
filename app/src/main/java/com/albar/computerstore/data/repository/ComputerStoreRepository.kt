package com.albar.computerstore.data.repository

import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore

interface ComputerStoreRepository {
    fun getComputerStore(result: (Result<List<ComputerStore>>) -> Unit)
    fun updateComputerStore(computerStore: ComputerStore, result: (Result<String>) -> Unit)
    fun isUsernameUsed(username: String, result: (Result<Boolean>) -> Unit)
}