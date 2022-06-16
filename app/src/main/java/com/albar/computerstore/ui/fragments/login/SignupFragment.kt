package com.albar.computerstore.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.data.local.entity.Coordinate
import com.albar.computerstore.databinding.FragmentSignupBinding
import com.albar.computerstore.others.Constants.BUNDLE_KEY
import com.albar.computerstore.others.Constants.REQUEST_KEY
import com.albar.computerstore.ui.dialogfragments.CustomDialogSearchlatlngFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment : Fragment(), OnMapReadyCallback {

    companion object {
        const val EXTRA_COORDINATE = "extra_coordinate"
    }

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap

    private var latValue: String? = null
    private var lngValue: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        setFragmentListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signIn.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_signinFragment)
        }

        binding.btnGetLatLng.setOnClickListener {
            val dialog = CustomDialogSearchlatlngFragment()
            dialog.show(
                childFragmentManager,
                CustomDialogSearchlatlngFragment::class.java.simpleName
            )
        }
    }

    private fun setFragmentListener() {
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val result = bundle.getParcelable<Coordinate>(BUNDLE_KEY) as Coordinate

            latValue = result.lat.toString()
            lngValue = result.lng.toString()

            binding.etLat.setText(latValue)
            binding.etLng.setText(lngValue)

            Toast.makeText(activity, "$latValue, $lngValue", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {

    }
}