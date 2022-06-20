package com.albar.computerstore.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.albar.computerstore.databinding.FragmentComputerStoreListBinding
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComputerStoreListFragment : Fragment() {

    private var _binding: FragmentComputerStoreListBinding? = null
    private val binding get() = _binding!!

    val viewModel: ComputerStoreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentComputerStoreListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getComputerStore()
        viewModel.computerStore.observe(viewLifecycleOwner) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}