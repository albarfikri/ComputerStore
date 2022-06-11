package com.albar.computerstore.ui.viewmodels

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(private val connectivityManager: ConnectivityManager) : ViewModel() {

    private val _hasConnection = MutableLiveData<Boolean>()

    val hasConnection: LiveData<Boolean>
        get() = isConectionAvailable()

    private fun isConectionAvailable(): MutableLiveData<Boolean> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.run {
                getNetworkCapabilities(activeNetwork)?.run {
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                }
            }
            _hasConnection.value = true
        } else {
            _hasConnection.value = false
        }
        return _hasConnection
    }
}

