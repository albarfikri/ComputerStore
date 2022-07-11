package com.albar.computerstore.ui.fragments.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.albar.computerstore.databinding.FragmentProductComputerStoreBinding
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import com.albar.computerstore.ui.viewmodels.NetworkViewModel
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductComputerStoreFragment : Fragment() {
    private var _binding: FragmentProductComputerStoreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComputerStoreViewModel by viewModels()
    private val networkStatusViewModel: NetworkViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    companion object {
        private const val TYPE = "type"

        @JvmStatic
        fun newInstance(type: String) = ProductComputerStoreFragment().apply {
            arguments = Bundle().apply {
                putString(type, TYPE)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductComputerStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun retrieveData() {
        val type = arguments?.getString(TYPE)
        viewModel.updateComputerStore
    }
}