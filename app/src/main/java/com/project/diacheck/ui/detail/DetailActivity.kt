package com.project.diacheck.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.project.diacheck.R
import com.project.diacheck.data.Result
import com.project.diacheck.databinding.ActivityDetailBinding
import com.project.diacheck.ml.DiabetesClassifierHelper
import com.project.diacheck.ui.ViewModelFactory
import com.project.diacheck.ui.form.FormViewModel
import com.google.android.material.card.MaterialCardView

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
//    private lateinit var diabetesClassifier: DiabetesClassifierHelper
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
        binding.textViewInputHypertension.text = "Hipertensi: ${if (hypertension == 1) "Ya" else "Tidak"}"
        binding.textViewInputHeartDisease.text = "Penyakit Jantung: ${if (heartdisease == 1) "Ya" else "Tidak"}"
        binding.textViewInputBMI.text = "BMI: $bmi"
        binding.textViewInputHbA1c.text = "HbA1c: $hbA1c"
        binding.textViewInputBloodGlucose.text = "Gula Darah: $bloodGlucose"

        val prediction = intent.getIntExtra("prediction", -1)
        val predictionMessage = intent.getStringExtra("prediction_message")
        val predictionProbability = intent.getFloatExtra("prediction_probability", 0f)

        val textViewPrediction = findViewById<TextView>(R.id.textViewPrediction)
        val textViewMessage = findViewById<TextView>(R.id.textViewMessage)
        val textViewProbability = findViewById<TextView>(R.id.textViewProbability)
        val cardPrediction = findViewById<MaterialCardView>(R.id.cardPrediction)

        val predictionResult = if (prediction == 1) {
            "Terdeteksi diabetes"
        } else if (prediction == 0) {
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

//        diabetesClassifier = DiabetesClassifierHelper(this)

        val factory = ViewModelFactory.getInstance(applicationContext)
        formViewModel = ViewModelProvider(this, factory).get(FormViewModel::class.java)

//        val inputData = intent.getFloatArrayExtra("PREDICTION_INPUT")
//        val formId = intent.getIntExtra(EXTRA_FORM_ID, 0)
//
//        if (formId != 0) {
//            formViewModel.getHistoryById(formId)
//            formViewModel.historyDetail.observe(this) { result ->
//                when (result) {
//                    is Result.Success -> {
//                        val history = result.data
//                        val predictionInputData = floatArrayOf(
//                            history.gender.toFloat(),
//                            history.age.toFloat(),
//                            history.hypertension.toFloat(),
//                            history.heartDisease.toFloat(),
//                            history.bmi,
//                            history.hbA1c,
//                            history.bloodGlucose
//                        )
//
//                        val predictionResult = diabetesClassifier.predict(predictionInputData)
//
//                        val predictionPercentage =
//                            predictionResult.replace("%", "").toIntOrNull() ?: 0
//
//                        val (category, explanation) = when {
//                            predictionPercentage in 0..10 -> "Tidak Berpotensi Diabetes" to "Risiko sangat rendah untuk terkena diabetes. Pola hidup sehat disarankan untuk mempertahankan kondisi ini."
//                            predictionPercentage in 11..20 -> "Tidak Berpotensi Diabetes" to "Risiko rendah. Terus pertahankan gaya hidup sehat, termasuk pola makan dan olahraga teratur."
//                            predictionPercentage in 21..30 -> "Kemungkinan Diabetes" to "Risiko mulai meningkat. Disarankan untuk melakukan pemeriksaan lebih lanjut dan menjaga pola makan yang seimbang."
//                            predictionPercentage in 31..40 -> "Kemungkinan Diabetes" to "Risiko sedang. Perlu konsultasi dengan dokter untuk mendapatkan rekomendasi terkait gaya hidup dan pengawasan kesehatan."
//                            predictionPercentage in 41..50 -> "Diabetes Ringan" to "Tanda-tanda awal diabetes terlihat. Anda perlu menjaga pola makan dan melakukan pemeriksaan rutin untuk menghindari komplikasi."
//                            predictionPercentage in 51..60 -> "Diabetes Ringan" to "Risiko diabetes semakin nyata. Disarankan untuk memonitor kadar gula darah secara rutin dan mengikuti saran medis."
//                            predictionPercentage in 61..70 -> "Diabetes Sedang" to "Terdapat gejala awal diabetes yang cukup signifikan. Pemeriksaan lebih lanjut dan perubahan gaya hidup sangat diperlukan."
//                            predictionPercentage in 71..80 -> "Diabetes Berat" to "Risiko tinggi terkena diabetes kronis. Anda harus segera berkonsultasi dengan dokter untuk pengelolaan penyakit yang tepat."
//                            predictionPercentage in 81..90 -> "Diabetes Berat" to "Kemungkinan besar diabetes sudah ada. Perawatan medis segera diperlukan untuk mencegah komplikasi lebih lanjut."
//                            predictionPercentage in 91..100 -> "Diabetes Kronis" to "Kondisi diabetes sudah sangat serius. Anda perlu segera mendapatkan pengobatan dan perawatan jangka panjang."
//                            else -> "Invalid Result" to "Data tidak valid. Silakan coba lagi."
//                        }
//
//                        binding.tvResult.text = "Hasil Prediksi: $predictionResult"
//                        binding.tvCategory.text = "Kategori: $category"
//                        binding.tvExplanation.text = "Penjelasan: $explanation"
//                    }
//
//                    is Result.Error -> {
//                        Log.e("DetailActivity", "Error: Data is invalid")
//                    }
//
//                    is Result.Loading -> {
//
//                    }
//                }
//            }
//        }
//
//        if (inputData != null) {
//            val predictionResult = diabetesClassifier.predict(inputData)
//
//            val predictionPercentage = predictionResult.replace("%", "").toIntOrNull() ?: 0
//
//            val (category, explanation) = when {
//                predictionPercentage in 0..10 -> "Tidak Berpotensi Diabetes" to "Risiko sangat rendah untuk terkena diabetes. Pola hidup sehat disarankan untuk mempertahankan kondisi ini."
//                predictionPercentage in 11..20 -> "Tidak Berpotensi Diabetes" to "Risiko rendah. Terus pertahankan gaya hidup sehat, termasuk pola makan dan olahraga teratur."
//                predictionPercentage in 21..30 -> "Kemungkinan Diabetes" to "Risiko mulai meningkat. Disarankan untuk melakukan pemeriksaan lebih lanjut dan menjaga pola makan yang seimbang."
//                predictionPercentage in 31..40 -> "Kemungkinan Diabetes" to "Risiko sedang. Perlu konsultasi dengan dokter untuk mendapatkan rekomendasi terkait gaya hidup dan pengawasan kesehatan."
//                predictionPercentage in 41..50 -> "Diabetes Ringan" to "Tanda-tanda awal diabetes terlihat. Anda perlu menjaga pola makan dan melakukan pemeriksaan rutin untuk menghindari komplikasi."
//                predictionPercentage in 51..60 -> "Diabetes Ringan" to "Risiko diabetes semakin nyata. Disarankan untuk memonitor kadar gula darah secara rutin dan mengikuti saran medis."
//                predictionPercentage in 61..70 -> "Diabetes Sedang" to "Terdapat gejala awal diabetes yang cukup signifikan. Pemeriksaan lebih lanjut dan perubahan gaya hidup sangat diperlukan."
//                predictionPercentage in 71..80 -> "Diabetes Berat" to "Risiko tinggi terkena diabetes kronis. Anda harus segera berkonsultasi dengan dokter untuk pengelolaan penyakit yang tepat."
//                predictionPercentage in 81..90 -> "Diabetes Berat" to "Kemungkinan besar diabetes sudah ada. Perawatan medis segera diperlukan untuk mencegah komplikasi lebih lanjut."
//                predictionPercentage in 91..100 -> "Diabetes Kronis" to "Kondisi diabetes sudah sangat serius. Anda perlu segera mendapatkan pengobatan dan perawatan jangka panjang."
//                else -> "Invalid Result" to "Data tidak valid. Silakan coba lagi."
//            }
//
//            binding.tvResult.text = "Hasil Prediksi: $predictionResult"
//            binding.tvCategory.text = "Kategori: $category"
//            binding.tvExplanation.text = "Penjelasan: $explanation"
//        } else {
//            binding.tvExplanation.text = "Data input tidak ditemukan"
//        }

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

    companion object {
        const val EXTRA_FORM_ID = "extra_form_id"
    }
}

