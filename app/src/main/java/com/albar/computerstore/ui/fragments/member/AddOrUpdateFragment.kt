package com.albar.computerstore.ui.fragments.member

import android.app.Activity
import android.app.AlertDialog
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.albar.computerstore.R
import com.albar.computerstore.data.Result
import com.albar.computerstore.data.remote.entity.ComputerStore
import com.albar.computerstore.data.remote.entity.ComputerStoreProduct
import com.albar.computerstore.databinding.FragmentAddOrUpdateBinding
import com.albar.computerstore.databinding.ViewConfirmationDialogBinding
import com.albar.computerstore.others.*
import com.albar.computerstore.ui.viewmodels.ComputerStoreProductViewModel
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddOrUpdateFragment : Fragment() {
    private var _binding: FragmentAddOrUpdateBinding? = null
    private val binding get() = _binding!!
    private var outputGeneral: String = ""
    private var imageUri: Uri? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val viewModel: ComputerStoreProductViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var sharedPref: SharedPreferences

    companion object {
        const val EXTRA_ID_COMPUTER_STORE_PRODUCT = "extra_id_computer_store_product"
        const val EXTRA_ID_COMPUTER_STORE = "extra_id_computer_store"
        const val EXTRA_ACTION_TYPE = "extra_action_type"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddOrUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setUpDropDown()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDefaultText()
        setUpDropDown()
        backToThePrevious()
        btnUploadImageClicked()
        addOrUpdateData()
        observeDeleteData()
    }

    private fun setUpDropDown() {
        val productType = resources.getStringArray(R.array.productType)
        val arrayAdapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_item_product_type, productType)
        binding.etType.setAdapter(arrayAdapter)
        binding.etType.setOnItemClickListener { _, _, position, _ ->
            outputGeneral = arrayAdapter.getItem(position).toString()
        }
    }

    private fun setDefaultText() {
        val idComputerStoreProduct = arguments?.getString(EXTRA_ID_COMPUTER_STORE_PRODUCT, "")!!
        toastShort(idComputerStoreProduct)
        viewModel.getProductById(idComputerStoreProduct)
        viewModel.getProductById.observe(viewLifecycleOwner) { output ->
            when (output) {
                is Result.Success -> {
                    toastShort("Set Successfully")
                    deleteProduct(output.data)
                    binding.apply {
                        glide
                            .load(output.data.productImage)
                            .placeholder(R.drawable.ic_broke_image)
                            .transform(CenterCrop(), RoundedCorners(12))
                            .into(binding.image)
                        etProductName.setText(output.data.productName)
                        etProductPrice.setText(output.data.productPrice)
                        etType.setText(output.data.productType)

                        outputGeneral = output.data.productType
                        setUpDropDown()

                        etUnit.setText(output.data.unit.toString())
                        etSpecification.setText(output.data.productSpecification)
                    }
                }
                else -> {}
            }
        }
    }

    private fun deleteProduct(computerStoreProduct: ComputerStoreProduct) {
        binding.ivDelete.setOnClickListener {
            val viewDialog = ViewConfirmationDialogBinding.inflate(layoutInflater, null, false)

            val builder = AlertDialog.Builder(requireContext())
                .setView(viewDialog.root)

            viewDialog.tvTextInformation.text = "Are you sure want to delete ?"
            viewDialog.tvInfo.show()
            viewDialog.tvInfo.text = computerStoreProduct.productName
            val dialog = builder.create()
            dialog.setCancelable(false)
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            viewDialog.btnYes.setOnClickListener {
                viewDialog.btnYes.text = ""
                viewDialog.btnProgressSignUp.show()
                viewModel.deleteData(computerStoreProduct)
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigateUp()
                    dialog.dismiss()
                    toastShort("Delete Successfully.")
                }, Constants.DELAY_TO_MOVE_ANOTHER_FRAGMENT)
            }
            viewDialog.imgCancelAction.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun observeDeleteData() {
        viewModel.deleteComputerStoreProduct.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Error -> {
                    toastShort(it.error)
                }
                is Result.Success -> {
                    toastShort(outputGeneral)
                }
                else -> {}
            }
        }
    }

    private fun addOrUpdateDataExecution() {
        binding.apply {
            btnAddNewData.setOnClickListener {
                var isEmptyFields = false

                val productName = etProductName.text.toString().trim()
                val productPrice = etProductPrice.text.toString().trim()
                val productUnit = etUnit.text.toString().trim()

                if (productName.isEmpty()) {
                    isEmptyFields = true
                    etProductName.error = "Product Name Field cannot be empty"
                }
                if (productPrice.isEmpty()) {
                    isEmptyFields = true
                    etProductPrice.error = "Product Price Field cannot be empty"
                }

                if (productUnit.isEmpty()) {
                    isEmptyFields = true
                    etUnit.error = "Product Unit Field cannot be empty"
                }

                if (!isEmptyFields) {
                    // from shared pref
                    val json = sharedPref.getString(Constants.COMPUTER_STORE_SESSION, "")
                    val obj = gson.fromJson(json, ComputerStore::class.java)

                    // from bundle
                    val idComputerStoreProduct =
                        arguments?.getString(EXTRA_ID_COMPUTER_STORE_PRODUCT)!!
                    val type = arguments?.getString(EXTRA_ACTION_TYPE, "")

                    when (type) {
                        "add" -> {
                            viewModel.addData(
                                ComputerStoreProduct(
                                    id = "",
                                    idComputerStore = obj.id,
                                    productName = productName,
                                    productType = outputGeneral,
                                    productPrice = productPrice,
                                    productImage = imageUri.toString(),
                                    productSpecification = etSpecification.text.toString(),
                                    unit = productUnit.toInt(),
                                    isStockAvailable = productUnit.toInt() > 0,
                                    createAt = Date()
                                )
                            )
                        }
                        "edit" -> {
                            viewModel.updateData(
                                ComputerStoreProduct(
                                    id = idComputerStoreProduct,
                                    idComputerStore = obj.id,
                                    productName = productName,
                                    productType = outputGeneral,
                                    productPrice = productPrice,
                                    productImage = imageUri.toString(),
                                    productSpecification = etSpecification.text.toString(),
                                    unit = productUnit.toInt(),
                                    isStockAvailable = productUnit.toInt() > 0,
                                    createAt = Date()
                                )
                            )
                        }
                    }

                }
            }
        }
    }

    private fun addOrUpdateData() {
        when (arguments?.getString(EXTRA_ACTION_TYPE, "")) {
            "add" -> {
                binding.ivDelete.hide()
                binding.btnAddNewData.text = "Add Data"
                addOrUpdateDataExecution()
                observeAddData()
            }
            "edit" -> {
                binding.ivDelete.show()
                binding.btnAddNewData.text = "Update Data"
                addOrUpdateDataExecution()
                observeUpdateData()
            }
        }
    }

    private fun observeUpdateData() {
        viewModel.updateComputerStoreProduct.observe(viewLifecycleOwner) { output ->
            when (output) {
                is Result.Loading -> {
                    binding.btnProgressAdd.show()
                    binding.btnAddNewData.text = ""
                }
                is Result.Error -> {
                    binding.btnProgressAdd.hide()
                    binding.btnAddNewData.text = getString(R.string.update_data)
                    toastShort(output.error)
                }
                is Result.Success -> {
                    toastShort(output.data)
                    binding.btnProgressAdd.hide()
                    binding.btnAddNewData.text = getString(R.string.update_data)
                    binding.btnAddNewData.snackBarShort("Data updated successfully")
                }
            }
        }
    }

    private fun observeAddData() {
        viewModel.addData.observe(viewLifecycleOwner) { output ->
            when (output) {
                is Result.Loading -> {
                    binding.btnProgressAdd.show()
                    binding.btnAddNewData.text = ""
                }
                is Result.Error -> {
                    binding.btnProgressAdd.hide()
                    binding.btnAddNewData.text = getString(R.string.add_data)
                    toastShort(output.error)
                }
                is Result.Success -> {
                    toastShort(output.data)
                    clearFields()
                    binding.btnProgressAdd.hide()
                    binding.btnAddNewData.text = getString(R.string.signup)
                    binding.btnAddNewData.snackBarShort("Data added successfully")
                }
            }
        }
    }

    private fun clearFields() {
        binding.apply {
            etProductName.setText("")
            etType.setText("")
            etProductPrice.setText("")
            etUnit.setText("")
            etSpecification.setText("")
            etProductName.clearFocus()
            etType.clearFocus()
            etProductPrice.clearFocus()
            etSpecification.clearFocus()
            etUnit.clearFocus()
        }
    }


    private val startForImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    imageUri = fileUri
                    binding.progressBar.hide()
                    glide
                        .load(imageUri)
                        .placeholder(R.drawable.ic_broke_image)
                        .transform(CenterCrop(), RoundedCorners(50))
                        .into(binding.image)
                    imageUpload()
                }
                ImagePicker.RESULT_ERROR -> {
                    binding.progressBar.hide()
                    toastShort(ImagePicker.getError(data))
                }
                else -> {
                    binding.progressBar.hide()
                }
            }
        }

    private fun imageUpload() {
        imageUri.let {
            if (it != null) {
                viewModel.uploadImage(it) { uploadedUri ->
                    when (uploadedUri) {
                        is Result.Loading -> {
                            binding.progressBar.show()
                        }
                        is Result.Error -> {
                            binding.progressBar.hide()
                        }
                        is Result.Success -> {
                            imageUri = uploadedUri.data
                            binding.progressBar.hide()
                        }
                    }
                }
            }
        }
    }

    private fun btnUploadImageClicked() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.btmSheet.bottomSheet)
        binding.addImage.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        binding.btmSheet.imgGallery.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            ImagePicker.with(this)
                .compress(512)
                .galleryOnly()
                .createIntent { intent ->
                    startForImageResult.launch(intent)
                }
        }

        binding.btmSheet.imgCamera.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            ImagePicker.with(this)
                .compress(512)
                .cameraOnly()
                .createIntent { intent ->
                    startForImageResult.launch(intent)
                }
        }

        binding.btmSheet.cancelBtn.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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