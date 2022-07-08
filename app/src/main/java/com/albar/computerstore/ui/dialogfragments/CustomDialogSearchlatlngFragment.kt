package com.albar.computerstore.ui.dialogfragments

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.albar.computerstore.R
import com.albar.computerstore.data.local.entity.Coordinate
import com.albar.computerstore.databinding.FragmentCustomDialogSearchlatlngBinding
import com.albar.computerstore.others.Constants
import com.albar.computerstore.others.Constants.BUNDLE_KEY
import com.albar.computerstore.others.Constants.REQUEST_KEY
import com.albar.computerstore.others.permissions.AppUtility
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CustomDialogSearchlatlngFragment : DialogFragment(), OnMapReadyCallback,
    EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentCustomDialogSearchlatlngBinding? = null
    private val binding get() = _binding!!

    private var currentLocation: Location? = null
    private var currentMarker: Marker? = null
    private var map: GoogleMap? = null
    private var isCurrentLocation = false
    private var isRequestingLocationUpdates = false

    private lateinit var setLocation: LatLng

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null

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
        requestPermission()
        actionButton()
        Toast.makeText(
            requireContext(),
            "Location ${currentLocation?.latitude} and ${currentLocation?.longitude}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun actionButton() {
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding.setCoordinate.setOnClickListener {
            Timber.d("Coordinate $setLocation")

            val coordinate = Coordinate(
                setLocation.latitude,
                setLocation.longitude
            )

            parentFragment?.setFragmentResult(
                REQUEST_KEY, bundleOf(
                    BUNDLE_KEY to coordinate
                )
            )
            dismiss()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationRequest = LocationRequest.create()

        locationRequest.apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 2000L
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    if (location != null) {
                        currentLocation?.latitude
                        currentLocation?.longitude
                    }
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback as LocationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationProviderClient.removeLocationUpdates(
                it
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestPermission() {
        if (AppUtility.hasLocationPermission(requireContext())) {
            val task = fusedLocationProviderClient.lastLocation

            task.addOnSuccessListener { location ->
                if (location != null) {
                    isRequestingLocationUpdates = false
                    this.currentLocation = location
                    val mapFragment =
                        childFragmentManager.findFragmentById(R.id.mapViewSignUp) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                    isBtnEnabledWhileGpsDisabled(true)
                } else {
                    isRequestingLocationUpdates = true
                    isBtnEnabledWhileGpsDisabled(false)

                    view?.let {
                        Snackbar.make(
                            it,
                            "Turn on gps, close and open again",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }
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

    private fun isBtnEnabledWhileGpsDisabled(status: Boolean) {
        binding.setCoordinate.isEnabled = status
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // getting current latitude and longitude
        val latLng = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)

        drawMarker(latLng)

        // get coordinates by dragging marker
        map!!.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(marker: Marker) {}

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

            override fun onMarkerDragStart(marker: Marker) {}
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

        setLocation = latLng

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

    override fun onPause() {
        super.onPause()
        if (!isRequestingLocationUpdates) stopLocationUpdates()
    }

    override fun onResume() {
        if (!isRequestingLocationUpdates) startLocationUpdates()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
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