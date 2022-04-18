package com.albar.computerstore.ui.fragments.opening

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.albar.computerstore.databinding.FragmentOnBoarding3Binding
import com.albar.computerstore.others.Constants.ON_BOARDING_DATA_STORE_KEY
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OnBoardingFragment3 : Fragment() {

    private lateinit var binding: FragmentOnBoarding3Binding

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "onBoardingStatus")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnBoarding3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.fabGetStarted.setOnClickListener {
            lifecycleScope.launch {
                save(
                    ON_BOARDING_DATA_STORE_KEY,
                    true
                )
            }
            Handler(Looper.myLooper()!!).postDelayed({
                lifecycleScope.launch {
                    val value = run("test")
                    Toast.makeText(requireContext(), value.toString(), Toast.LENGTH_SHORT).show()
                }
            }, 1500L)
        }
    }

    private suspend fun save(key: String, value: Boolean) {
        val dataStoreKey = stringPreferencesKey(key.toString())
        context?.dataStore?.edit { onBoarding ->
            onBoarding[dataStoreKey] = value.toString()
        }
    }

    private suspend fun run(key: String): Boolean {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences =
            context?.dataStore?.data?.first() // emit single preferences object using first()
        return preferences?.get(dataStoreKey).toBoolean()
    }
}