package com.albar.computerstore.data.repository

import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore

interface AdministratorRepository {
    fun getUnverifiedOrVerifiedList(
        isVerified: Boolean, result: (Result<List<ComputerStore>>) -> Unit
    )

    fun getSessionNumber(result: (Result<List<Int>>) -> Unit)
}