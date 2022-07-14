package com.albar.computerstore.ui.fragments.member

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.albar.computerstore.data.Result
import com.albar.computerstore.databinding.FragmentProductComputerStoreBinding
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.adapter.ComputerStoreProductAdapter
import com.albar.computerstore.ui.viewmodels.ComputerStoreProductViewModel
import com.albar.computerstore.ui.viewmodels.NetworkViewModel
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ProductComputerStoreFragment : Fragment() {
    private var _binding: FragmentProductComputerStoreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComputerStoreProductViewModel by viewModels()
    private val networkStatusViewModel: NetworkViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    companion object {
        private const val TYPE = "type"

        @JvmStatic
        fun newInstance(type: String) = ProductComputerStoreFragment().apply {
            arguments = Bundle().apply {
                putString(TYPE, type)
            }
        }
    }

    private val adapter by lazy {
        ComputerStoreProductAdapter(
            onItemClicked = { pos, item ->

            },
            glide
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductComputerStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        networkStatus()
    }

    private fun setUpRecyclerView() {

        val orientation = resources.configuration.orientation

        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            binding.rvProductList.layoutManager =
                GridLayoutManager(requireContext(), 2)
        } else {
            binding.rvProductList.layoutManager =
                GridLayoutManager(requireContext(), 3)
        }
        binding.rvProductList.setHasFixedSize(true)
        binding.rvProductList.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
        val type = arguments?.getString(TYPE)
        if (type != null) {
            viewModel.getProductByType(type)
            viewModel.getProductByType.observe(viewLifecycleOwner) {
                when (it) {
                    is Result.Loading -> {
                        dataAvailableCheck(true)
                        binding.rvProductList.hide()
                        binding.shimmer.startShimmer()
                        binding.shimmer.show()
                    }
                    is Result.Error -> {
                        binding.shimmer.stopShimmer()
                        binding.shimmer.hide()
                        binding.rvProductList.hide()
                        dataAvailableCheck(false)
                        toastShort(it.error)
                    }
                    is Result.Success -> {
                        dataAvailableCheck(true)
                        binding.shimmer.stopShimmer()
                        binding.shimmer.hide()
                        binding.rvProductList.show()
                        adapter.updateList(it.data.toMutableList())
                    }
                }
            }
        } else {
            toastShort(type)
        }

    }

    private fun noNetworkAvailableSign(isConnectionAvailable: Boolean) {
        if (!isConnectionAvailable) {
            binding.apply {
                dataAvailableCheck(true)
                rvProductList.hide()
                noNetwork.show()
            }
        } else {
            binding.apply {
                noNetwork.hide()
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

}