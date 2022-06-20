package com.albar.computerstore.data.repository

import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore

interface ComputerStoreRepository {
    fun getComputerStore(): Result<List<ComputerStore>>
}