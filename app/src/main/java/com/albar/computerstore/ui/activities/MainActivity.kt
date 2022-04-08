package com.albar.computerstore.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.albar.computerstore.R
import com.albar.computerstore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var _navHost: View
    private val navHost get() = _navHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _navHost = findViewById(R.id.navHostFragment)

        // botom navigation
        binding.bottomNavigationView.background = null

        binding.login.setOnClickListener {
            Toast.makeText(this, "Hi There", Toast.LENGTH_SHORT).show()
        }

        binding.apply {
            bottomNavigationView.setupWithNavController(navHost.findNavController())
            navHost.findNavController()
                .addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.list, R.id.locationFragment, R.id.nearest ->
                            bottomNavigationView.visibility = View.VISIBLE
                        else -> bottomNavigationView.visibility = View.GONE
                    }
                }
        }
    }
}