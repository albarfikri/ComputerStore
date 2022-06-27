package com.albar.computerstore.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.data.Result
import com.albar.computerstore.databinding.FragmentComputerStoreListBinding
import com.albar.computerstore.others.Constants.KEY
import com.albar.computerstore.others.Constants.PARCELABLE_KEY
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.adapter.ComputerStoreListAdapter
import com.albar.computerstore.ui.fragments.detail.DetailComputerStoreFragment.Companion.DETAIL_CLICKED
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
            onCallClicked = { position, item ->

            },
            onDetailClicked = { _, item ->
                findNavController().navigate(R.id.action_list_to_detailList, Bundle().apply {
                    putString(KEY, DETAIL_CLICKED)
                    putParcelable(PARCELABLE_KEY, item)
                })
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
                noNetworkAvailableSign(isConnected)
                retrieveData()
            }
        }
    }

    private fun retrieveData() {
        viewModel.getComputerStore()
        viewModel.computerStore.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.rvComputerList.hide()
                    binding.shimmer.startShimmer()
                    binding.shimmer.show()
                }
                is Result.Error -> {
                    binding.shimmer.hide()
                    binding.shimmer.stopShimmer()
                    binding.rvComputerList.hide()
                    toastShort(it.error)
                }
                is Result.Success -> {
                    binding.shimmer.hide()
                    binding.shimmer.stopShimmer()
                    binding.rvComputerList.show()
                    adapter.updateList(it.data.toMutableList())
                    adapter.notifyDataSetChanged()
                }
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
                noDataLottie.hide()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}