package com.albar.computerstore.di

import androidx.lifecycle.ViewModel
import com.albar.computerstore.others.DataStoreUtility
import com.albar.computerstore.ui.DataStoreViewModel
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModel::class)
object ViewModelModule {
    @ViewModelScoped
    fun provideDataStoreUtility(dataStoreUtility: DataStoreUtility) {
        DataStoreViewModel(dataStoreUtility)
    }
}