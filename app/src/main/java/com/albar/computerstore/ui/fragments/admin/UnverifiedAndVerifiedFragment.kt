package com.albar.computerstore.ui.fragments.admin

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.albar.computerstore.R
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.databinding.FragmentUnverifiedAndVerifiedBinding
import com.albar.computerstore.databinding.ViewConfirmationDialogBinding
import com.albar.computerstore.others.Constants
import com.albar.computerstore.others.Constants.KEY
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.adapter.UnverifiedListAdapter
import com.albar.computerstore.ui.fragments.detail.DetailComputerStoreFragment
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import com.albar.computerstore.ui.viewmodels.NetworkViewModel
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UnverifiedAndVerifiedFragment : Fragment() {
    private var _binding: FragmentUnverifiedAndVerifiedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComputerStoreViewModel by viewModels()
    private val networkStatusViewModel: NetworkViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    private var deletePosition: Int = -1

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
        val isVerifiedStatus = arguments?.getBoolean(IS_VERIFIED)
        UnverifiedListAdapter(
            onItemSweep = { pos, item ->
                deletePosition = pos
                dialogBuilder(pos, item)
            },
            onCallClicked = { _, item ->
                val dialPhoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$item"))
                startActivity(dialPhoneIntent)
            },
            onDetailClicked = { _, item ->
                findNavController().navigate(
                    R.id.action_administratorFragment_to_detailComputerStoreFragment,
                    Bundle().apply {
                        putBoolean(DetailComputerStoreFragment.D0_CALL_OR_VERIFIED_USER, true)
                        putString(KEY, DetailComputerStoreFragment.DETAIL_CLICKED)
                        putParcelable(Constants.PARCELABLE_KEY, item)
                    })
            },
            glide,
            requireContext(),
            isVerifiedStatus!!
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnverifiedAndVerifiedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvUnverifiedList.adapter = adapter
        setUpItemTouchListener()
        networkStatus()
        observeDeletedItem()
    }

    private fun deleteItem(item: ComputerStore) {
        viewModel.deleteComputerStore(item)
    }

    private fun dialogBuilder(position: Int, item: ComputerStore) {
        val recentlyDeletedItem: ComputerStore = item

        adapter.removeList(position)

        val viewDialog = ViewConfirmationDialogBinding.inflate(layoutInflater, null, false)

        val builder = AlertDialog.Builder(requireContext())
            .setView(viewDialog.root)

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        viewDialog.tvInfo.show()
        viewDialog.tvTextInformation.text = "Deleting this Item ?"
        viewDialog.tvInfo.text = item.name
        viewDialog.imgActionIcon.setImageResource(R.drawable.ic_baseline_delete_24)

        viewDialog.btnYes.setOnClickListener {
            deleteItem(item)
            dialog.dismiss()
        }
        viewDialog.imgCancelAction.setOnClickListener {
            adapter.addItem(position, recentlyDeletedItem)
            dialog.dismiss()
        }
    }

    private fun setUpItemTouchListener() {
        val itemTouchHelper = ItemTouchHelper(adapter.SimpleCallback())
        itemTouchHelper.attachToRecyclerView(binding.rvUnverifiedList)
    }

    private fun observeDeletedItem() {
        viewModel.deleteComputerStore.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.rvUnverifiedList.hide()
                    binding.shimmer.startShimmer()
                    binding.shimmer.show()
                }
                is Result.Error -> {
                    binding.shimmer.stopShimmer()
                    binding.shimmer.hide()
                    binding.rvUnverifiedList.hide()
                }
                is Result.Success -> {
                    viewModel.getUnverifiedAndVerifiedNumber()
                    binding.shimmer.stopShimmer()
                    binding.shimmer.hide()
                    binding.rvUnverifiedList.show()
                    retrieveData()
                    val snackBar = Snackbar.make(
                        binding.rvUnverifiedList,
                        it.data,
                        Snackbar.LENGTH_SHORT
                    )
                    snackBar.view.setBackgroundColor(
                        resources.getColor(
                            R.color.verified,
                            context?.theme
                        )
                    )
                    snackBar.show()
                }
            }
        }
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
        val isVerifiedStatus = arguments?.getBoolean(IS_VERIFIED)
        viewModel.getUnverifiedOrVerifiedList(isVerifiedStatus!!)
        viewModel.unverifiedOrVerifiedComputerStoreList.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    dataAvailableCheck(true)
                    binding.rvUnverifiedList.hide()
                    binding.shimmer.startShimmer()
                    binding.shimmer.show()
                }
                is Result.Error -> {
                    dataAvailableCheck(false)
                    binding.shimmer.stopShimmer()
                    binding.shimmer.hide()
                    binding.rvUnverifiedList.hide()
                    toastShort(it.error)
                }
                is Result.Success -> {
                    if (it.data.isNotEmpty()) {
                        dataAvailableCheck(true)
                        binding.shimmer.stopShimmer()
                        binding.shimmer.hide()
                        binding.rvUnverifiedList.show()
                        adapter.updateList(it.data.toMutableList())
                    } else {
                        binding.shimmer.stopShimmer()
                        binding.shimmer.hide()
                        binding.rvUnverifiedList.hide()
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
                dataAvailableCheck(true)
                rvUnverifiedList.hide()
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