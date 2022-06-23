package com.albar.computerstore.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.local.entity.Coordinate
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.databinding.FragmentSignupBinding
import com.albar.computerstore.others.Constants.BUNDLE_KEY
import com.albar.computerstore.others.Constants.REQUEST_KEY
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.dialogfragments.CustomDialogSearchlatlngFragment
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private var latValue: String? = null
    private var lngValue: String? = null

    private val viewModel: ComputerStoreViewModel by viewModels()

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

        viewModel.insertComputerStore.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.btnProgressSignUp.show()
                    binding.btnSignup.text = ""
                    toastShort("Loading please wait.")
                }
                is Result.Error -> {
                    binding.btnProgressSignUp.hide()
                    binding.btnSignup.text = getString(R.string.signup)
                    toastShort(it.error)
                }
                is Result.Success -> {
                    toastShort("Computer store has been created successfully")
                    clearFields()
                    binding.btnProgressSignUp.hide()
                    binding.btnSignup.text = getString(R.string.signup)
                }
            }
        }
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
                    viewModel.insertComputerStore(
                        ComputerStore(
                            id = "",
                            name = inputStoreName,
                            address = inputAddress,
                            lat = inputLat.toDouble(),
                            lng = inputLng.toDouble(),
                            image = "",
                            username = inputUsername,
                            password = inputPassword,
                            createAt = Date(),
                            isVerified = false
                        )
                    )
                }
            }
        }
    }

    private fun clearFields() {
        binding.apply {
            etStoreName.setText("")
            etAddress.setText("")
            etLat.setText("")
            etLng.setText("")
            etUsername.setText("")
            etPassword.setText("")
            etStoreName.clearFocus()
            etAddress.clearFocus()
            etLat.clearFocus()
            etLng.clearFocus()
            etUsername.clearFocus()
            etPassword.clearFocus()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}