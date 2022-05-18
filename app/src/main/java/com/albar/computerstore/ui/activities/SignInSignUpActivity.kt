package com.albar.computerstore.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.albar.computerstore.R
import com.albar.computerstore.databinding.ActivitySignInSignUpBinding

class SignInSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        val moveBackToMainActivity =
            Intent(this@SignInSignUpActivity, MainActivity::class.java)
        startActivity(moveBackToMainActivity).apply{
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
        }
        super.onBackPressed()
    }

}