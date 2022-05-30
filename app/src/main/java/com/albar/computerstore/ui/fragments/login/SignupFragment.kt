package com.albar.computerstore.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.databinding.FragmentSignupBinding
import com.albar.computerstore.ui.dialogfragments.CustomDialogSearchlatlngFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

class SignupFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signIn.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_signinFragment)
        }

        binding.btnGetLatLng.setOnClickListener {
            var dialog = CustomDialogSearchlatlngFragment()

            dialog.show(parentFragmentManager, "CustomDialogSearchlatlngFragment")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {

    }
}