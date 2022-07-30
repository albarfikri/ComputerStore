package com.albar.computerstore.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.albar.computerstore.data.repository.GoogleMapsRepository

class LocationViewModel : ViewModel() {
    private val repo = GoogleMapsRepository()


    fun getDirection(url: String) = repo.getDirection(url)
}