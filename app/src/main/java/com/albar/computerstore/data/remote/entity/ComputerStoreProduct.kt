package com.albar.computerstore.data.remote.entity

import android.os.Parcelable
import com.albar.computerstore.others.Constants.NOT_SET
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class ComputerStoreProduct(
    var id: String = "",
    var idComputerStore: String = "",
    val productName: String = NOT_SET,
    val productType: String = NOT_SET,
    val productPrice: String = NOT_SET,
    val productImage: String = NOT_SET,
    val productSpecification: String = NOT_SET,
    val unit: Int = 0,
    val isStockAvailable: Boolean = false,
    @ServerTimestamp
    val createAt: Date = Date(),
) : Parcelable