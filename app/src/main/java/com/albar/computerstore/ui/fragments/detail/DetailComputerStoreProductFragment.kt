package com.albar.computerstore.ui.fragments.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.data.Result
import com.albar.computerstore.databinding.FragmentDetailComputerStoreProductBinding
import com.albar.computerstore.others.Tools.moneyConverter
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.fragments.member.AddOrUpdateFragment
import com.albar.computerstore.ui.viewmodels.ComputerStoreProductViewModel
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailComputerStoreProductFragment : Fragment() {
    private var _binding: FragmentDetailComputerStoreProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComputerStoreProductViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var gson: Gson

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailComputerStoreProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDefaultText()
        backToThePrevious()
    }

    private fun setDefaultText() {
        val idComputerStoreProduct =
            arguments?.getString(AddOrUpdateFragment.EXTRA_ID_COMPUTER_STORE_PRODUCT, "")!!
        viewModel.getProductById(idComputerStoreProduct)
        viewModel.getProductById.observe(viewLifecycleOwner) { output ->
            when (output) {
                is Result.Success -> {
                    toastShort("Set Successfully")
                    binding.apply {
                        val productPrice = output.data.productPrice.replace("[Rp. ]".toRegex(), "")
                        glide
                            .load(output.data.productImage)
                            .placeholder(R.drawable.ic_broke_image)
                            .transform(CenterCrop(), RoundedCorners(12))
                            .into(binding.tvBackdrop)
                        glide
                            .load(output.data.productImage)
                            .placeholder(R.drawable.ic_broke_image)
                            .transform(CenterCrop(), RoundedCorners(12))
                            .into(binding.ivProductImage)
                        tvProductName.text = output.data.productName
                        tvProductPrice.text = moneyConverter(productPrice.toDouble())
                        tvProductType.text = output.data.productType
                        tvUnit.text = "${output.data.unit.toString()} Unit"
                        tvSpecification.text = output.data.productSpecification
                    }
                }
                else -> {}
            }
        }
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