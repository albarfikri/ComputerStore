package com.albar.computerstore.ui.dialogfragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.albar.computerstore.R
import com.albar.computerstore.databinding.FragmentCustomDialogSearchlatlngBinding
import com.albar.computerstore.others.Constants.PEKANBARU_LATITUDE
import com.albar.computerstore.others.Constants.PEKANBARU_LONGITUDE
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber

class CustomDialogSearchlatlngFragment : DialogFragment(), OnMapReadyCallback {
    private var _binding: FragmentCustomDialogSearchlatlngBinding? = null
    private val binding get() = _binding!!

    private var map: GoogleMap? = null

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

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapViewSignUp) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val location = LatLng(PEKANBARU_LATITUDE, PEKANBARU_LONGITUDE)

        map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}