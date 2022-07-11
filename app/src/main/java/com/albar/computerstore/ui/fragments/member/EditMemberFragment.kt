package com.albar.computerstore.ui.fragments.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.albar.computerstore.databinding.FragmentEditMemberBinding
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditMemberFragment : Fragment() {
    private var _binding: FragmentEditMemberBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComputerStoreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}