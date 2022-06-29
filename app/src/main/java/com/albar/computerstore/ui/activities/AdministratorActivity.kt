package com.albar.computerstore.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.albar.computerstore.databinding.ActivityAdministratorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdministratorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdministratorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdministratorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}