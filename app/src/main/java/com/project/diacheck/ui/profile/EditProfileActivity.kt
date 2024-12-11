package com.project.diacheck.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.project.diacheck.R
import com.project.diacheck.databinding.ActivityEditProfileBinding
import com.project.diacheck.getImageUri
import com.project.diacheck.reduceFileImage
import com.project.diacheck.ui.ViewModelFactory
import com.project.diacheck.uriToFile
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class EditProfileActivity : AppCompatActivity() {
    private var userId: Int? = null
    private var userName: String? = null

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeUserData()
    }

    private fun setupUI() {
        binding.profileImage.setOnClickListener { showPhotoOptionsDialog() }

        binding.saveButton.setOnClickListener { saveUserProfile() }

        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this, ProfileFragment::class.java))
        }
    }

    private fun observeUserData() {
        lifecycleScope.launch {
            try {
                viewModel.getUserSession().collectLatest { user ->
                    userId = user.id_users
                    userName = user.name

                    binding.editName.setText(user.name)

                    Glide.with(this@EditProfileActivity)
                        .load(user.avatar)
                        .placeholder(R.drawable.ic_placeholder_profile)
                        .circleCrop()
                        .into(binding.profileImage)
                }
            } catch (e: Exception) {
                Log.e("EditProfileActivity", "Failed to get userId: ${e.message}")
                Toast.makeText(
                    this@EditProfileActivity,
                    "Gagal mengambil informasi pengguna. Coba lagi.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun saveUserProfile() {
        lifecycleScope.launch {
            try {
                val nameInput = binding.editName.text.toString().trim()
                val nameChanged = nameInput.isNotEmpty() && nameInput != userName?.trim()

                val imageUriChanged = viewModel.currentImageUri.value != null

                val updatedName = if (nameChanged) nameInput else null
                val updatedImageUri = if (imageUriChanged) viewModel.currentImageUri.value else null

                Log.d("EditProfile", "Updating user with id: $userId, name: $updatedName, uri: $updatedImageUri")

                viewModel.updateUser(
                    userId,
                    updatedName,
                    null,
                    null,
                    this@EditProfileActivity,
                    updatedImageUri
                )
            } catch (e: SocketTimeoutException) {
                Log.e("EditProfileActivity", "Request timeout: ${e.message}")
                Toast.makeText(
                    this@EditProfileActivity,
                    "Server terlalu lama merespons. Coba lagi nanti.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleImageFromUri(uri: Uri?) {
        uri?.let { imageUri ->
            Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.ic_placeholder_profile)
                .circleCrop()
                .into(binding.profileImage)

            lifecycleScope.launch {
                val file = uriToFile(imageUri, this@EditProfileActivity)
                file.reduceFileImage()
            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setCurrentImageUri(uri)
            handleImageFromUri(uri)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            val uri = viewModel.currentImageUri.value
            if (uri != null) {
                handleImageFromUri(uri)
            }
        } else {
            viewModel.setCurrentImageUri(null)
        }
    }

    private fun startCamera() {
        viewModel.setCurrentImageUri(getImageUri(this))
        launcherIntentCamera.launch(viewModel.currentImageUri.value!!)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showPhotoOptionsDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Photo Option")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startCamera()
                    1 -> startGallery()
                }
            }
            .show()
    }
}
