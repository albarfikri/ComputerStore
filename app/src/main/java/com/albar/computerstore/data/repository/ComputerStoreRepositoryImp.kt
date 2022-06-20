package com.albar.computerstore.data.repository

import com.albar.computerstore.data.remote.entity.ComputerStore

class ComputerStoreRepositoryImp : ComputerStoreRepository {

    // get data from firebase
    override fun getComputerStore(): List<ComputerStore> {
        return arrayListOf()
    }
}