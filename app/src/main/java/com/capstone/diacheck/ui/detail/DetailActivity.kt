package com.capstone.diacheck.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.capstone.diacheck.R
import com.capstone.diacheck.databinding.ActivityDetailBinding
import com.capstone.diacheck.ml.DiabetesClassifierHelper

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var diabetesClassifier: DiabetesClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
    }
}