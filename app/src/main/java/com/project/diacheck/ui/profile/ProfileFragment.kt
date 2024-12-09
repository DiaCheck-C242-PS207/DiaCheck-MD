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
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.work.*
import com.project.diacheck.ui.worker.NotificationWorker
import java.util.concurrent.TimeUnit

class ProfileFragment : Fragment() {
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                scheduleNotification()
                Toast.makeText(requireContext(), "Notifications enabled", Toast.LENGTH_SHORT).show()
            } else {
                binding.switchNotifications.isChecked = false
                Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
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
            val dialog = EditProfileDialogFragment()
            dialog.setOnSaveListener { newName, newImageUri ->
                if (newImageUri != null) {
                    handleImageFromUri(newImageUri)
                }
                binding.name.text = newName
                lifecycleScope.launch {
                    updateUserProfile()
                }
            }
            dialog.show(parentFragmentManager, "EditProfileDialog")
        }


        logoutButton.setOnClickListener {
            viewModel.logout()
            requireActivity().finish()
        }

        return view
    }

    private fun handleImageFromUri(uri: Uri?) {
        uri?.let { imageUri ->
            lifecycleScope.launch {
                val file = uriToFile(imageUri, requireContext())
                val reducedFile = file.reduceFileImage()
            }
        }
    }

    private fun requestNotificationPermission() {
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun scheduleNotification() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(8, TimeUnit.HOURS).build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "8HourNotification",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun cancelNotification() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("8HourNotification")
    }


    private suspend fun updateUserProfile() {
        val userId: Unit = viewModel.getUserSession().collectLatest { user -> user.id_users }
        val name = binding.name.text.toString()
        val file = uriToFile(viewModel.currentImageUri.value!!, requireContext())

        viewModel.updateUser(userId , name,  file).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Profile Updated: ${result.data.message}", Toast.LENGTH_SHORT).show()
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.linearProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
