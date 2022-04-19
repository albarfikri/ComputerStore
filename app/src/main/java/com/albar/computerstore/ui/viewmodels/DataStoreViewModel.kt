package com.albar.computerstore.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.albar.computerstore.others.Constants.ON_BOARDING_DATA_STORE_KEY
import com.albar.computerstore.others.DataStoreUtility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataStoreViewModel @Inject constructor(private val dataStoreUtility: DataStoreUtility) :
    ViewModel() {
    fun getDataStoreStatus(): LiveData<String> {
        return dataStoreUtility.getDataStore(ON_BOARDING_DATA_STORE_KEY).asLiveData()
    }

    fun saveDataStoreStatus(key: String, value: Boolean) {
        viewModelScope.launch {
            dataStoreUtility.save(key, value)
        }
    }
}