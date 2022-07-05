package com.albar.computerstore.data.repository

import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore

interface AuthRepository {
    fun loginComputerStore(username: String, password: String, result: (Result<Boolean>) -> Unit)
    fun registerComputerStore(computerStore: ComputerStore, result: (Result<String>) -> Unit)
    fun getSession(result: (ComputerStore?) -> Unit)
    fun logout(result: () -> Unit)
}