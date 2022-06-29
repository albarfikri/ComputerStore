package com.albar.computerstore.data.remote.entity

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class ComputerStore(
    var id: String = "",
    val name: String = "Not Set",
    val address: String = "Not Set",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val image: String = "",
    val phone: String = "Not set",
    val username: String = "Not set",
    val password: String = "Not set",
    @ServerTimestamp
    val createAt: Date = Date(),
    val isVerified: Boolean = false
    //val computerStoreData: List<ComputerStoreData>? = null
) : Parcelable