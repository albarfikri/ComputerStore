package com.albar.computerstore.ui.fragments.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.databinding.FragmentDetailComputerStoreBinding
import com.albar.computerstore.others.Constants.KEY
import com.albar.computerstore.others.Constants.PARCELABLE_KEY
import com.albar.computerstore.others.Constants.TAB_TITLES
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.show
import com.albar.computerstore.ui.adapter.DetailSectionsPagerAdapter
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailComputerStoreFragment : Fragment() {

    companion object {
        const val ITEM_CLICKED = "item"
        const val CALL_CLICKED = "call"
        const val DETAIL_CLICKED = "detail"
    }

    @Inject
    lateinit var Glide: RequestManager

    private var _binding: FragmentDetailComputerStoreBinding? = null
    private val binding get() = _binding!!

    private var objectComputerStore: ComputerStore? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailComputerStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAdapterAndViewPager()
        showDetail()
        buttonBack()
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
                        Glide
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
        val detailSectionsPagerAdapter = DetailSectionsPagerAdapter(this)

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