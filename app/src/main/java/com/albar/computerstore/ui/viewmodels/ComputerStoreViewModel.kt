package com.albar.computerstore.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.data.repository.ComputerStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ComputerStoreViewModel @Inject constructor(
    private val repository: ComputerStoreRepository
) : ViewModel() {

    private val _computerStore = MutableLiveData<Result<List<ComputerStore>>>()
    val computerStore: LiveData<Result<List<ComputerStore>>>
        get() = _computerStore

    private val _insertComputerStore = MutableLiveData<Result<String>>()
    val insertComputerStore: LiveData<Result<String>>
        get() = _insertComputerStore

    fun getComputerStore() {
        _computerStore.value = Result.Loading
        repository.getComputerStore { _computerStore.value = it }
    }

    fun insertComputerStore(computerStore: ComputerStore) {
        _insertComputerStore.value = Result.Loading
        repository.insertComputerStore(computerStore) {
            _insertComputerStore.value = it
        }

    }

}