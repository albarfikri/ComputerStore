package com.albar.computerstore.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.databinding.ActivityMainBinding
import com.albar.computerstore.others.Constants.COMPUTER_STORE_LIST
import com.albar.computerstore.others.Constants.COMPUTER_STORE_MAPS
import com.albar.computerstore.others.Constants.COMPUTER_STORE_NEAREST
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
        _navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        setContentView(binding.root)


        // bottom navigation
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(1).isEnabled = false

        binding.textView.text = COMPUTER_STORE_MAPS

        navigation()


        binding.login.setOnClickListener {
            val moveToSignInSignUpActivity =
                Intent(this@MainActivity, SignInSignUpActivity::class.java)
            startActivity(moveToSignInSignUpActivity).apply {
                overridePendingTransition(
                    com.airbnb.lottie.R.anim.abc_slide_in_bottom,
                    com.airbnb.lottie.R.anim.abc_slide_out_top
                )
            }
            finish()
        }
    }

    private fun navigation() {
        binding.fabLocation.setOnClickListener {
            binding.textView.text = COMPUTER_STORE_MAPS
            removeEffectsWhileClickingFab(false)
            val inflater = navHost.navController.navInflater
            val graph = inflater.inflate(R.navigation.nav_graph)
            graph.setStartDestination(R.id.location)
            navHost.navController.navigate(R.id.location)
        }

        binding.apply {
            bottomNavigationView.setOnItemSelectedListener {
                textView.text = COMPUTER_STORE_MAPS
                removeEffectsWhileClickingFab(true)
                when (it.itemId) {
                    R.id.list -> {
                        textView.text = COMPUTER_STORE_LIST
                        navHost.findNavController().navigate(R.id.list)
                        showClickedItemBottomNav(2, true, 0, false)
                    }
                    R.id.nearest -> {
                        textView.text = COMPUTER_STORE_NEAREST
                        navHost.findNavController().navigate(R.id.nearest)
                        showClickedItemBottomNav(0, false, 2, true)
                    }
                }
                false
            }

            navHost.findNavController()
                .addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.list, R.id.location, R.id.nearest ->
                            hidingSomeViewsInSplashScreen(false)
                        R.id.detailList -> {
                            hidingSomeViewsInSplashScreen(true)
                        }
                        else -> hidingSomeViewsInSplashScreen(true)
                    }
                }
        }
    }

    private fun hidingSomeViewsInSplashScreen(statusGone: Boolean) {
        if (!statusGone) {
            binding.apply {
                bottomAppBar.visibility = View.VISIBLE
                fabLocation.visibility = View.VISIBLE
                bottomNavigationView.visibility = View.VISIBLE
                toolbar.show()
            }
        } else {
            binding.apply {
                bottomAppBar.visibility = View.GONE
                fabLocation.visibility = View.GONE
                bottomNavigationView.visibility = View.GONE
                toolbar.hide()
            }

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