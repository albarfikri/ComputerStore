package com.albar.computerstore.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.data.repository.ComputerStoreRepository

class ComputerStoreViewModel(
    private val repository: ComputerStoreRepository
) : ViewModel() {

    private val _computerStore = MutableLiveData<List<ComputerStore>>()

    val computerStore: LiveData<List<ComputerStore>>
        get() = _computerStore

    fun getComputerStore(){
      _computerStore.value = repository.getComputerStore()
    }

}