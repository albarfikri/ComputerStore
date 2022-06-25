package com.albar.computerstore.data.remote.entity

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class ComputerStore(
    var id: String = "",
    val name: String = "",
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val image: String = "",
    val username: String = "",
    val password: String = "",
    @ServerTimestamp
    val createAt: Date = Date(),
    val isVerified: Boolean = false
) : Parcelable