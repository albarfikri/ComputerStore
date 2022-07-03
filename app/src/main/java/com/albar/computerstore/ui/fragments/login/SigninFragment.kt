package com.albar.computerstore.ui.fragments.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.data.Result
import com.albar.computerstore.databinding.FragmentSigninBinding
import com.albar.computerstore.others.Constants.DELAY_TO_MOVE_ANOTHER_ACTIVITY
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.activities.MainActivity
import com.albar.computerstore.ui.activities.MemberActivity
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SigninFragment : Fragment() {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComputerStoreViewModel by viewModels()

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
        loginButtonClicked()
        observeStatusLogin()
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
                        toastShort("Login As Member")
                        clearFields()
                        Handler(Looper.getMainLooper()).postDelayed({
                            headingToMember()
                        }, DELAY_TO_MOVE_ANOTHER_ACTIVITY)
                    } else {
                        toastShort("Login As Admin")
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

    private fun loginButtonClicked() {
        binding.btnSignIn.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (loginValidation()) {
                viewModel.login(username, password)
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
        findNavController().navigate(R.id.action_signinFragment_to_administratorFragment)
    }

    private fun headingToMember() {
        val moveToMainActivity =
            Intent(requireActivity(), MemberActivity::class.java)
        startActivity(moveToMainActivity).apply {
            requireActivity().overridePendingTransition(
                com.airbnb.lottie.R.anim.abc_slide_in_bottom,
                com.airbnb.lottie.R.anim.abc_slide_out_top
            )
        }
    }
}