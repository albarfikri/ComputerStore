package com.albar.computerstore.ui.fragments.admin

import android.app.Activity
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.albar.computerstore.databinding.FragmentEditAdminBinding
import com.albar.computerstore.others.Constants.COMPUTER_STORE_SESSION
import com.albar.computerstore.others.Tools.decryptCBC
import com.albar.computerstore.others.Tools.encryptCBC
import com.albar.computerstore.others.hide
import com.albar.computerstore.others.show
import com.albar.computerstore.others.toastShort
import com.albar.computerstore.ui.viewmodels.ComputerStoreViewModel
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class EditAdminFragment : Fragment() {
    private var _binding: FragmentEditAdminBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var glide: RequestManager

    private val viewModel: ComputerStoreViewModel by viewModels()

    private var imageUri: Uri? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTextToEditText()
        backToThePrevious()
        uploadPhotoButtonClick()
        editData()

        viewModel.updateComputerStore.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.btnProgressUpdate.show()
                    binding.btnUpdate.text = ""
                }
                is Result.Error -> {
                    binding.btnProgressUpdate.hide()
                    binding.btnUpdate.text = getString(R.string.update)
                }
                is Result.Success -> {
                    binding.btnProgressUpdate.hide()
                    binding.btnUpdate.text = getString(R.string.update)
                    val snackBar =
                        Snackbar.make(binding.root, "Updated Successfully", Snackbar.LENGTH_SHORT)

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

    private fun setTextToEditText() {
        val data = sharedPref.getString(COMPUTER_STORE_SESSION, null)

        // convert data from shared pref to ComputerStore data class.
        val objectComputerStore = gson.fromJson(data, ComputerStore::class.java)

        // set to edit text
        binding.apply {
            toastShort(objectComputerStore.image.toUri().toString())
            imageUri = objectComputerStore.image.toUri()
            glide
                .load(imageUri)
                .placeholder(R.drawable.ic_broke_image)
                .transform(CenterCrop(), RoundedCorners(10))
                .into(image)
            etUsername.setText(objectComputerStore.username)
            etPassword.setText(objectComputerStore.password.decryptCBC())
            etEmail.setText(objectComputerStore.email)
            etPhone.setText(objectComputerStore.phone)
            etAddress.setText(objectComputerStore.address)
        }
    }

    private fun editData() {
        binding.apply {
            btnUpdate.setOnClickListener {
                var isEmptyFields = false

                val inputUsername = etUsername.text.toString().trim()
                val inputPassword = etPassword.text.toString().trim()
                val inputEmail = etEmail.text.toString().trim()
                val inputPhone = etPhone.text.toString().trim()
                val inputAddress = etAddress.text.toString().trim()

                if (inputUsername.isEmpty()) {
                    isEmptyFields = true
                    etUsername.error = "Username Field cannot be empty"
                }

                if (inputPassword.isEmpty()) {
                    isEmptyFields = true
                    etPassword.error = "Password Field cannot be empty"
                }

                if (inputEmail.isEmpty()) {
                    isEmptyFields = true
                    etUsername.error = "Email Field cannot be empty"
                }

                if (inputPhone.isEmpty()) {
                    isEmptyFields = true
                    etPassword.error = "Phone Field cannot be empty"
                }

                if (inputAddress.isEmpty()) {
                    isEmptyFields = true
                    etAddress.error = "Address Field cannot be empty"
                }

                if (inputEmail.isNotEmpty()) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
                        etEmail.error = "Email isn't valid"
                        isEmptyFields = true
                    }
                }

                if (inputPassword.isNotEmpty()) {
                    if (inputPassword.length < 5) {
                        etPassword.error = "Minimum 5 Character Password"
                        isEmptyFields = true
                    }
                    if (!inputPassword.matches(".*[A-Z].*".toRegex())) {
                        etPassword.error = "Must contain 1 Upper-case Character"
                        isEmptyFields = true
                    }
                    if (!inputPassword.matches(".*[a-z].*".toRegex())) {
                        etPassword.error = "Must contain 1 Lower-case Character"
                        isEmptyFields = true
                    }
                    if (!inputPassword.matches(".*[@#\$%^&+=!()].*".toRegex())) {
                        etPassword.error = "Must contain 1 Special Character.*[@#\$%^&+=!()"
                        isEmptyFields = true
                    }
                }

                if (!isEmptyFields) {
                    uploadEditedData()
                }
            }
        }
    }

    private fun uploadPhotoButtonClick() {
        bottomSheetSetUp()
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

    private fun uploadEditedData() {
        val data = sharedPref.getString(COMPUTER_STORE_SESSION, null)

        // convert data from shared pref to ComputerStore data class.
        val objectComputerStore = gson.fromJson(data, ComputerStore::class.java)

        binding.apply {
            viewModel.updateComputerStore(
                ComputerStore(
                    id = objectComputerStore.id,
                    isAdmin = objectComputerStore.isAdmin,
                    createAt = Date(),
                    name = etUsername.text.toString(),
                    address = etAddress.text.toString(),
                    image = imageUri.toString(),
                    phone = etPhone.text.toString(),
                    email = etEmail.text.toString(),
                    username = etUsername.text.toString(),
                    password = etPassword.text.toString().encryptCBC(),
                )
            )
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

    private fun bottomSheetSetUp() {
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