package com.albar.computerstore.ui.fragments.member

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.albar.computerstore.R
import com.albar.computerstore.data.Result
import com.albar.computerstore.databinding.FragmentSearchProductBinding
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.adapter.ComputerStoreProductAdapter
import com.albar.computerstore.ui.viewmodels.ComputerStoreProductViewModel
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchProductFragment : Fragment() {
    private var _binding: FragmentSearchProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComputerStoreProductViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var gson: Gson

    companion object {
        const val EXTRA_ID_COMPUTER_STORE_FOR_SEARCHING = "extra_id_computer_store_for_searching"
        const val EXTRA_TYPE = "extra_type"
    }

    private val adapter by lazy {
        val type = arguments?.getString(EXTRA_TYPE)
        ComputerStoreProductAdapter(
            onItemClicked = { _, item ->
                if (type.equals("computer_store"))
                    findNavController().navigate(
                        R.id.action_searchProductFragment_to_addOrUpdateFragment,
                        Bundle().apply {
                            putString(AddOrUpdateFragment.EXTRA_ACTION_TYPE, "edit")
                            putString(AddOrUpdateFragment.EXTRA_ID_COMPUTER_STORE_PRODUCT, item.id)
                        }
                    )
                if (type.equals("user")) {
                    findNavController().navigate(
                        R.id.action_searchProductFragment2_to_detailComputerStoreProductFragment,
                        Bundle().apply {
                            putString(AddOrUpdateFragment.EXTRA_ACTION_TYPE, "edit")
                            putString(AddOrUpdateFragment.EXTRA_ID_COMPUTER_STORE_PRODUCT, item.id)
                        }
                    )
                }
            },
            glide
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchProductBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val idComputerStoreProduct =
            arguments?.getString(EXTRA_ID_COMPUTER_STORE_FOR_SEARCHING) ?: ""
        viewModel.getAllProductByIdComputerStore(idComputerStoreProduct)
        backToThePrevious()
        setUpRecyclerView()
        search()
        observeAdapterToGetAll()
    }

    private fun search() {
        val idComputerStore = arguments?.getString(EXTRA_ID_COMPUTER_STORE_FOR_SEARCHING) ?: ""
        binding.svProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    viewModel.getProductByName(idComputerStore, newText)
                    observeAdapterToGetSearch()
                } else {
                    viewModel.getAllProductByIdComputerStore(idComputerStore)
                }
                return true
            }

        })
    }

    private fun observeAdapterToGetSearch() {
        viewModel.getProductByName.observe(viewLifecycleOwner) {
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
    }

    private fun observeAdapterToGetAll() {
        viewModel.getAllProduct.observe(viewLifecycleOwner) {
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

    private fun backToThePrevious() {
        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}