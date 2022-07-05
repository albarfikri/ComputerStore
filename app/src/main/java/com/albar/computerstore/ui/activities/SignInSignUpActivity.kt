package com.albar.computerstore.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.albar.computerstore.R
import com.albar.computerstore.databinding.ActivitySignInSignUpBinding
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

    private fun backToHome() {
        val moveToMainActivity =
            Intent(this@SignInSignUpActivity, MainActivity::class.java)
        startActivity(moveToMainActivity).apply {
            this@SignInSignUpActivity.overridePendingTransition(
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
        }
        finish()
    }

    override fun onBackPressed() {
        backToHome()
    }
}