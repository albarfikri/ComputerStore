package com.albar.computerstore.data.repository

import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ComputerStoreRepositoryImp(val database: FirebaseFirestore) : ComputerStoreRepository {

    // get data from firebase
    override fun getComputerStore(): Result<List<ComputerStore>> {
        val data = arrayListOf(
            ComputerStore(
                id = "123",
                name = "albarComp",
                address = "Jl. Al khalish No.5",
                lat = 101.2222,
                lng = 53.000,
                image = "https://google.com",
                username = "albarfikri",
                password = "albarfikri",
                createAt = Date(),
                isVerified = false
            ),
            ComputerStore(
                id = "1234",
                name = "albarComp",
                address = "Jl. Al khalish No.5",
                lat = 101.2222,
                lng = 53.000,
                image = "https://google.com",
                username = "albarfikri",
                password = "albarfikri",
                createAt = Date(),
                isVerified = false
            )
        )
        if (data.isNullOrEmpty()) {
            return Result.Error("Data is Empty")
        }else{
            return Result.Success(data)
        }
    }
}