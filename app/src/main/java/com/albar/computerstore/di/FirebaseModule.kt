package com.albar.computerstore.di

import com.albar.computerstore.others.Constants.STORAGE_ROOT_DIRECTORY
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFireStoreInstance(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorageInstance(): StorageReference =
        FirebaseStorage.getInstance().getReference(STORAGE_ROOT_DIRECTORY)
}