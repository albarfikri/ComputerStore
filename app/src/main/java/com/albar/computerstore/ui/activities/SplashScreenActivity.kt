package com.albar.computerstore.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.albar.computerstore.databinding.ActivitySplashScreenBinding
import com.albar.computerstore.ui.adapter.ScreenSlidePagerAdapter

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpAdapter()
    }

    private fun setUpAdapter() {
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        binding.viewPager.adapter = pagerAdapter
    }
}

