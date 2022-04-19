package com.albar.computerstore.ui.fragments.opening

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.albar.computerstore.databinding.FragmentOnBoarding3Binding
import com.albar.computerstore.others.Constants.ON_BOARDING_DATA_STORE_KEY
import com.albar.computerstore.ui.viewmodels.DataStoreViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment3 : Fragment() {

    private lateinit var binding: FragmentOnBoarding3Binding

    private val viewModel: DataStoreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoarding3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.fabGetStarted.setOnClickListener {
            viewModel.saveDataStoreStatus(ON_BOARDING_DATA_STORE_KEY, true)
            Handler(Looper.myLooper()!!).postDelayed({
                viewModel.getDataStoreStatus().observe(viewLifecycleOwner) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                }
            }, 1500L)
        }
    }
}