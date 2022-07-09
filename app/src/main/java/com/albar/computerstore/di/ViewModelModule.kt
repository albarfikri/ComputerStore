package com.albar.computerstore.di

import android.content.Context
import android.net.ConnectivityManager
import com.albar.computerstore.others.DataStoreUtility
import com.albar.computerstore.ui.viewmodels.DataStoreViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @ViewModelScoped
    fun provideDataStoreUtility(dataStoreUtility: DataStoreUtility) {
        DataStoreViewModel(dataStoreUtility)
    }

    @ViewModelScoped
    @Provides
    fun connectivityManager(@ApplicationContext app: Context): ConnectivityManager =
        app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}