package com.albar.computerstore.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.albar.computerstore.data.Result
import com.albar.computerstore.databinding.FragmentComputerStoreListBinding
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.adapter.ComputerStoreListAdapter
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import com.albar.computerstore.ui.viewmodels.NetworkViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ComputerStoreListFragment : Fragment() {

    private var _binding: FragmentComputerStoreListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComputerStoreViewModel by viewModels()

    private val networkStatusViewModel: NetworkViewModel by viewModels()

    private val adapter by lazy {
        ComputerStoreListAdapter(
            onItemClicked = { position, item ->


            },
            onEditClicked = { position, item ->

            },
            onDeleteClicked = { position, item ->

            }
        )

    }

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
        binding.rvComputerList.adapter = adapter

        networkStatus()
    }

    private fun networkStatus() {
        networkStatusViewModel.hasConnection.observe(viewLifecycleOwner) { isConnected ->
            Timber.d("Ini Koneksi $isConnected")
            if (!isConnected) {
                Toast.makeText(requireContext(), "No internet connection !", Toast.LENGTH_SHORT)
                    .show()
                noNetworkAvailableSign(isConnected)
            } else {
                Toast.makeText(requireContext(), "Internet is Available", Toast.LENGTH_SHORT)
                    .show()
                viewModel.getComputerStore()
                viewModel.computerStore.observe(viewLifecycleOwner) {
                    when (it) {
                        is Result.Loading -> {
                            binding.progressBar.show()
                        }
                        is Result.Error -> {
                            binding.progressBar.hide()
                            toastShort(it.error)
                        }
                        is Result.Success -> {
                            binding.progressBar.hide()
                            adapter.updateList(it.data.toMutableList())
                        }
                    }
                }
                noNetworkAvailableSign(isConnected)
            }
        }
    }

    private fun noNetworkAvailableSign(isConnectionAvailable: Boolean) {
        if (!isConnectionAvailable) {
            binding.apply {
                rvComputerList.hide()
                noDataLottie.show()
            }
        } else {
            binding.apply {
                rvComputerList.show()
                noDataLottie.hide()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}