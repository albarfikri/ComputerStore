package com.albar.computerstore.ui.activities

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var _navHost: NavHostFragment
    private val navHost get() = _navHost


    override fun onStart() {
        removeEffectsWhileClickingFab(false)
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        // bottom navigation
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(1).isEnabled = false

        navigation()

        binding.login.setOnClickListener {
            Toast.makeText(this, "Hi There", Toast.LENGTH_SHORT).show()
        }

    }

    private fun navigation() {
        binding.fabLocation.setOnClickListener {
            navHost.navController.navigate(R.id.locationFragment)
            removeEffectsWhileClickingFab(false)

            val inflater = navHost.navController.navInflater
            val graph = inflater.inflate(R.navigation.nav_graph)
            graph.setStartDestination(R.id.locationFragment)
        }

        binding.apply {
            bottomNavigationView.setOnItemSelectedListener {

                removeEffectsWhileClickingFab(true)
                when (it.itemId) {
                    R.id.list -> {
                        navHost.findNavController().navigate(R.id.list)
                        showClickedItemBottomNav(2, true, 0, false)
                    }
                    R.id.nearest -> {
                        navHost.findNavController().navigate(R.id.nearest)
                        showClickedItemBottomNav(0, false, 2, true)
                    }
                }
                false
            }

            navHost.findNavController()
                .addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.list, R.id.locationFragment, R.id.nearest ->
                            hidingSomeViewsInSplashScreen(false)
                        else -> hidingSomeViewsInSplashScreen(true)
                    }
                }
        }
    }

    private fun hidingSomeViewsInSplashScreen(statusGone: Boolean) {
        if (!statusGone) {
            binding.bottomAppBar.visibility = View.VISIBLE
            binding.textView.visibility = View.VISIBLE
            binding.login.visibility = View.VISIBLE
            binding.fabLocation.visibility = View.VISIBLE
            binding.bottomNavigationView.visibility = View.VISIBLE
        } else {
            binding.bottomAppBar.visibility = View.GONE
            binding.textView.visibility = View.GONE
            binding.login.visibility = View.GONE
            binding.fabLocation.visibility = View.GONE
            binding.bottomNavigationView.visibility = View.GONE
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun removeEffectsWhileClickingFab(status: Boolean) {
        binding.bottomNavigationView.menu.getItem(0).isChecked = status
        binding.bottomNavigationView.menu.getItem(1).isChecked = status

        if (!status) {
            binding.fabLocation.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
        } else {
            binding.fabLocation.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.defaultColor))
        }
    }

    private fun showClickedItemBottomNav(
        getItemNumber1: Int,
        statusId1: Boolean,
        getItemNumber2: Int,
        statusId2: Boolean
    ) {
        binding.bottomNavigationView.menu.getItem(getItemNumber1).isChecked = statusId1
        binding.bottomNavigationView.menu.getItem(getItemNumber2).isChecked = statusId2
    }
}