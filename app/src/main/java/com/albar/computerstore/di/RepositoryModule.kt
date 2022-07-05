package com.albar.computerstore.di

import android.content.SharedPreferences
import com.albar.computerstore.data.repository.*
import com.google.firebase.firestore.FirebaseFirestore
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
        database: FirebaseFirestore
    ): ComputerStoreRepository {
        return ComputerStoreRepositoryImp(database)
    }

    @Provides
    fun provideAuthRepository(
        database: FirebaseFirestore,
        sharedPref: SharedPreferences,
        gson: Gson
    ): AuthRepository {
        return AuthRepositoryImp(database, sharedPref, gson)
    }

    @Provides
    fun provideAdministratorRepository(
        database: FirebaseFirestore,
    ): AdministratorRepository {
        return AdministratorRepositoryImp(database)
    }
}