package com.albar.computerstore.data.remote.entity

import com.squareup.moshi.Json

data class LocationModel(
    @field:Json(name = "location")
    val location: LocationModel?
)