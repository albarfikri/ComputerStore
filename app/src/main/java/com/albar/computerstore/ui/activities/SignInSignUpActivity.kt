package com.albar.computerstore.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.databinding.ActivitySignInSignUpBinding

class SignInSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInSignUpBinding

    private lateinit var _navHost: NavHostFragment
    private val navHost get() = _navHost


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment2) as NavHostFragment
    }

    override fun onBackPressed() {
        navHost.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.signupFragment, R.id.signinFragment -> {
                    }
                }
            }
    }
}