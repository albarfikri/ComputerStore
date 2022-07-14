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

    private val _computerStoreProduct = MutableLiveData<Result<List<ComputerStoreProduct>>>()
    val computerStoreProduct: LiveData<Result<List<ComputerStoreProduct>>>
        get() = _computerStoreProduct

    private val _getProductByType = MutableLiveData<Result<List<ComputerStoreProduct>>>()
    val getProductByType: LiveData<Result<List<ComputerStoreProduct>>>
        get() = _getProductByType

    fun getComputerStoreProduct() {
        _computerStoreProduct.value = Result.Loading

    }

    // Auth Repository
    fun addData(computerStoreProduct: ComputerStoreProduct) {
        _addData.value = Result.Loading
        computerStoreProductRepository.addComputerStoreProduct(computerStoreProduct) {
            _addData.value = it
        }
    }

    fun uploadImage(fileUri: Uri, onResult: (Result<Uri>) -> Unit) {
        onResult.invoke(Result.Loading)
        viewModelScope.launch {
            computerStoreRepository.uploadImage(fileUri, onResult)
        }
    }

    fun getProductByType(type: String) {
        _getProductByType.value = Result.Loading
        computerStoreProductRepository.getProductByType(type) {
            _getProductByType.value = it
        }

    }
}