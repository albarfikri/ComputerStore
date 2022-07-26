package com.albar.computerstore.ui.dialogfragments

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.albar.computerstore.R
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker


class CustomInfoWindowGoogleMap(
    private val context: Context,
    private val glide: RequestManager
) : GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(p0: Marker): View? {
        val view = (context as AppCompatActivity)
            .layoutInflater
            .inflate(R.layout.layout_tooltip_marker, null)

        val tvComputerName = view.findViewById<TextView>(R.id.tv_nama_toko_komputer)
        val tvComputerAddress = view.findViewById<TextView>(R.id.iv_address)
        val tvImage = view.findViewById<ImageView>(R.id.iv_location)

        val infoWindow = p0.tag as ComputerStore
        tvComputerName.text = infoWindow.name
        tvComputerAddress.text = infoWindow.address

        glide
            .load(infoWindow.image)
            .placeholder(R.drawable.ic_broke_image)
            .transform(CenterCrop(), RoundedCorners(10))
            .into(tvImage)

        return view
    }

    override fun getInfoWindow(p0: Marker): View? {
        return null
    }

}