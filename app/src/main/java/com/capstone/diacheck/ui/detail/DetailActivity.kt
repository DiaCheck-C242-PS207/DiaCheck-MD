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

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi model
        diabetesClassifier = DiabetesClassifierHelper(this)

        // Menerima data dari Intent
        val inputData = intent.getFloatArrayExtra("PREDICTION_INPUT")

        if (inputData != null) {
            val predictionResult = diabetesClassifier.predict(inputData)

            val predictionPercentage = predictionResult?.replace("%", "")?.toIntOrNull() ?: 0

            val (category, explanation) = when {
                predictionPercentage in 0..10 -> "Tidak Berpotensi Diabetes" to "Risiko sangat rendah untuk terkena diabetes. Pola hidup sehat disarankan untuk mempertahankan kondisi ini."
                predictionPercentage in 11..20 -> "Tidak Berpotensi Diabetes" to "Risiko rendah. Terus pertahankan gaya hidup sehat, termasuk pola makan dan olahraga teratur."
                predictionPercentage in 21..30 -> "Kemungkinan Diabetes" to "Risiko mulai meningkat. Disarankan untuk melakukan pemeriksaan lebih lanjut dan menjaga pola makan yang seimbang."
                predictionPercentage in 31..40 -> "Kemungkinan Diabetes" to "Risiko sedang. Perlu konsultasi dengan dokter untuk mendapatkan rekomendasi terkait gaya hidup dan pengawasan kesehatan."
                predictionPercentage in 41..50 -> "Diabetes Ringan" to "Tanda-tanda awal diabetes terlihat. Anda perlu menjaga pola makan dan melakukan pemeriksaan rutin untuk menghindari komplikasi."
                predictionPercentage in 51..60 -> "Diabetes Ringan" to "Risiko diabetes semakin nyata. Disarankan untuk memonitor kadar gula darah secara rutin dan mengikuti saran medis."
                predictionPercentage in 61..70 -> "Diabetes Sedang" to "Terdapat gejala awal diabetes yang cukup signifikan. Pemeriksaan lebih lanjut dan perubahan gaya hidup sangat diperlukan."
                predictionPercentage in 71..80 -> "Diabetes Berat" to "Risiko tinggi terkena diabetes kronis. Anda harus segera berkonsultasi dengan dokter untuk pengelolaan penyakit yang tepat."
                predictionPercentage in 81..90 -> "Diabetes Berat" to "Kemungkinan besar diabetes sudah ada. Perawatan medis segera diperlukan untuk mencegah komplikasi lebih lanjut."
                predictionPercentage in 91..100 -> "Diabetes Kronis" to "Kondisi diabetes sudah sangat serius. Anda perlu segera mendapatkan pengobatan dan perawatan jangka panjang."
                else -> "Invalid Result" to "Data tidak valid. Silakan coba lagi."
            }

            binding.tvResult.text = "Hasil Prediksi: $predictionResult"
            binding.tvCategory.text = "Kategori: $category"
            binding.tvExplanation.text = "Penjelasan: $explanation"
        } else {
            binding.tvExplanation.text = "Data input tidak ditemukan"
        }

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
            title = getString(R.string.title_add)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}