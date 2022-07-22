package com.albar.computerstore.ui.fragments.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.albar.computerstore.R
import com.albar.computerstore.data.Result
import com.albar.computerstore.databinding.FragmentLocationBinding
import com.albar.computerstore.others.Constants
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.permissions.AppUtility
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import com.albar.computerstore.ui.viewmodels.NetworkViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LocationFragment : Fragment(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val networkStatusViewModel: NetworkViewModel by viewModels()

    private val viewModel: ComputerStoreViewModel by viewModels()

    private var currentLocation: Location? = null
    private var currentMarker: Marker? = null
    private var map: GoogleMap? = null
    private var isCurrentLocation = false
    private var isRequestingLocationUpdates = false

    private lateinit var setLocation: LatLng
    private var addressBasedOnLatLng: String = ""


    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networkStatus()
        requestPermission()
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // getting current latitude and longitude
        val latLng = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)



        map!!.isMyLocationEnabled = true

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
                //drawMarker(newLatLng)
            }

            override fun onMarkerDragStart(marker: Marker) {}
        })
    }

    private fun drawMarker(latLng: LatLng) {
        val titleText = if (!isCurrentLocation) "Your current coordinate" else "Your new coordinate"

        if (!isCurrentLocation) {
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title(titleText)
                .snippet(getAddress(latLng.latitude, latLng.longitude))
                .draggable(false)
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
            currentMarker = map?.addMarker(markerOptions)
            currentMarker?.showInfoWindow()

            setLocation = latLng

            isCurrentLocation = false
        } else {
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title(titleText)
                .snippet(getAddress(latLng.latitude, latLng.longitude))
                .draggable(true)
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
            currentMarker = map?.addMarker(markerOptions)
            currentMarker?.showInfoWindow()

            setLocation = latLng
        }

    }

    private fun getAddress(lat: Double, lng: Double): String? {
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, lng, 1)
        if (address.size > 0) {
            addressBasedOnLatLng = address[0].getAddressLine(0).toString()
            return address[0].getAddressLine(0).toString()
        }
        return ""
    }

    private fun retrieveData() {
        viewModel.getComputerStore()
        viewModel.computerStore.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.loading.show()
                }
                is Result.Error -> {
                    binding.loading.hide()
                    toastShort(it.error)
                }
                is Result.Success -> {
                    binding.loading.hide()
                    it.data.forEach {
                        val newLatLng = LatLng(it.lat, it.lng)
                        drawMarker(newLatLng)
                    }
                }
            }
        }
    }

    private fun noNetworkAvailableSign(isConnectionAvailable: Boolean) {
        if (!isConnectionAvailable) {
            binding.mapView.hide()
            binding.noDataLottie.show()
            binding.txtNoConnection.show()
        } else {
            binding.mapView.show()
            binding.noDataLottie.hide()
            binding.txtNoConnection.hide()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationRequest = LocationRequest.create()

        locationRequest.apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 1000L
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    if (location != null) {
                        currentLocation?.latitude
                        currentLocation?.longitude
//                        val updateLatLng =
//                            LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
//                        drawMarker(updateLatLng)
                    }
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback as LocationCallback,
            Looper.getMainLooper()
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestPermission() {
        if (AppUtility.hasLocationPermission(requireContext())) {
            val task = fusedLocationProviderClient.lastLocation

            task.addOnSuccessListener { location ->
                if (location != null) {
                    retrieveData()
                    isRequestingLocationUpdates = false
                    this.currentLocation = location
                    val mapFragment =
                        childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                } else {
                    startLocationUpdates()
                    isRequestingLocationUpdates = true
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

    private fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationProviderClient.removeLocationUpdates(
                it
            )
        }
    }

    private fun networkStatus() {
        networkStatusViewModel.hasConnection.observe(viewLifecycleOwner) { isConnected ->
            Timber.d("Ini Koneksi $isConnected")
            if (!isConnected) {
                Toast.makeText(requireContext(), "No internet connection !", Toast.LENGTH_SHORT)
                    .show()
                noNetworkAvailableSign(isConnected)
            } else {
                Toast.makeText(requireContext(), "Internet is Available", Toast.LENGTH_SHORT)
                    .show()
                noNetworkAvailableSign(isConnected)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isRequestingLocationUpdates) stopLocationUpdates()
    }

    override fun onResume() {
        if (!isRequestingLocationUpdates) startLocationUpdates()
        super.onResume()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
