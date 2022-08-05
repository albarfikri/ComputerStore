package com.albar.computerstore.ui.fragments.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.data.Result
import com.albar.computerstore.databinding.FragmentSigninBinding
import com.albar.computerstore.others.Constants.DELAY_TO_MOVE_ANOTHER_ACTIVITY
import com.albar.computerstore.others.enable
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.activities.MainActivity
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import com.albar.computerstore.ui.viewmodels.NetworkViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SigninFragment : Fragment() {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComputerStoreViewModel by viewModels()
    private val networkStatusViewModel: NetworkViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.action_signinFragment_to_signupFragment)
        }
        backToThePrevious()

        networkStatusViewModel.hasConnection.observe(viewLifecycleOwner) { isConnected ->
            if (!isConnected) {
                noNetworkAvailableSign(isConnected)
            } else {
                loginButtonClicked()
                observeStatusLogin()
                noNetworkAvailableSign(isConnected)
            }
        }
    }

    private fun noNetworkAvailableSign(isConnectionAvailable: Boolean) {
        if (!isConnectionAvailable) {
            binding.btnSignIn.isClickable = false
            binding.btnSignIn.alpha = 0.6F
        } else {
            binding.btnSignIn.enable()
            binding.btnSignIn.alpha = 1F
        }
    }

    private fun observeStatusLogin() {
        viewModel.loginComputerStore.observe(viewLifecycleOwner) { status ->
            when (status) {
                is Result.Loading -> {
                    binding.btnProgressSignUp.show()
                    binding.btnSignIn.text = ""
                }
                is Result.Error -> {
                    binding.btnProgressSignUp.hide()
                    binding.btnSignIn.text = getString(R.string.signIn)
                    toastShort(status.error)
                }
                is Result.Success -> {
                    if (status.data) {
                        toastShort("Login as Member")
                        clearFields()
                        Handler(Looper.getMainLooper()).postDelayed({
                            headingToMember()
                        }, DELAY_TO_MOVE_ANOTHER_ACTIVITY)
                    } else {
                        toastShort("Login as Admin")
                        clearFields()
                        Handler(Looper.getMainLooper()).postDelayed({
                            headingToAdmin()
                        }, DELAY_TO_MOVE_ANOTHER_ACTIVITY)
                    }
                    binding.btnProgressSignUp.hide()
                    binding.btnSignIn.text = getString(R.string.signIn)
                }
            }
        }
    }

    private fun loginButtonClicked() {
        binding.btnSignIn.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (loginValidation()) {
                viewModel.login(username, password)
                viewModel.getUnverifiedAndVerifiedNumber()
            }
        }
    }

    private fun loginValidation(): Boolean {
        var isValid = true

        binding.apply {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty()) {
                isValid = false
                textInputLayoutUsername.error = "Password field cannot be empty"
            }

            if (password.length < 5) {
                isValid = false
                textInputLayoutPass.error = "Password length minimum 5 Character"
            }

            if (password.isEmpty()) {
                isValid = false
                textInputLayoutPass.error = "Password field cannot be empty"
            }

        }
        return isValid
    }

    private fun clearFields() {
        binding.etUsername.setText("")
        binding.etPassword.setText("")
        binding.etUsername.clearFocus()
        binding.etPassword.clearFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun headingToAdmin() {
        findNavController().navigate(R.id.action_signinFragment_to_administratorFragment).apply {
        }
    }

    private fun headingToMember() {
        findNavController().navigate(R.id.action_signinFragment_to_memberFragment)
    }

    private fun backToThePrevious() {
        binding.back.setOnClickListener {
            val moveToMainActivity = Intent(context, MainActivity::class.java)
            startActivity(moveToMainActivity).apply {
                requireActivity().overridePendingTransition(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
            }
            activity?.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSession { computerStore ->
            if (computerStore != null) {
                if (computerStore.isAdmin) {
                    findNavController().navigate(R.id.action_signinFragment_to_administratorFragment)
                } else {
                    findNavController().navigate(R.id.action_signinFragment_to_memberFragment)
                }
            }
        }
    }
}