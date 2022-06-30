package com.albar.computerstore.data.repository

import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.others.Constants
import com.albar.computerstore.others.Tools.encrypt
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepositoryImp(private val database: FirebaseFirestore) : AuthRepository {
    override fun loginComputerStore(
        username: String,
        password: String,
        result: (Result<Boolean>) -> Unit
    ) {
        val document = database.collection(Constants.FIRESTORE_TABLE)
        document.whereEqualTo("username", username)
            .whereEqualTo("password", encrypt(password)).get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    result.invoke(Result.Success(false))
                } else {
                    result.invoke(Result.Success(true))
                }
            }
            .addOnFailureListener {
                result.invoke(Result.Error(it.localizedMessage!!))
            }
    }

    override fun registerComputerStore(
        computerStore: ComputerStore,
        result: (Result<String>) -> Unit
    ) {
        val document = database.collection(Constants.FIRESTORE_TABLE).document()
        computerStore.id = document.id
        document.set(computerStore)
            .addOnSuccessListener {
                result.invoke(
                    Result.Success("Successfully registered")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Result.Error(it.localizedMessage!!)
                )
            }
    }
}