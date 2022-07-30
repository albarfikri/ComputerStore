package com.albar.computerstore.data.remote.entity

import com.squareup.moshi.Json

data class DirectionPolylineModel(
    @field:Json(name="points")

    var points: String? = null
)