package com.albar.computerstore.ui.fragments.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
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
import com.albar.computerstore.others.*
import com.albar.computerstore.others.Formula.haversineFormula
import com.albar.computerstore.others.Tools.decode
import com.albar.computerstore.others.permissions.AppUtility
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
    private var position: Double = 0.0
    private var onceOnLaunchDraw: Boolean = true
    var nearest = LatLng(0.0, 0.0)

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
        map?.apply {
            setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            isMyLocationEnabled = true
            setPadding(0, 0, 0, 125)
            uiSettings.setAllGesturesEnabled(true)
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isZoomGesturesEnabled = true
            uiSettings.isMapToolbarEnabled = true
            uiSettings.isZoomControlsEnabled = true
        }
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
                    val listAfterCalculating = arrayListOf<ComputerStore>()
                    binding.loading.hide()
                    if (it.data.isNotEmpty()) {
                        drawMarker(it.data)
                        for (output in it.data) {
                            val availableArea = resources.getStringArray(R.array.computerStoreArea)
                            val computerAreaFromAPI = output.area

                            position = Formula.finalOutput(
                                availableArea,
                                computerAreaFromAPI,
                                // Distance
                                haversineFormula(
                                    currentLocation!!.latitude,
                                    currentLocation!!.longitude,
                                    output.lat,
                                    output.lng
                                )
                            )
                            val dataObject = ComputerStore(
                                id = output.id,
                                isAdmin = output.isAdmin,
                                isVerified = output.isVerified,
                                lat = output.lat,
                                lng = output.lng,
                                createAt = output.createAt,
                                name = output.name,
                                address = output.address,
                                image = output.image,
                                phone = output.phone,
                                email = output.email,
                                username = output.username,
                                password = output.password,
                                positionOrder = position
                            )
                            listAfterCalculating.add(dataObject)
                        }

                        val findMinOutput = findMin(listAfterCalculating)

                        if (onceOnLaunchDraw) {
                            nearest = LatLng(findMinOutput.lat, findMinOutput.lng)
                            drawLine(nearest)
                            onceOnLaunchDraw = false
                        }
                    } else {
                        toastShort("Have no computer store data!")
                    }
                }
            }
        }
    }

    private fun findMin(list: List<ComputerStore>): ComputerStore {
        return list.sortedWith(compareBy { it.positionOrder }).first()
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
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker))

            currentMarker = map?.addMarker(markerOptions)

            val customInfoWindow = CustomInfoWindowGoogleMap(requireActivity(), glide)
            map?.setInfoWindowAdapter(customInfoWindow)
            currentMarker?.tag = info

            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(data.lat, data.lng), 13.5f))

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
            interval = 3000L
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
        if (AppUtility.hasLocationPermission(requireActivity())) {
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
                    toastShort("Your gps is disabled")
                    startLocationUpdates()
                    isRequestingLocationUpdates = true
                }
            }
            return
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
            noNetworkAvailableSign(isConnected)
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        val destination = LatLng(p0.position.latitude, p0.position.longitude)
        drawLine(destination)
        return false
    }

    private fun drawLine(destination: LatLng) {
        val stepList: MutableList<LatLng> = ArrayList()
        val options = PolylineOptions().apply {
            width(15f)
            color(
                resources.getColor(
                    R.color.path,
                    context?.theme
                )
            )
            geodesic(true)
            clickable(true)
            visible(true)
        }

        polylineFinal?.remove()

        val ai: ApplicationInfo = requireContext().packageManager
            .getApplicationInfo(requireContext().packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()

        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + currentLocation?.latitude + "," + currentLocation?.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&mode=driving" +
                "&key=" + apiKey

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
                    }
                }
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
