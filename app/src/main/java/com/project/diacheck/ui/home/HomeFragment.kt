package com.project.diacheck.ui.home

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.project.diacheck.R
import com.project.diacheck.databinding.FragmentHomeBinding
import com.project.diacheck.ui.ViewModelFactory
import com.project.diacheck.ui.adapter.ImageSliderAdapter
import com.project.diacheck.ui.profile.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageSlider: ViewPager2
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageSlider = binding.imageSlider

        val images = listOf(
            R.drawable.ic_google,
            R.drawable.diacheck_black,
            R.drawable.dia_light
        )

        val adapter = ImageSliderAdapter(images)
        imageSlider.adapter = adapter
        TabLayoutMediator(binding.dotsIndicator, imageSlider) { _, _ -> }.attach()
        autoScrollImages()

        val nameTextView: TextView = binding.username
        lifecycleScope.launch {
            viewModel.getUserSession().collectLatest { user ->
                nameTextView.text = user.name
            }
        }

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                sendNotification("Daily checklist terpenuhi. Great job!")
            } else {
                Toast.makeText(
                    requireContext(),
                    "Notification permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        resetChecklistIfNewDay()
        setupChecklistListeners()
    }

    private fun autoScrollImages() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val nextItem = (imageSlider.currentItem + 1) % imageSlider.adapter!!.itemCount
                imageSlider.currentItem = nextItem
                handler.postDelayed(this, 6000) // detik
            }
        }
        handler.postDelayed(runnable, 6000)
    }

    private fun setupChecklistListeners() {
        val checkWater = binding.checkWater
        val checkExercise = binding.checkExercise
        val checkWalking = binding.checkWalking

        val checklistItems = listOf(checkWater, checkExercise, checkWalking)

        checklistItems.forEach { checkbox ->
            checkbox.setOnCheckedChangeListener { _, _ ->
                if (checklistItems.all { it.isChecked }) {
                    sendNotification("Daily checklist terpenuhi. Great job!")
                }
            }
        }
    }

    private fun isNewDay(context: Context): Boolean {
        val prefs = context.getSharedPreferences("daily_checklist_prefs", Context.MODE_PRIVATE)
        val lastDate = prefs.getString("last_checklist_date", null)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (lastDate == null || lastDate != currentDate) {
            prefs.edit().putString("last_checklist_date", currentDate).apply()
            return true
        }
        return false
    }

    private fun resetChecklistIfNewDay() {
        if (isNewDay(requireContext())) {
            binding.checkWater.isChecked = false
            binding.checkExercise.isChecked = false
            binding.checkWalking.isChecked = false
        }
    }

    private fun sendNotification(message: String) {
        val channelId = "daily_checklist_channel"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Checklist Notifications"
            val descriptionText = "Notifications for daily checklist status"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.diacheck_black)
            .setContentTitle("Daily Checklist")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
            notify(notificationId, builder.build())
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

