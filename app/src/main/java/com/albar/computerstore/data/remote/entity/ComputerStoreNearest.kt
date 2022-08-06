package com.albar.computerstore.data.remote.entity

import android.os.Parcelable
import com.albar.computerstore.others.Constants.NOT_SET
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class ComputerStoreNearest(
    var id: String = "",
    val name: String = NOT_SET,
    val address: String = NOT_SET,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val email: String = NOT_SET,
    val image: String = "",
    val phone: String = NOT_SET,
    val username: String = NOT_SET,
    val password: String = NOT_SET,
    @ServerTimestamp
    val createAt: Date = Date(),
    @field:JvmField
    val isVerified: Boolean = false,
    @field:JvmField
    val isAdmin: Boolean = false,
    val positionOrder: Double = 0.0
) : Parcelable