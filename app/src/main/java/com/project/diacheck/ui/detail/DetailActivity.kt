package com.project.diacheck.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.card.MaterialCardView
import com.project.diacheck.R
import com.project.diacheck.databinding.ActivityDetailBinding
import com.project.diacheck.ui.ViewModelFactory
import com.project.diacheck.ui.form.FormViewModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var formViewModel: FormViewModel

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val age = intent.getIntExtra("input_age", 0)
        val gender = intent.getIntExtra("input_gender", 0)
        val hypertension = intent.getIntExtra("input_hypertension", 0)
        val heartdisease = intent.getIntExtra("input_heartDisease", 0)
        val bmi = intent.getFloatExtra("input_bmi", 0f)
        val hbA1c = intent.getFloatExtra("input_hbA1c", 0f)
        val bloodGlucose = intent.getIntExtra("input_bloodGlucose", 0)

        binding.textViewInputAge.text = "Usia: $age tahun"
        binding.textViewInputGender.text = if (gender == 1) "Laki-laki" else "Perempuan"
        binding.textViewInputHypertension.text =
            "Hipertensi: ${if (hypertension == 1) "Ya" else "Tidak"}"
        binding.textViewInputHeartDisease.text =
            "Penyakit Jantung: ${if (heartdisease == 1) "Ya" else "Tidak"}"
        binding.textViewInputBMI.text = "BMI: $bmi"
        binding.textViewInputHbA1c.text = "HbA1c: $hbA1c"
        binding.textViewInputBloodGlucose.text = "Gula Darah: $bloodGlucose"

        val prediction = intent.getIntExtra("prediction", -1)
        val predictionMessage = intent.getStringExtra("prediction_message")
        val predictionProbability = intent.getFloatExtra("prediction_probability", -1f)

        val textViewPrediction = findViewById<TextView>(R.id.textViewPrediction)
        val textViewMessage = findViewById<TextView>(R.id.textViewMessage)
        val cardPrediction = findViewById<MaterialCardView>(R.id.cardPrediction)

        val predictionResult = if (prediction == 2) {
            "Terdeteksi diabetes"
        } else if (prediction == 1) {
            "Tidak terdeteksi diabetes"
        } else {
            "Hasil tidak valid"
        }

        textViewPrediction.text = predictionResult
        textViewMessage.text = predictionMessage ?: "Tidak ada pesan"

        val strokeColor = when {
            predictionProbability in 0f..20f -> R.color.light_green
            predictionProbability in 21f..50f -> R.color.light_yellow
            predictionProbability in 51f..100f -> R.color.light_red
            else -> R.color.gray
        }

        cardPrediction.setStrokeColor(ContextCompat.getColor(this, strokeColor))

        val factory = ViewModelFactory.getInstance(applicationContext)
        formViewModel = ViewModelProvider(this, factory).get(FormViewModel::class.java)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(
                ContextCompat.getDrawable(
                    this@DetailActivity,
                    R.drawable.ic_arrow_back
                )
            )
            title = getString(R.string.title_result)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.linearProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_FORM_ID = "extra_form_id"
    }
}

