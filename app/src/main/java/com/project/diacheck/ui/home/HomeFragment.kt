package com.project.diacheck.ui.home

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
    }

    private fun autoScrollImages() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val nextItem = (imageSlider.currentItem + 1) % imageSlider.adapter!!.itemCount
                imageSlider.currentItem = nextItem
                handler.postDelayed(this, 3000) // 3 detik
            }
        }
        handler.postDelayed(runnable, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

