package com.albar.computerstore.di

import com.albar.computerstore.data.repository.ComputerStoreRepository
import com.albar.computerstore.data.repository.ComputerStoreRepositoryImp
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
}