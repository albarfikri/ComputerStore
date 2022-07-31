package com.albar.computerstore.ui.fragments.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
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
import androidx.lifecycle.lifecycleScope
import com.albar.computerstore.R
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.data.remote.entity.DirectionLegModel
import com.albar.computerstore.data.remote.entity.DirectionResponseModel
import com.albar.computerstore.data.remote.entity.DirectionRouteModel
import com.albar.computerstore.databinding.FragmentLocationBinding
import com.albar.computerstore.others.Constants
import com.albar.computerstore.others.Formula.haversineFormula
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.permissions.AppUtility
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.dialogfragments.CustomInfoWindowGoogleMap
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import com.albar.computerstore.ui.viewmodels.LocationViewModel
import com.albar.computerstore.ui.viewmodels.NetworkViewModel
import com.bumptech.glide.RequestManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LocationFragment : Fragment(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks,
    GoogleMap.OnMarkerClickListener {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val networkStatusViewModel: NetworkViewModel by viewModels()

    private val viewModel: ComputerStoreViewModel by viewModels()

    private val locationViewModel: LocationViewModel by viewModels()

    private var currentLocation: Location? = null

    private var currentMarker: Marker? = null
    private var map: GoogleMap? = null
    private var isRequestingLocationUpdates = false

    private lateinit var setLocation: LatLng

    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
    private var polylineFinal: Polyline? = null

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

        map!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));

        map!!.isMyLocationEnabled = true
        map?.setPadding(0, 0, 0, 125)
        map?.uiSettings?.setAllGesturesEnabled(true)
        map?.uiSettings?.isMyLocationButtonEnabled = true
        map?.uiSettings?.isZoomGesturesEnabled = true
        map?.uiSettings?.isMapToolbarEnabled = true
        map?.uiSettings?.isZoomControlsEnabled = true
        map?.setOnMarkerClickListener(this)
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
                    if (it.data.isNotEmpty()) {
                        drawMarker(it.data)
                    } else {
                        toastShort("Have no computer store data!")
                    }
                }
            }
        }
    }

    private fun drawMarker(computerStoreData: List<ComputerStore>) {
        for (data in computerStoreData) {
            val info = ComputerStore()
            val distance = haversineFormula(
                currentLocation!!.latitude,
                currentLocation!!.longitude,
                data.lat,
                data.lng
            )

            info.name = data.name
            info.address = data.address
            info.image = data.image
            info.distance = distance

            val markerOptions = MarkerOptions()
                .position(LatLng(data.lat, data.lng))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

            currentMarker = map?.addMarker(markerOptions)

            val customInfoWindow = CustomInfoWindowGoogleMap(requireActivity(), glide)
            map?.setInfoWindowAdapter(customInfoWindow)

            currentMarker?.tag = info

            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(data.lat, data.lng), 12f))

            setLocation = LatLng(data.lat, data.lng)
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

    override fun onMarkerClick(p0: Marker): Boolean {
        val stepList: MutableList<LatLng> = ArrayList()
        val options = PolylineOptions().apply {
            width(15f)
            color(
                resources.getColor(
                    R.color.search,
                    context?.theme
                )
            )
            geodesic(true)
            clickable(true)
            visible(true)
        }

        polylineFinal?.remove()

        val key = "AIzaSyAkmHpMq2iqx_Em1tP0P6tmA-wS3ePNaWM"
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + currentLocation?.latitude + "," + currentLocation?.longitude +
                "&destination=" + p0.position.latitude + "," + p0.position.longitude +
                "&mode=driving" +
                "&key=" + key

        lifecycleScope.launchWhenCreated {
            locationViewModel.getDirection(url).collect {
                when (it) {
                    is Result.Loading -> {
                        binding.loading.show()
                    }

                    is Result.Success -> {
                        binding.loading.hide()
                        val directionResponseModel: DirectionResponseModel =
                            it.data as DirectionResponseModel

                        val routeModel: DirectionRouteModel =
                            directionResponseModel.directionRouteModels!![0]


                        val legModel: DirectionLegModel = routeModel.legs?.get(0)!!


                        val pattern: List<PatternItem>
                        pattern = listOf(Dash(30f))

                        options.pattern(pattern)
                        for (stepModel in legModel.steps!!) {
                            val decodeList = decode(stepModel.polyline?.points!!)
                            for (latLng in decodeList) {
                                stepList.add(
                                    LatLng(
                                        latLng.latitude,
                                        latLng.longitude
                                    )
                                )
                            }
                        }

                        options.addAll(stepList)
                        polylineFinal = map?.addPolyline(options)
                        val startLocation = LatLng(
                            legModel.startLocation?.lat!!,
                            legModel.startLocation.lng!!
                        )

                        val endLocation = LatLng(
                            legModel.endLocation?.lat!!,
                            legModel.endLocation.lng!!
                        )

                        val builder = LatLngBounds.builder()
                        builder.include(endLocation).include(startLocation)
                        val latLngBounds = builder.build()

                        map?.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                latLngBounds, 32
                            )
                        )
                    }
                }
            }
        }

        return false
    }

    private fun decode(points: String): List<LatLng> {
        val len = points.length
        val path: MutableList<LatLng> = java.util.ArrayList(len / 2)
        var index = 0
        var lat = 0
        var lng = 0
        while (index < len) {
            var result = 1
            var shift = 0
            var b: Int
            do {
                b = points[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            result = 1
            shift = 0
            do {
                b = points[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            path.add(LatLng(lat * 1e-5, lng * 1e-5))
        }
        return path
    }
}
