package com.albar.computerstore.ui.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.albar.computerstore.databinding.ActivityOnBoardingBinding
import com.albar.computerstore.ui.adapter.ScreenSlidePagerAdapter
import kotlinx.coroutines.flow.first

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpAdapter()

        binding.splashScreen
    }

    private fun setUpAdapter() {
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        binding.viewPager.adapter = pagerAdapter
    }
}