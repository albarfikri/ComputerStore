package com.albar.computerstore.data.remote.entity

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class ComputerStoreProduct(
    var id: String = "",
    var idComputerStore: String = "",
    val productName: String = "Not set",
    val productType: String = "Not set",
    val productPrice: String = "Not set",
    val productImage: String = "Not set",
    val productSpecification: String = "Not set",
    val unit: Int = 0,
    val isStockAvailable: Boolean = false,
    @ServerTimestamp
    val createAt: Date = Date(),
) : Parcelable