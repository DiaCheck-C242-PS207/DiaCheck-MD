package com.project.diacheck.ui.home

import android.content.Context
import android.content.Intent
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
import com.project.diacheck.ui.detail.AddFormActivity
import com.project.diacheck.ui.profile.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

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

        binding.btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddFormActivity::class.java)
            startActivity(intent)
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
                handler.postDelayed(this, 6000) // 6 detik
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
                    // Tindakan jika checklist lengkap
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


