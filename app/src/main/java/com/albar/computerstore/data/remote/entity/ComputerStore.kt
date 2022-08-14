package com.albar.computerstore.data.remote.entity

import android.os.Parcelable
import com.albar.computerstore.others.Constants.NOT_SET
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class ComputerStore(
    var id: String = "",
    var name: String = NOT_SET,
    var address: String = NOT_SET,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val open: String = "",
    val close: String = "",
    val area: String = "",
    val email: String = NOT_SET,
    var image: String = "",
    val phone: String = NOT_SET,
    val username: String = NOT_SET,
    val password: String = NOT_SET,
    @ServerTimestamp
    val createAt: Date = Date(),
    @field:JvmField
    val isVerified: Boolean = false,
    @field:JvmField
    val isAdmin: Boolean = false,
    var positionOrder: Double = 0.0,
    var distance: Double = 0.0
) : Parcelable