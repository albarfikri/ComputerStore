package com.albar.computerstore.ui.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.albar.computerstore.databinding.FragmentDetailUnverifiedAndVerifiedBinding

class DetailUnverifiedAndVerifiedFragment : Fragment() {
    private var _binding: FragmentDetailUnverifiedAndVerifiedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailUnverifiedAndVerifiedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}