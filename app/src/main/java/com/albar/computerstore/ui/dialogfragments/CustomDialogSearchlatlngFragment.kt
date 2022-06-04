package com.albar.computerstore.ui.dialogfragments

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.albar.computerstore.R
import com.albar.computerstore.databinding.FragmentCustomDialogSearchlatlngBinding
import com.albar.computerstore.others.AppUtility
import com.albar.computerstore.others.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class CustomDialogSearchlatlngFragment : DialogFragment(), OnMapReadyCallback,
    EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentCustomDialogSearchlatlngBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private var currentMarker: Marker? = null

    private var map: GoogleMap? = null

    private var isCurrentLocation: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomDialogSearchlatlngBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        requestPermission()

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

//        val location = LatLng(PEKANBARU_LATITUDE, PEKANBARU_LONGITUDE)
//
//        map!!.addMarker(
//            MarkerOptions()
//                .position(location)
//                .title("Gg. Al Khalish")
//                .draggable(true)
//        )
//        map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

        // getting current latitude and longitude
        val latLng = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)
        drawMarker(latLng)

        // get coordinates by dragging marker
        map!!.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(marker: Marker) {

            }

            override fun onMarkerDragEnd(marker: Marker) {
                // if marker is dragged, remove marker and add the new one
                if (currentMarker != null) {
                    currentMarker?.remove()
                }

                isCurrentLocation = true
                // adding new coordinates from the new marker
                val newLatLng = LatLng(marker.position.latitude, marker.position.longitude)
                drawMarker(newLatLng)
            }

            override fun onMarkerDragStart(marker: Marker) {

            }

        })
    }

    private fun drawMarker(latLng: LatLng) {
        val titleText = if (!isCurrentLocation) {
            "Your current coordinate"
        } else {
            "Your new coordinate"
        }

        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(titleText)
            .snippet(getAddress(latLng.latitude, latLng.longitude))
            .draggable(true)

        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        currentMarker = map?.addMarker(markerOptions)
        currentMarker?.showInfoWindow()
        isCurrentLocation = false
    }

    private fun getAddress(lat: Double, lng: Double): String? {
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, lng, 1)
        if (address.size > 0) {
            return address[0].getAddressLine(0).toString()
        }
        return ""
    }

    @SuppressLint("MissingPermission")
    private fun requestPermission() {
        if (AppUtility.hasLocationPermission(requireContext())) {
            val task = fusedLocationProviderClient.lastLocation

            task.addOnSuccessListener { location ->
                if (location != null) {
                    this.currentLocation = location
                    val mapFragment =
                        childFragmentManager.findFragmentById(R.id.mapViewSignUp) as SupportMapFragment

                    mapFragment.getMapAsync(this)
                }
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this, "You need to accept to location permissions to use this app.",
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept to location permissions to use this app.",
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(requireContext(), "accepted", Toast.LENGTH_SHORT).show()
        if (requestCode == Constants.REQUEST_CODE_LOCATION_PERMISSION) {
            return
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            requestPermission()
        }
    }
}