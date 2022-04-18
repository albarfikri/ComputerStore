package com.albar.computerstore.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.albar.computerstore.others.DataStoreUtility
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DataStoreViewModel @Inject constructor(private val dataStoreUtility: DataStoreUtility) :
    ViewModel() {
    fun getDataStoreStatus(): LiveData<Boolean> {
        return dataStoreUtility.
    }
}