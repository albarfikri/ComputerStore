package com.albar.computerstore.ui.fragments.admin

import android.content.Intent
import android.net.Uri
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
import com.albar.computerstore.databinding.FragmentUnverifiedAndVerifiedBinding
import com.albar.computerstore.others.Constants
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.adapter.UnverifiedListAdapter
import com.albar.computerstore.ui.fragments.detail.DetailComputerStoreFragment
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import com.albar.computerstore.ui.viewmodels.NetworkViewModel
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class UnverifiedAndVerifiedFragment : Fragment() {
    private var _binding: FragmentUnverifiedAndVerifiedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComputerStoreViewModel by viewModels()

    private val networkStatusViewModel: NetworkViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    companion object {
        private const val IS_VERIFIED = "isVerified"

        @JvmStatic
        fun newInstance(isVerified: Boolean) = UnverifiedAndVerifiedFragment().apply {
            arguments = Bundle().apply {
                putBoolean(IS_VERIFIED, isVerified)
            }
        }
    }

    private val adapter by lazy {
        UnverifiedListAdapter(
            onItemClicked = { position, item ->

            },
            onCallClicked = { _, item ->
                val dialPhoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$item"))
                startActivity(dialPhoneIntent)
            },
            onDetailClicked = { _, item ->
                findNavController().navigate(R.id.action_list_to_detailList, Bundle().apply {
                    putString(Constants.KEY, DetailComputerStoreFragment.DETAIL_CLICKED)
                    putParcelable(Constants.PARCELABLE_KEY, item)
                })
            },
            glide,
            requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUnverifiedAndVerifiedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvUnverifiedList.adapter = adapter
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
        val isVerifiedStatus = arguments?.getBoolean(IS_VERIFIED)
        viewModel.getUnverifiedOrVerifiedList(isVerifiedStatus!!)
        viewModel.unverifiedOrVerifiedComputerStoreList.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.rvUnverifiedList.hide()
                    binding.shimmer.startShimmer()
                    binding.shimmer.show()
                }
                is Result.Error -> {
                    binding.shimmer.hide()
                    binding.shimmer.stopShimmer()
                    binding.rvUnverifiedList.hide()
                    toastShort(it.error)
                }
                is Result.Success -> {
                    binding.shimmer.hide()
                    binding.shimmer.stopShimmer()
                    binding.rvUnverifiedList.show()
                    adapter.updateList(it.data.toMutableList())
                }
            }
        }
    }

    private fun noNetworkAvailableSign(isConnectionAvailable: Boolean) {
        if (!isConnectionAvailable) {
            binding.apply {
                rvUnverifiedList.hide()
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