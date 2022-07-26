package com.albar.computerstore.data.remote.entity

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class ComputerStore(
    var id: String = "",
    var name: String = "Not set",
    var address: String = "Not set",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val area: String = "",
    val email: String = "Not set",
    var image: String = "",
    val phone: String = "Not set",
    val username: String = "Not set",
    val password: String = "Not set",
    @ServerTimestamp
    val createAt: Date = Date(),
    @field:JvmField
    val isVerified: Boolean = false,
    @field:JvmField
    val isAdmin: Boolean = false,
    val positionOrder: Double = 0.0,
    var distance: Double = 0.0
    //val computerStoreData: List<ComputerStoreData>? = null
) : Parcelable