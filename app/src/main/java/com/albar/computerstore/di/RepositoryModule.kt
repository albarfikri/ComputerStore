package com.albar.computerstore.di

import com.albar.computerstore.data.repository.*
import com.google.firebase.firestore.FirebaseFirestore
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
        database: FirebaseFirestore
    ): AuthRepository {
        return AuthRepositoryImp(database)
    }

    @Provides
    fun provideAdministratorRepository(
        database: FirebaseFirestore
    ): AdministratorRepository {
        return AdministratorRepositoryImp(database)
    }
}