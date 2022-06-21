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
import com.albar.computerstore.others.toast
import com.albar.computerstore.ui.dialogfragments.CustomDialogSearchlatlngFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private var latValue: String? = null
    private var lngValue: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        setFragmentListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signIn.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_signinFragment)
        }


        openDialogFragment()
        checkIfFieldEmpty()
    }

    private fun openDialogFragment() {
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

    private fun checkIfFieldEmpty() {

        binding.apply {
            btnSignup.setOnClickListener {
                var isEmptyFields = false

                val inputStoreName = etStoreName.text.toString().trim()
                val inputAddress = etAddress.text.toString().trim()

                // boolean type convert to string for checking space
                val inputLat = etLat.text.toString().trim()
                val inputLng = etLng.text.toString().trim()

                val inputUsername = etUsername.text.toString().trim()
                val inputPassword = etPassword.text.toString().trim()


                if (inputStoreName.isEmpty()) {
                    isEmptyFields = true
                    etStoreName.error = "Store Name Field cannot be empty"
                }
                if (inputAddress.isEmpty()) {
                    isEmptyFields = true
                    etAddress.error = "Address Field cannot be empty"
                }
                if (inputLat.isEmpty()) {
                    isEmptyFields = true
                    etLat.error = "Latitude Field cannot be empty"
                }
                if (inputLng.isEmpty()) {
                    isEmptyFields = true
                    etLng.error = "Longitude Field cannot be empty"
                }
                if (inputUsername.isEmpty()) {
                    isEmptyFields = true
                    etUsername.error = "Username Field cannot be empty"
                }

                if (inputPassword.isEmpty()) {
                    isEmptyFields = true
                    etPassword.error = "Password Field cannot be empty"
                }

                if (inputPassword.isNotEmpty()) {
                    if (inputPassword.length < 5) {
                        etPassword.error = "Minimum 5 Character Password"
                        isEmptyFields = true
                    }
                    if (!inputPassword.matches(".*[A-Z].*".toRegex())) {
                        etPassword.error = "Must contain 1 Upper-case Character"
                        isEmptyFields = true
                    }
                    if (!inputPassword.matches(".*[a-z].*".toRegex())) {
                        etPassword.error = "Must contain 1 Lower-case Character"
                        isEmptyFields = true
                    }
                    if (!inputPassword.matches(".*[@#\$%^&+=!()].*".toRegex())) {
                        etPassword.error = "Must contain 1 Special Character.*[@#\$%^&+=!()"
                        isEmptyFields = true
                    }
                }

                if (!isEmptyFields) {
                    toast("Success")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}