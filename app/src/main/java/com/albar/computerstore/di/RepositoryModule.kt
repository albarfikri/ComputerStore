package com.albar.computerstore.di

import android.content.SharedPreferences
import com.albar.computerstore.data.repository.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideComputerStoreRepository(
        database: FirebaseFirestore,
        storageReference: StorageReference,
        sharedPref: SharedPreferences,
        gson: Gson
    ): ComputerStoreRepository =
        ComputerStoreRepositoryImp(database, storageReference, sharedPref, gson)


    @Provides
    fun provideAuthRepository(
        database: FirebaseFirestore,
        sharedPref: SharedPreferences,
        gson: Gson
    ): AuthRepository =
        AuthRepositoryImp(database, sharedPref, gson)


    @Provides
    fun provideAdministratorRepository(
        database: FirebaseFirestore,
        sharedPref: SharedPreferences
    ): AdministratorRepository =
        AdministratorRepositoryImp(database, sharedPref)

    @Provides
    fun provideComputerStoreProductRepository(
        database: FirebaseFirestore,
        storageReference: StorageReference,
        sharedPref: SharedPreferences,
        gson: Gson
    ): ComputerStoreProductRepository =
        ComputerStoreProductRepositoryImp(database, storageReference, sharedPref, gson)
}