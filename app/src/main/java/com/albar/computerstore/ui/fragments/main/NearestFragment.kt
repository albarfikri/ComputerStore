package com.albar.computerstore.ui.fragments.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.databinding.FragmentNearestBinding
import com.albar.computerstore.others.Constants
import com.albar.computerstore.others.Formula.euclideanFormula
import com.albar.computerstore.others.Formula.finalOutput
import com.albar.computerstore.others.Formula.haversineFormula
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.permissions.AppUtility
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.adapter.ComputerStoreNearestAdapter
import com.albar.computerstore.ui.fragments.detail.DetailComputerStoreFragment
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import com.albar.computerstore.ui.viewmodels.NetworkViewModel
import com.bumptech.glide.RequestManager
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NearestFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentNearestBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val networkStatusViewModel: NetworkViewModel by viewModels()

    private val viewModel: ComputerStoreViewModel by viewModels()

    private var currentLocation: Location? = null
    private var isRequestingLocationUpdates = false

    private var addressBasedOnLatLng: String = ""

    private var originLatitude: Double = 0.0
    private var originLongitude: Double = 0.0
    private var position: Double = 0.0
    private var distance: Double = 0.0
    private var isHaversine: Boolean = true


    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null

    @Inject
    lateinit var glide: RequestManager

    private var listAfterCalculating = arrayListOf<ComputerStore>()

    private val adapter by lazy {
        ComputerStoreNearestAdapter(
            onItemClicked = { _, item ->
                findNavController().navigate(R.id.action_nearest_to_detailList, Bundle().apply {
                    putBoolean(DetailComputerStoreFragment.D0_CALL_OR_VERIFIED_USER, false)
                    putString(Constants.KEY, DetailComputerStoreFragment.DETAIL_CLICKED)
                    putParcelable(Constants.PARCELABLE_KEY, item)
                })
            },
            onCallClicked = { _, item ->
                val dialPhoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$item"))
                startActivity(dialPhoneIntent)
            },
            onNavigate = { _, _ -> },
            glide
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNearestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvComputerList.adapter = adapter
        requestPermission()
        networkStatus()
    }

    private fun networkStatus() {
        networkStatusViewModel.hasConnection.observe(viewLifecycleOwner) { isConnected ->
            Timber.d("Ini Koneksi $isConnected")
            if (!isConnected) {
                Toast.makeText(requireContext(), "No internet connection !", Toast.LENGTH_SHORT)
                    .show()
                noNetworkAvailableSign(isConnected)
                dataAvailableCheck(true)
            } else {
                Toast.makeText(requireContext(), "Internet is Available", Toast.LENGTH_SHORT)
                    .show()
                noNetworkAvailableSign(isConnected)
                methodType()
                retrieveData()
            }
        }
    }

    private fun noNetworkAvailableSign(isConnectionAvailable: Boolean) {
        if (!isConnectionAvailable) {
            binding.apply {
                stopLocationUpdates()
                rvComputerList.hide()
                noNetwork.show()
            }
        } else {
            binding.apply {
                noNetwork.hide()
            }
        }
    }

    private fun dataAvailableCheck(isAvailable: Boolean) {
        if (!isAvailable) {
            binding.apply {
                noDataLottie.show()
                noDataDescription.show()
            }
        } else {
            binding.apply {
                noDataLottie.hide()
                noDataDescription.hide()
            }
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
                    currentLocation?.latitude
                    currentLocation?.longitude
                    binding.tvLat.text = currentLocation?.latitude.toString()
                    binding.tvLon.text = currentLocation?.longitude.toString()
                    binding.tvPosition.text =
                        getAddress(currentLocation!!.latitude, currentLocation!!.longitude)
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

    private fun methodType() {
        binding.spMethodType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ) {
                    when (p2) {
                        0 -> isHaversine = true
                        1 -> isHaversine = false
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
    }

    private fun retrieveData() {
        viewModel.getComputerStore()
        viewModel.computerStore.observe(requireActivity()) { it ->
            when (it) {
                is Result.Loading -> {
                    listAfterCalculating.clear()
                    binding.loading.show()
                }
                is Result.Error -> {
                    binding.loading.hide()
                    toastShort(it.error)
                }
                is Result.Success -> {
                    binding.loading.hide()
                    it.data.forEach { output ->
                        val availableArea = resources.getStringArray(R.array.computerStoreArea)
                        val computerAreaFromAPI = output.area

                        if (isHaversine) {
                            position = finalOutput(
                                availableArea,
                                computerAreaFromAPI,
                                // Distance
                                haversineFormula(
                                    originLatitude,
                                    originLongitude,
                                    output.lat,
                                    output.lng
                                )
                            )
                            distance = haversineFormula(
                                originLatitude,
                                originLongitude,
                                output.lat,
                                output.lng
                            )
                        } else {
                            position = finalOutput(
                                availableArea,
                                computerAreaFromAPI,
                                // Distance
                                euclideanFormula(
                                    originLatitude,
                                    originLongitude,
                                    output.lat,
                                    output.lng
                                )
                            )
                            distance = euclideanFormula(
                                originLatitude,
                                originLongitude,
                                output.lat,
                                output.lng
                            )
                        }

                        val data = ComputerStore(
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
                            positionOrder = position,
                            distance = distance
                        )
                        listAfterCalculating.add(data)
                    }
                    adapter.updateList(listAfterCalculating.distinctBy { it.id }
                        .sortedBy { it.positionOrder }.toMutableList())
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }


    private fun getAddress(lat: Double, lng: Double): String {
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, lng, 1)
        if (address.size > 0) {
            addressBasedOnLatLng = address[0].getAddressLine(0).toString()
            return address[0].getAddressLine(0).toString()
        }
        return ""
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationRequest = LocationRequest.create()

        locationRequest.apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 5000L
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    if (location != null) {
                        originLatitude = location.latitude
                        originLongitude = location.longitude

                        binding.tvLat.text = location.latitude.toString()
                        binding.tvLon.text = location.longitude.toString()
                        binding.tvPosition.text =
                            getAddress(location.latitude, location.longitude)
                        retrieveData()
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