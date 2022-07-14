package com.albar.computerstore.data.repository

import android.content.SharedPreferences
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.others.Constants
import com.albar.computerstore.others.Constants.COMPUTER_STORE_SESSION
import com.albar.computerstore.others.Constants.USERNAME_FIELD
import com.albar.computerstore.others.Tools.decryptCBC
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class AuthRepositoryImp(
    private val database: FirebaseFirestore,
    private val sharedPref: SharedPreferences,
    private val gson: Gson
) : AuthRepository {
    override fun loginComputerStore(
        username: String,
        password: String,
        result: (Result<Boolean>) -> Unit
    ) {
        val document = database.collection(Constants.COMPUTER_STORE_TABLE)
        document.whereEqualTo(USERNAME_FIELD, username)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    result.invoke(Result.Error("You are not registered"))
                }

                snapshot.forEach { document ->
                    val computerStore = document.toObject(ComputerStore::class.java)

                    if (computerStore.username == username &&
                        computerStore.password.decryptCBC() == password && !computerStore.isAdmin
                        && !computerStore.isVerified
                    ) {
                        result.invoke(Result.Error("Your account hasn't verified yet"))
                    } else if (computerStore.username == username &&
                        computerStore.password.decryptCBC() == password && !computerStore.isAdmin
                        && computerStore.isVerified
                    ) {
                        storeSession(computerStore.id) {
                            if (it == null) {
                                result.invoke(Result.Error("Failed to restore local data"))
                            } else {
                                result.invoke(Result.Success(true))
                            }
                        }
                    } else if (computerStore.username == username &&
                        computerStore.password.decryptCBC() == password && computerStore.isAdmin
                    ) {
                        storeSession(computerStore.id) {
                            if (it == null) {
                                result.invoke(Result.Error("Failed to restore local data"))
                            } else {
                                result.invoke(Result.Success(false))
                            }
                        }
                    } else {
                        result.invoke(Result.Error("Username and password not match"))
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
        val document = database.collection(Constants.COMPUTER_STORE_TABLE).document()
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

    private fun storeSession(id: String, result: (ComputerStore?) -> Unit) {
        val document = database.collection(Constants.COMPUTER_STORE_TABLE).document(id)
        document.get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val computerStore = it.result.toObject(ComputerStore::class.java)
                    sharedPref.edit()
                        .putString(COMPUTER_STORE_SESSION, gson.toJson(computerStore))
                        .apply()
                    result.invoke(computerStore)
                } else {
                    result.invoke(null)
                }
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }

    override fun getSession(result: (ComputerStore?) -> Unit) {
        val user = sharedPref.getString(COMPUTER_STORE_SESSION, null)
        if (user == null) {
            result.invoke(null)
        } else {
            val computerStore = gson.fromJson(user, ComputerStore::class.java)
            result.invoke(computerStore)
        }
    }

    override fun logout(result: () -> Unit) {
        sharedPref.edit().putString(COMPUTER_STORE_SESSION, null).apply()
        result.invoke()
    }
}