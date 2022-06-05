package com.albar.computerstore.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coordinate(
    val lat: Double,
    val lng: Double
) : Parcelable
