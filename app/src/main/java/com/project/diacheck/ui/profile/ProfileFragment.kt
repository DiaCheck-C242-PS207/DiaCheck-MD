package com.project.diacheck.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.project.diacheck.R
import com.project.diacheck.databinding.FragmentProfileBinding
import com.project.diacheck.getImageUri
import com.project.diacheck.reduceFileImage
import com.project.diacheck.ui.ViewModelFactory
import com.project.diacheck.uriToFile
import com.project.diacheck.data.Result
import com.project.diacheck.data.local.settings.ThemePreference
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.work.*
import com.project.diacheck.NotificationPermissionUtil
import com.project.diacheck.ui.worker.NotificationWorker
import java.util.concurrent.TimeUnit

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        val isDarkMode = ThemePreference.isDarkMode(requireContext())
        binding.switchTheme.isChecked = isDarkMode

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            ThemePreference.setDarkMode(requireContext(), isChecked)
            requireActivity().recreate()
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    scheduleNotification()
                } else {
                    requestNotificationPermission()
                }
            } else {
                cancelNotification()
            }
        }

        val profileImageView: ShapeableImageView = binding.profileUser
        val layoutParams = profileImageView.layoutParams
        layoutParams.width = 200
        layoutParams.height = 200
        profileImageView.layoutParams = layoutParams

        val nameTextView: TextView = binding.name
        val emailTextView: TextView = binding.email
        val logoutButton: Button = binding.logoutButton

        lifecycleScope.launch {
            viewModel.getUserSession().collectLatest { user ->
                nameTextView.text = user.name
                emailTextView.text = user.email
                Glide.with(this@ProfileFragment)
                    .load(user.avatar)
                    .placeholder(R.drawable.ic_placeholder_profile)
                    .circleCrop()
                    .into(profileImageView)
            }
        }

        binding.editPhotoButton.setOnClickListener {
            showPhotoOptionsDialog()
        }

        logoutButton.setOnClickListener {
            viewModel.logout()
            requireActivity().finish()
        }

        return view
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            binding.profileUser.setImageURI(uri)
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
                binding.profileUser.setImageURI(uri)
                handleImageFromUri(uri)
            }
        } else {
            viewModel.setCurrentImageUri(null)
        }
    }


    private fun handleImageFromUri(uri: Uri?) {
        uri?.let { imageUri ->
            lifecycleScope.launch {
                val file = uriToFile(imageUri, requireContext())
                val reducedFile = file.reduceFileImage()
                sendImageToApi(reducedFile)
            }
        }
    }

    private fun sendImageToApi(file: File) {
        viewModel.uploadImage(file).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Upload Successful", Toast.LENGTH_SHORT).show()
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.linearProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    private fun startCamera() {
        viewModel.setCurrentImageUri(getImageUri(requireContext()))
        launcherIntentCamera.launch(viewModel.currentImageUri.value!!)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showPhotoOptionsDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Photo Option")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startCamera()
                    1 -> startGallery()
                }
            }
            .show()
    }

    private fun requestNotificationPermission() {
        if (hasAskedNotificationPermission()) {
            Toast.makeText(
                requireContext(),
                "Permission already requested",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                scheduleNotification()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Notification permission denied",
                    Toast.LENGTH_SHORT
                ).show()
                binding.switchNotifications.isChecked = false
            }
            setAskedNotificationPermission()
        }
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }


    private fun hasAskedNotificationPermission(): Boolean {
        val prefs = requireContext().getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("asked_permission", false)
    }

    private fun setAskedNotificationPermission() {
        val prefs = requireContext().getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("asked_permission", true).apply()
    }


    private fun scheduleNotification() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(8, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "8HourNotification",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
        Toast.makeText(requireContext(), "Notifications scheduled", Toast.LENGTH_SHORT).show()
    }

    private fun cancelNotification() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("8HourNotification")
        Toast.makeText(requireContext(), "Notifications canceled", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
