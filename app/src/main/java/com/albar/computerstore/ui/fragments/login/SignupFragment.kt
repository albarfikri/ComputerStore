package com.albar.computerstore.ui.fragments.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.local.entity.Coordinate
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.databinding.FragmentSignupBinding
import com.albar.computerstore.others.*
import com.albar.computerstore.others.Constants.BUNDLE_KEY
import com.albar.computerstore.others.Constants.REQUEST_KEY
import com.albar.computerstore.others.Tools.encryptCBC
import com.albar.computerstore.ui.dialogfragments.CustomDialogSearchlatlngFragment
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import com.albar.computerstore.ui.viewmodels.NetworkViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private var latValue: String? = null
    private var lngValue: String? = null
    private var addressValue: String? = null
    private var outputGeneral: String = ""

    private val viewModel: ComputerStoreViewModel by viewModels()
    private val networkStatusViewModel: NetworkViewModel by viewModels()

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

        networkStatusViewModel.hasConnection.observe(viewLifecycleOwner) { isConnected ->
            if (!isConnected) {
                noNetworkAvailableSign(isConnected)
            } else {
                openDialogFragment()
                checkIfFieldEmpty()
                registerComputerStoreObserver()
                noNetworkAvailableSign(isConnected)
            }
        }

        setUpDropDown()
    }

    private fun noNetworkAvailableSign(isConnectionAvailable: Boolean) {
        if (!isConnectionAvailable) {
            binding.btnSignup.isClickable = false
            binding.btnSignup.alpha = 0.6F
        } else {
            binding.btnSignup.enable()
            binding.btnSignup.alpha = 1F
        }
    }

    private fun registerComputerStoreObserver() {
        viewModel.registerComputerStore.observe(viewLifecycleOwner) { output ->
            when (output) {
                is Result.Loading -> {
                    binding.btnProgressSignUp.show()
                    binding.btnSignup.text = ""
                }
                is Result.Error -> {
                    binding.btnProgressSignUp.hide()
                    binding.btnSignup.text = getString(R.string.signup)
                    toastShort(output.error)
                }
                is Result.Success -> {
                    toastShort(output.data)
                    clearFields()
                    binding.btnProgressSignUp.hide()
                    binding.btnSignup.text = getString(R.string.signup)
                    toastShort("Saving!")
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
        binding.apply {
            setFragmentResultListener(REQUEST_KEY) { _, bundle ->
                val result = bundle.getParcelable<Coordinate>(BUNDLE_KEY) as Coordinate

                latValue = result.lat.toString()
                lngValue = result.lng.toString()
                addressValue = result.address

                etLat.setText(latValue)
                etLng.setText(lngValue)
                etAddress.setText(addressValue)
                etLat.error = null
                etLng.error = null

                Toast.makeText(activity, "$latValue, $lngValue", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUsername() {
        binding.etUsername.doOnTextChanged { _, _, _, _ ->
            viewModel.isUsernameUsed(binding.etUsername.text.toString().trim())
            viewModel.isUsernameUsed.observe(viewLifecycleOwner) { isUsernameUsed ->
                when (isUsernameUsed) {
                    is Result.Success -> {
                        if (isUsernameUsed.data) {
                            binding.btnSignup.enable()
                        } else {
                            binding.btnSignup.disable()
                            binding.etUsername.error = "Username is Already used"
                        }
                    }
                    is Result.Error -> {
                        toastShort(isUsernameUsed.error)
                    }
                }
            }
        }
    }

    private fun checkIfFieldEmpty() {
        checkUsername()
        binding.apply {
            btnSignup.setOnClickListener {
                var isEmptyFields = false

                val inputStoreName = etStoreName.text.toString().trim()
                val inputAddress = etAddress.text.toString().trim()

                // boolean type convert to string for checking space
                val inputLat = etLat.text.toString().trim()
                val inputLng = etLng.text.toString().trim()

                val inputEmail = etEmail.text.toString().trim()
                val inputPhone = etPhone.text.toString().trim()

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
                if (inputEmail.isEmpty()) {
                    isEmptyFields = true
                    etEmail.error = "Email Field cannot be empty"
                }else{
                    if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
                        etEmail.error = "Email isn't valid"
                        isEmptyFields = true
                    }
                }

                if (inputPhone.isEmpty()) {
                    isEmptyFields = true
                    etPhone.error = "Phone Field cannot be empty"
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
                    viewModel.registerComputerStore(
                        ComputerStore(
                            id = "",
                            name = inputStoreName,
                            address = inputAddress,
                            lat = inputLat.toDouble(),
                            lng = inputLng.toDouble(),
                            area = outputGeneral,
                            email = inputEmail,
                            phone = inputPhone,
                            image = "",
                            username = inputUsername,
                            password = inputPassword.encryptCBC(),
                            createAt = Date(),
                            isVerified = false
                        )
                    )
                }
            }
        }
    }

    private fun setUpDropDown() {
        val productType = resources.getStringArray(R.array.computerStoreArea)
        val arrayAdapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_item_product_type, productType)
        outputGeneral = arrayAdapter.getItem(0).toString()
        binding.etArea.setAdapter(arrayAdapter)
        binding.etArea.setOnItemClickListener { _, _, position, _ ->
            outputGeneral = arrayAdapter.getItem(position).toString()
        }
    }

    private fun clearFields() {
        binding.apply {
            etStoreName.setText("")
            etAddress.setText("")
            etLat.setText("")
            etLng.setText("")
            etEmail.setText("")
            etPhone.setText("")
            etUsername.setText("")
            etPassword.setText("")
            etStoreName.clearFocus()
            etAddress.clearFocus()
            etLat.clearFocus()
            etLng.clearFocus()
            etEmail.clearFocus()
            etPhone.clearFocus()
            etUsername.clearFocus()
            etPassword.clearFocus()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}