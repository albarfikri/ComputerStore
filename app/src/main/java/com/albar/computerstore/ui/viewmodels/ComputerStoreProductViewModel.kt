package com.albar.computerstore.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStoreProduct
import com.albar.computerstore.data.repository.ComputerStoreProductRepository
import com.albar.computerstore.data.repository.ComputerStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComputerStoreProductViewModel @Inject constructor(
    private val computerStoreProductRepository: ComputerStoreProductRepository,
    private val computerStoreRepository: ComputerStoreRepository
) : ViewModel() {

    private val _addData = MutableLiveData<Result<String>>()
    val addData: LiveData<Result<String>>
        get() = _addData

    private val _getProductByType = MutableLiveData<Result<List<ComputerStoreProduct>>>()
    val getProductByType: LiveData<Result<List<ComputerStoreProduct>>>
        get() = _getProductByType

    private val _getProductById = MutableLiveData<Result<ComputerStoreProduct>>()
    val getProductById: LiveData<Result<ComputerStoreProduct>>
        get() = _getProductById

    private val _getProductByName = MutableLiveData<Result<List<ComputerStoreProduct>>>()
    val getProductByName: LiveData<Result<List<ComputerStoreProduct>>>
        get() = _getProductByName

    private val _updateComputerStoreProduct = MutableLiveData<Result<String>>()
    val updateComputerStoreProduct: LiveData<Result<String>>
        get() = _updateComputerStoreProduct

    private val _deleteComputerStoreProduct = MutableLiveData<Result<String>>()
    val deleteComputerStoreProduct: LiveData<Result<String>>
        get() = _deleteComputerStoreProduct


    fun addData(computerStoreProduct: ComputerStoreProduct) {
        _addData.value = Result.Loading
        computerStoreProductRepository.addComputerStoreProduct(computerStoreProduct) {
            _addData.value = it
        }
    }

    fun deleteData(computerStoreProduct: ComputerStoreProduct) {
        _deleteComputerStoreProduct.value = Result.Loading
        computerStoreProductRepository.deleteComputerStoreProduct(computerStoreProduct) {
            _deleteComputerStoreProduct.value = it
        }
    }

    fun updateData(computerStoreProduct: ComputerStoreProduct) {
        _updateComputerStoreProduct.value = Result.Loading
        computerStoreProductRepository.updateComputerStoreProduct(computerStoreProduct) {
            _updateComputerStoreProduct.value = it
        }
    }

    fun uploadImage(fileUri: Uri, onResult: (Result<Uri>) -> Unit) {
        onResult.invoke(Result.Loading)
        viewModelScope.launch {
            computerStoreRepository.uploadImage(fileUri, onResult)
        }
    }

    fun getProductByType(idComputerStore: String, type: String) {
        _getProductByType.value = Result.Loading
        computerStoreProductRepository.getProductByType(idComputerStore, type) {
            _getProductByType.value = it
        }
    }

    fun getProductById(id: String) {
        _getProductById.value = Result.Loading
        computerStoreProductRepository.getProductById(id) {
            _getProductById.value = it
        }
    }

    fun getProductByName(productName: String) {
        _getProductByName.value = Result.Loading
        computerStoreProductRepository.getProductByName(productName) {
            _getProductByName.value = it
        }
    }
}