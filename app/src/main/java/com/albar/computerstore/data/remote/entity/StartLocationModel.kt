package com.albar.computerstore.data.remote.entity

import com.squareup.moshi.Json

data class StartLocationModel(
    @field:Json(name = "lat")
    var lat: Double? = null,

    @field:Json(name = "lng")
    var lng: Double? = null
)