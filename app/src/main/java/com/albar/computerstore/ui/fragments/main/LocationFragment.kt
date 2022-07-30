package com.albar.computerstore.ui.fragments.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.AsyncTask
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
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.data.remote.entity.MapData
import com.albar.computerstore.databinding.FragmentLocationBinding
import com.albar.computerstore.others.Constants
import com.albar.computerstore.others.Formula.haversineFormula
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.permissions.AppUtility
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.dialogfragments.CustomInfoWindowGoogleMap
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import com.albar.computerstore.ui.viewmodels.NetworkViewModel
import com.bumptech.glide.RequestManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LocationFragment : Fragment(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks {
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

    private var currentLocation: Location? = null
    private var destinationLocation: Location? = null

    private var currentMarker: Marker? = null
    private var map: GoogleMap? = null
    private var isCurrentLocation = false
    private var isRequestingLocationUpdates = false

    private lateinit var setLocation: LatLng

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

        map!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));

        map!!.isMyLocationEnabled = true
        map?.setPadding(0, 0, 0, 125)
        map?.uiSettings?.setAllGesturesEnabled(true)
        map?.uiSettings?.isMyLocationButtonEnabled = true
        map?.uiSettings?.isZoomGesturesEnabled = true
        map?.uiSettings?.isMapToolbarEnabled = true
        map?.uiSettings?.isZoomControlsEnabled = true

        // get coordinates by dragging marker
        map!!.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(marker: Marker) {}

            override fun onMarkerDragEnd(marker: Marker) {
                // if marker is dragged, remove marker and add the new one
                if (currentMarker != null) {
                    currentMarker?.remove()
                }

                isCurrentLocation = true
            }

            override fun onMarkerDragStart(marker: Marker) {}
        })

//        map!!.setOnMarkerClickListener {
//        }
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


            val ai: ApplicationInfo = requireActivity().packageManager
                .getApplicationInfo(requireActivity().packageName, PackageManager.GET_META_DATA)
            val value = ai.metaData["com.google.android.geo.API_KEY"]
            val apiKey = value.toString()

            if (!Places.isInitialized()) {
                Places.initialize(requireActivity(), apiKey)
            }

            val origin = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            val destination = LatLng(data.lat, data.lng)
            val url = getDirectionURL(origin, destination, apiKey)
            GetDirection(url).execute()

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
            currentMarker?.showInfoWindow()

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


    private fun getDirectionURL(origin: LatLng, dest: LatLng, secret: String): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url: String) :
        AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body().toString()

            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, MapData::class.java)
                val path = ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices) {
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.GREEN)
                lineoption.geodesic(true)
            }
            map?.addPolyline(lineoption)
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
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
