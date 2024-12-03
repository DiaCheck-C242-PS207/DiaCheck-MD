package com.project.diacheck.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.project.diacheck.R
import com.project.diacheck.databinding.FragmentHomeBinding
import com.project.diacheck.ui.adapter.ImageSliderAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageSlider: ViewPager2
    private lateinit var dotsIndicator: TabLayoutMediator

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

        // Hubungkan ViewPager2 dengan TabLayout untuk indikator
        TabLayoutMediator(binding.dotsIndicator, imageSlider) { _, _ -> }.attach()

        // Aktifkan fitur auto-scroll
        autoScrollImages()
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

