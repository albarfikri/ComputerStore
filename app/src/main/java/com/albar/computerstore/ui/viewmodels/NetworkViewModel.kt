package com.albar.computerstore.ui.viewmodels

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(private val connectivityManager: ConnectivityManager) :
    ViewModel() {

    private var _hasConnection = MutableLiveData<Boolean>()

    val hasConnection: LiveData<Boolean>
        get() = isConnectionAvailable()

    private fun isConnectionAvailable(): MutableLiveData<Boolean> {
        var result= false
        connectivityManager.run {
            getNetworkCapabilities(activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
        _hasConnection.postValue(result)
        return _hasConnection
    }
}

