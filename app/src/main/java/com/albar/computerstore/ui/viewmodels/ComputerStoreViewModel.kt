package com.albar.computerstore.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.data.repository.AdministratorRepository
import com.albar.computerstore.data.repository.AuthRepository
import com.albar.computerstore.data.repository.ComputerStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ComputerStoreViewModel @Inject constructor(
    private val repository: ComputerStoreRepository,
    private val authRepository: AuthRepository,
    private val administratorRepository: AdministratorRepository
) : ViewModel() {

    private val _computerStore = MutableLiveData<Result<List<ComputerStore>>>()
    val computerStore: LiveData<Result<List<ComputerStore>>>
        get() = _computerStore

    private val _updateComputerStore = MutableLiveData<Result<String>>()
    val updateComputerStore: LiveData<Result<String>>
        get() = _updateComputerStore

    private val _isUsernameUsed = MutableLiveData<Result<Boolean>>()
    val isUsernameUsed: LiveData<Result<Boolean>>
        get() = _isUsernameUsed

    private val _registerComputerStore = MutableLiveData<Result<String>>()
    val registerComputerStore: LiveData<Result<String>>
        get() = _registerComputerStore

    private val _loginComputerStore = MutableLiveData<Result<Boolean>>()
    val loginComputerStore: LiveData<Result<Boolean>>
        get() = _loginComputerStore

    private val _unverifiedOrVerifiedComputerStoreList = MutableLiveData<Result<List<ComputerStore>>>()
    val unverifiedOrVerifiedComputerStoreList: LiveData<Result<List<ComputerStore>>>
        get() = _unverifiedOrVerifiedComputerStoreList


    fun getComputerStore() {
        _computerStore.value = Result.Loading
        repository.getComputerStore { _computerStore.value = it }
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

    // Auth Repository
    fun registerComputerStore(computerStore: ComputerStore) {
        _registerComputerStore.value = Result.Loading
        authRepository.registerComputerStore(computerStore) {
            _registerComputerStore.value = it
        }
    }

    // Auth Repository
    fun login(username: String, password: String) {
        _loginComputerStore.value = Result.Loading
        authRepository.loginComputerStore(username, password) {
            _loginComputerStore.value = it
        }
    }

    fun getUnverifiedOrVerifiedList(isVerified: Boolean) {
        _unverifiedOrVerifiedComputerStoreList.value = Result.Loading
        administratorRepository.getUnverifiedOrVerifiedList(isVerified) {
            _unverifiedOrVerifiedComputerStoreList.value = it
        }
    }
}