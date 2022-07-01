package com.albar.computerstore.data.repository

import android.util.Log
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.others.Constants
import com.albar.computerstore.others.Constants.USERNAME_FIELD
import com.albar.computerstore.others.Tools.decryptCBC
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepositoryImp(private val database: FirebaseFirestore) : AuthRepository {
    override fun loginComputerStore(
        username: String,
        password: String,
        result: (Result<Boolean>) -> Unit
    ) {
        val document = database.collection(Constants.FIRESTORE_TABLE)
        document.whereEqualTo(USERNAME_FIELD, username)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    result.invoke(Result.Error("Not match"))
                }
                snapshot.forEach { document ->
                    val computerStore = document.toObject(ComputerStore::class.java)

                    if (computerStore.username == username &&
                        computerStore.password.decryptCBC() == password && !computerStore.isAdmin
                    ) {
                        result.invoke(Result.Success(true))
                        Log.d("Output", "${computerStore.id}, ${computerStore.isAdmin}, ${computerStore.address}")
                    } else if (computerStore.username == username &&
                        computerStore.password.decryptCBC() == password && computerStore.isAdmin
                    ) {
                        result.invoke(Result.Success(false))
                        Log.d("Output", "${computerStore.id}, ${computerStore.isAdmin}")
                    } else {
                        result.invoke(Result.Error("Not match"))
                    }
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