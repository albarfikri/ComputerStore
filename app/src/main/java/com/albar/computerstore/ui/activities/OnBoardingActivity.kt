package com.albar.computerstore.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.albar.computerstore.databinding.ActivityOnBoardingBinding
import com.albar.computerstore.ui.adapter.ScreenSlidePagerAdapter

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpAdapter()
    }

    private fun setUpAdapter() {
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        binding.viewPager.adapter = pagerAdapter
    }
}