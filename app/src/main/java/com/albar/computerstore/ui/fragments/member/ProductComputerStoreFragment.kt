package com.albar.computerstore.ui.fragments.member

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.albar.computerstore.R
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.databinding.FragmentProductComputerStoreBinding
import com.albar.computerstore.others.Constants
import com.albar.computerstore.others.Constants.ADMIN
import com.albar.computerstore.others.Constants.MAIN
import com.albar.computerstore.others.Constants.MEMBER
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.adapter.ComputerStoreProductAdapter
import com.albar.computerstore.ui.fragments.member.AddOrUpdateFragment.Companion.EXTRA_ACTION_TYPE
import com.albar.computerstore.ui.fragments.member.AddOrUpdateFragment.Companion.EXTRA_ID_COMPUTER_STORE_PRODUCT
import com.albar.computerstore.ui.viewmodels.ComputerStoreProductViewModel
import com.albar.computerstore.ui.viewmodels.NetworkViewModel
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
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

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var sharedPref: SharedPreferences

    companion object {
        const val EXTRA_ADAPTER_SET_FROM = "extra_adapter_set_from"
        private const val TYPE = "type"
        const val EXTRA_COMPUTER_STORE_OBJECT = "extra_computer_store_object"

        @JvmStatic
        fun newInstance(adapterSetFrom: String, type: String, objectComputerStore: ComputerStore) =
            ProductComputerStoreFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_ADAPTER_SET_FROM, adapterSetFrom)
                    putString(TYPE, type)
                    putParcelable(EXTRA_COMPUTER_STORE_OBJECT, objectComputerStore)
                }
            }
    }

    private val adapter by lazy {
        ComputerStoreProductAdapter(
            onItemClicked = { _, item ->
                when (arguments?.getString(EXTRA_ADAPTER_SET_FROM)) {
                    MAIN -> {
                        findNavController().navigate(
                            R.id.action_detailList_to_detailComputerStoreProductFragment,
                            Bundle().apply {
                                putString(EXTRA_ID_COMPUTER_STORE_PRODUCT, item.id)
                            }
                        )
                    }
                    MEMBER -> {
                        findNavController().navigate(
                            R.id.action_memberFragment_to_addOrUpdateFragment,
                            Bundle().apply {
                                putString(EXTRA_ACTION_TYPE, "edit")
                                putString(EXTRA_ID_COMPUTER_STORE_PRODUCT, item.id)
                            }
                        )
                    }
                    ADMIN -> {
                        findNavController().navigate(
                            R.id.action_detailComputerStoreFragment_to_detailComputerStoreProductFragment2,
                            Bundle().apply {
                                putString(EXTRA_ID_COMPUTER_STORE_PRODUCT, item.id)
                            }
                        )
                    }
                }
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
        binding.rvProductList.setHasFixedSize(true)
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            binding.rvProductList.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        } else {
            binding.rvProductList.layoutManager =
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        }

        binding.rvProductList.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun networkStatus() {
        networkStatusViewModel.hasConnection.observe(viewLifecycleOwner) { isConnected ->
            if (!isConnected) {
                noNetworkAvailableSign(isConnected)
            } else {
                noNetworkAvailableSign(isConnected)
                retrieveData()
            }
        }
    }

    private fun retrieveData() {
        val json = sharedPref.getString(Constants.COMPUTER_STORE_SESSION, "")
        val obj = gson.fromJson(json, ComputerStore::class.java)
        val objComputerStore: ComputerStore? =
            arguments?.getParcelable(EXTRA_COMPUTER_STORE_OBJECT)

        val type = arguments?.getString(TYPE)
        val idComputerStore = if (objComputerStore?.id.isNullOrEmpty()) {
            obj.id
        } else {
            objComputerStore?.id
        }

        if (type != null) {
            if (idComputerStore != null) {
                viewModel.getProductByType(idComputerStore, type)
            }
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