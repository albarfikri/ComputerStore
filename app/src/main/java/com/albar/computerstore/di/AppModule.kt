package com.albar.computerstore.di

import android.content.Context
import com.albar.computerstore.others.DataStoreUtility
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module

@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideContextDataStoreUtility(@ApplicationContext app: Context): DataStoreUtility =
        DataStoreUtility(app)
}