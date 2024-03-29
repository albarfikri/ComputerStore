package com.albar.computerstore.ui.fragments.member

import android.app.Activity
import android.app.AlertDialog
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
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
import com.albar.computerstore.others.Tools.moneyConverter
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
        viewModel.getProductById(idComputerStoreProduct)
        viewModel.getProductById.observe(viewLifecycleOwner) { output ->
            when (output) {
                is Result.Success -> {
                    deleteProduct(output.data)
                    binding.apply {
                        glide
                            .load(output.data.productImage)
                            .placeholder(R.drawable.ic_broke_image)
                            .transform(CenterCrop(), RoundedCorners(12))
                            .into(binding.image)

                        imageUri = output.data.productImage.toUri()

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
                    toastShort("Deleted Successfully")
                }
                else -> {}
            }
        }
    }

    private fun addOrUpdateDataExecution() {
        var getInputProductPrice = binding.etProductPrice.text.toString().trim()
        binding.etProductPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (text.toString() != getInputProductPrice) {
                    binding.etProductPrice.removeTextChangedListener(this)
                    val replace = text.toString().replace("[Rp. ]".toRegex(), "")
                    getInputProductPrice = if (replace.isNotEmpty()) {
                        moneyConverter(replace.toDouble())
                    } else {
                        ""
                    }
                    binding.etProductPrice.setText(getInputProductPrice)
                    binding.etProductPrice.setSelection(getInputProductPrice.length)
                    binding.etProductPrice.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

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
                        arguments?.getString(EXTRA_ID_COMPUTER_STORE_PRODUCT) ?: ""

                    when (arguments?.getString(EXTRA_ACTION_TYPE, "")) {
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
                                    createAt = obj.createAt
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
                                    createAt = obj.createAt
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
                    binding.btnProgressAdd.hide()
                    binding.btnAddNewData.text = getString(R.string.update_data)
                    binding.btnAddNewData.snackBarShort("Data updated successfully")
                    findNavController().navigateUp()
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
                    clearFields()
                    binding.btnProgressAdd.hide()
                    binding.btnAddNewData.text = getString(R.string.add_data)
                    binding.btnAddNewData.snackBarShort("Data added successfully")
                    findNavController().navigateUp()
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

    private fun imageUpload(uri: Uri) {
        uri.let { uploadImageUri ->
            viewModel.uploadImage(uploadImageUri) { uploadedUri ->
                when (uploadedUri) {
                    is Result.Loading -> {
                        binding.progressBar.show()
                        binding.btnAddNewData.isClickable = false
                        binding.btnAddNewData.alpha = 0.6F
                    }
                    is Result.Error -> {
                        binding.btnAddNewData.isClickable = false
                        binding.btnProgressAdd.alpha = 0.6F
                        binding.btnAddNewData.hide()
                    }
                    is Result.Success -> {
                        imageUri = uploadedUri.data
                        Log.d("fileUri", uploadedUri.toString())
                        binding.btnAddNewData.isClickable = true
                        binding.btnAddNewData.alpha = 1F
                        binding.progressBar.hide()
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

    private val startForImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    binding.progressBar.hide()
                    val fileUri = data?.data!!
                    glide
                        .load(fileUri)
                        .placeholder(R.drawable.ic_broke_image)
                        .transform(CenterCrop(), RoundedCorners(10))
                        .into(binding.image)
                    imageUpload(fileUri)
                }
                ImagePicker.RESULT_ERROR -> {
                    binding.progressBar.hide()
                }
                else -> {
                    toastShort("You've canceled selecting picture")
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