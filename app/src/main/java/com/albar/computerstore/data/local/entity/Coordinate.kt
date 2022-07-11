package com.albar.computerstore.data.local.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coordinate(
    val lat: Double,
    val lng: Double,
    val address: String
) : Parcelable
