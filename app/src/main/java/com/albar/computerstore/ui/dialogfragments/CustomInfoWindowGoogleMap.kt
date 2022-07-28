package com.albar.computerstore.ui.dialogfragments

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.albar.computerstore.R
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
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

        val tvComputerName = view.findViewById<TextView>(R.id.tv_computer_store_name)
        val tvComputerAddress = view.findViewById<TextView>(R.id.iv_address)
        val tvComputerDistance = view.findViewById<TextView>(R.id.tv_computer_store_distance)
        val tvImage = view.findViewById<ImageView>(R.id.iv_location)
        val infoWindow = p0.tag as ComputerStore
        val requestOption = RequestOptions.overrideOf(100, 80)
        glide
            .asBitmap()
            .load(infoWindow.image)
            .placeholder(R.drawable.ic_broke_image)
            .apply(requestOption)
            .transform(CenterCrop(), RoundedCorners(10))
            .into(tvImage)
        tvComputerName.text = infoWindow.name
        tvComputerAddress.text = infoWindow.address
        tvComputerDistance.text = "± ${infoWindow.distance} km dari lokasi anda"



        return view
    }

    override fun getInfoWindow(p0: Marker): View? {
        return null
    }

}