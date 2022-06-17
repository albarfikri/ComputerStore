package com.albar.computerstore.ui.viewmodels

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
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

    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        .build()

    private fun isConnectionAvailable(): MutableLiveData<Boolean> {
        _hasConnection.postValue(false)
        connectivityManager.requestNetwork(networkRequest, networkCallback)
        return _hasConnection
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            _hasConnection.postValue(true)
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            _hasConnection.postValue(false)
        }
    }
}

