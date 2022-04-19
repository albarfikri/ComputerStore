package com.albar.computerstore.di

import androidx.lifecycle.ViewModel
import com.albar.computerstore.others.DataStoreUtility
import com.albar.computerstore.ui.viewmodels.DataStoreViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @ViewModelScoped
    fun provideDataStoreUtility(dataStoreUtility: DataStoreUtility) {
        DataStoreViewModel(dataStoreUtility)
    }
}