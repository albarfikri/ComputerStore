package com.albar.computerstore.ui.fragments.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.albar.computerstore.ui.fragments.detail.DetailComputerStoreFragment.Companion.D0_CALL_OR_VERIFIED_USER
import com.albar.computerstore.ui.fragments.detail.DetailComputerStoreFragment.Companion.DETAIL_CLICKED
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import com.albar.computerstore.ui.viewmodels.NetworkViewModel
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ComputerStoreListFragment : Fragment() {

    private var _binding: FragmentComputerStoreListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComputerStoreViewModel by viewModels()

    private val networkStatusViewModel: NetworkViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    private val adapter by lazy {
        ComputerStoreListAdapter(
            onItemClicked = { _, _ ->

            },
            onCallClicked = { _, item ->
                val dialPhoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$item"))
                startActivity(dialPhoneIntent)
            },
            onDetailClicked = { _, item ->
                findNavController().navigate(R.id.action_list_to_detailList, Bundle().apply {
                    putBoolean(D0_CALL_OR_VERIFIED_USER, false)
                    putString(KEY, DETAIL_CLICKED)
                    putParcelable(PARCELABLE_KEY, item)
                })
            },
            glide
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            if (!isConnected) {
                noNetworkAvailableSign(isConnected)
                dataAvailableCheck(true)
            } else {
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
                    dataAvailableCheck(true)
                    binding.shimmer.show()
                }
                is Result.Error -> {
                    binding.shimmer.hide()
                    binding.shimmer.stopShimmer()
                    dataAvailableCheck(false)
                    binding.rvComputerList.hide()
                    toastShort(it.error)
                }
                is Result.Success -> {
                    if (it.data.isNotEmpty()) {
                        binding.shimmer.hide()
                        binding.shimmer.stopShimmer()
                        binding.rvComputerList.show()
                        dataAvailableCheck(true)
                        adapter.updateList(it.data.toMutableList())
                        adapter.notifyDataSetChanged()
                    } else {
                        dataAvailableCheck(false)
                    }
                }
            }
        }
    }

    private fun dataAvailableCheck(isAvailable: Boolean) {
        if (!isAvailable) {
            binding.apply {
                noDataLottie.show()
                noDataDescription.show()
            }
        } else {
            binding.apply {
                noDataLottie.hide()
                noDataDescription.hide()
            }
        }
    }

    private fun noNetworkAvailableSign(isConnectionAvailable: Boolean) {
        if (!isConnectionAvailable) {
            binding.apply {
                rvComputerList.hide()
                noNetwork.show()
            }
        } else {
            binding.apply {
                noNetwork.hide()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}