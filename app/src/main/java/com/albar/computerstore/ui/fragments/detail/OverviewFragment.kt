package com.albar.computerstore.ui.fragments.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.databinding.FragmentOverviewBinding
import com.albar.computerstore.others.Constants.PARCELABLE_KEY

class OverviewFragment : Fragment() {
    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!

    private var objectComputerStore: ComputerStore? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retrieveData()
    }

    private fun retrieveData() {
        objectComputerStore = arguments?.getParcelable(PARCELABLE_KEY)
        binding.apply {
            tvAddress.text = objectComputerStore?.address
            tvPhone.text = objectComputerStore?.phone
            tvEmail.text = objectComputerStore?.email
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}