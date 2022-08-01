package com.albar.computerstore.ui.fragments.detail

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.databinding.FragmentDetailComputerStoreBinding
import com.albar.computerstore.others.Constants.KEY
import com.albar.computerstore.others.Constants.PARCELABLE_KEY
import com.albar.computerstore.others.Constants.TAB_TITLES
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.show
import com.albar.computerstore.others.snackBarShort
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.adapter.DetailSectionsPagerAdapter
import com.albar.computerstore.ui.fragments.member.SearchProductFragment.Companion.EXTRA_ID_COMPUTER_STORE_FOR_SEARCHING
import com.albar.computerstore.ui.fragments.member.SearchProductFragment.Companion.EXTRA_TYPE
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DetailComputerStoreFragment : Fragment() {

    companion object {
        const val D0_CALL_OR_VERIFIED_USER = "do_call_or_verified_user"
        const val DETAIL_CLICKED = "detail"
    }

    @Inject
    lateinit var glide: RequestManager

    private val viewModel: ComputerStoreViewModel by viewModels()

    private var _binding: FragmentDetailComputerStoreBinding? = null
    private val binding get() = _binding!!

    private var objectComputerStore: ComputerStore? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailComputerStoreBinding.inflate(inflater, container, false)
        doCallOrVerifiedUser()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapterAndViewPager()
        showDetail()
        buttonBack()
        search()
    }

    private fun search() {
        binding.search.setOnClickListener {
            objectComputerStore = arguments?.getParcelable(PARCELABLE_KEY)
            findNavController().navigate(
                R.id.action_detailList_to_searchProductFragment2,
                Bundle().apply {
                    putString(EXTRA_ID_COMPUTER_STORE_FOR_SEARCHING, objectComputerStore?.id)
                    putString(EXTRA_TYPE, "user")
                }
            )
        }
    }

    private fun doCall() {
        binding.apply {
            fabAction.setImageResource(R.drawable.ic_round_phone)
            fabAction.setOnClickListener {
                val phoneNumber = objectComputerStore?.phone
                val dialPhoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                startActivity(dialPhoneIntent)
            }
        }
    }

    private fun verifiedUser() {
        var isVerifiedChecked: Boolean
        binding.apply {
            objectComputerStore = arguments?.getParcelable(PARCELABLE_KEY)
            isVerifiedChecked = if (!objectComputerStore?.isVerified!!) {
                fabAction.setImageResource(R.drawable.ic_unverified_computer_store)
                false
            } else {
                fabAction.setImageResource(R.drawable.ic_verified_computer_store)
                true
            }

            fabAction.setOnClickListener {
                isVerifiedChecked = !isVerifiedChecked

                if (isVerifiedChecked) {
                    updateVerifiedOrUnverifiedComputerStore(isVerifiedChecked)
                    observerOutputVerifiedOrUnverified(isVerifiedChecked)
                } else {
                    updateVerifiedOrUnverifiedComputerStore(isVerifiedChecked)
                    observerOutputVerifiedOrUnverified(isVerifiedChecked)
                }
            }
        }
    }

    private fun observerOutputVerifiedOrUnverified(status: Boolean) {
        viewModel.updateVerifiedOrUnVerifiedStore.observe(viewLifecycleOwner) { output ->
            when (output) {
                is Result.Success -> {
                    binding.fabAction.setImageResource(R.drawable.ic_verified_computer_store)
                    if (status) {
                        val snackBar = Snackbar.make(
                            binding.root,
                            "Computer Store has been Verified",
                            Snackbar.LENGTH_SHORT
                        )
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            snackBar.view.setBackgroundColor(
                                resources.getColor(
                                    R.color.verified,
                                    context?.theme
                                )
                            )
                        }
                        snackBar.show()
                    } else {
                        binding.fabAction.setImageResource(R.drawable.ic_unverified_computer_store)
                        binding.root.snackBarShort("Computer Store has been Unverified")
                    }
                }
                is Result.Error -> toastShort(output.error)
                is Result.Loading -> {}
            }
        }
    }

    private fun updateVerifiedOrUnverifiedComputerStore(isVerified: Boolean) {
        objectComputerStore = arguments?.getParcelable(PARCELABLE_KEY)
        viewModel.updateVerifiedOrUnVerifiedStore(
            ComputerStore(
                id = objectComputerStore!!.id,
                isVerified = isVerified,
                createAt = Date(),
                name = objectComputerStore!!.name,
                address = objectComputerStore!!.address,
                area = objectComputerStore!!.area,
                phone = objectComputerStore!!.phone,
                email = objectComputerStore!!.email,
                lat = objectComputerStore!!.lat,
                lng = objectComputerStore!!.lng,
                image = objectComputerStore!!.image,
                username = objectComputerStore!!.username,
                password = objectComputerStore!!.password,
            )
        )
        viewModel.getUnverifiedAndVerifiedNumber()
    }

    private fun doCallOrVerifiedUser() {
        val isAdmin = arguments?.getBoolean(D0_CALL_OR_VERIFIED_USER)
        if (isAdmin == true) {
            verifiedUser()
        } else {
            doCall()
        }
    }

    private fun buttonBack() {
        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showDetail() {
        val type = arguments?.getString(KEY, null)
        type?.let {
            when (it) {
                DETAIL_CLICKED -> {
                    objectComputerStore = arguments?.getParcelable(PARCELABLE_KEY)
                    binding.apply {
                        progressBar(true)
                        glide
                            .load(objectComputerStore?.image)
                            .transform(CenterCrop(), RoundedCorners(10))
                            .into(imgComputerStore)
                        progressBar(false)
                        expandedTitle.text = objectComputerStore?.name
                        collapsedTitle.text = objectComputerStore?.name
                    }
                }
                else -> {}
            }
        }
    }

    private fun progressBar(status: Boolean) {
        if (!status) {
            binding.progressBar.hide()
        } else {
            binding.progressBar.show()
        }
    }

    private fun setUpAdapterAndViewPager() {
        // Sending object to DetailSectionsPagerAdapter
        val isAdmin = arguments?.getBoolean(D0_CALL_OR_VERIFIED_USER)
        val detailSectionsPagerAdapter = DetailSectionsPagerAdapter(this)
        objectComputerStore = arguments?.getParcelable(PARCELABLE_KEY)
        detailSectionsPagerAdapter.objectComputerStore = objectComputerStore

        if (isAdmin == true) {
            detailSectionsPagerAdapter.isAdmin = isAdmin
        }

        binding.apply {
            viewPager2.adapter = detailSectionsPagerAdapter
            TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                tab.text = resources.getString(TAB_TITLES[position])
            }.attach()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}