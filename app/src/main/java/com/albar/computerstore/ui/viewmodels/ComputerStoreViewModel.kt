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

    private val _updateComputerStore = MutableLiveData<Result<String>>()
    val updateComputerStore: LiveData<Result<String>>
        get() = _updateComputerStore

    private val _isUsernameUsed = MutableLiveData<Result<Boolean>>()
    val isUsernameUsed: LiveData<Result<Boolean>>
        get() = _isUsernameUsed

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

    fun updateComputerStore(computerStore: ComputerStore) {
        _updateComputerStore.value = Result.Loading
        repository.updateComputerStore(computerStore) {
            _updateComputerStore.value = it
        }
    }

    fun isUsernameUsed(username: String) {
        _isUsernameUsed.value = Result.Loading
        repository.isUsernameUsed(username) { result ->
            _isUsernameUsed.value = result
        }
    }

}