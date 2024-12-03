package com.project.diacheck.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.project.diacheck.R
import com.project.diacheck.data.Result
import com.project.diacheck.databinding.ActivityDetailBinding
import com.project.diacheck.ml.DiabetesClassifierHelper
import com.project.diacheck.ui.ViewModelFactory
import com.project.diacheck.ui.form.FormViewModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var diabetesClassifier: DiabetesClassifierHelper
    private lateinit var formViewModel: FormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        diabetesClassifier = DiabetesClassifierHelper(this)

        val factory = ViewModelFactory.getInstance(applicationContext)
        formViewModel = ViewModelProvider(this, factory).get(FormViewModel::class.java)

        val inputData = intent.getFloatArrayExtra("PREDICTION_INPUT")
        val formId = intent.getIntExtra(EXTRA_FORM_ID, 0)

        // Ambil data dari API berdasarkan formId
        if (formId != 0) {
            formViewModel.getHistoryById(formId)
            formViewModel.historyDetail.observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        val history = result.data
                        // Menyusun inputData berdasarkan data dari database
                        val predictionInputData = floatArrayOf(
                            history.gender.toFloat(),   // Convert gender ke float jika perlu
                            history.age.toFloat(),
                            history.hypertension.toFloat(),
                            history.heartDisease.toFloat(),
                            history.bmi.toFloat(),
                            history.hbA1c.toFloat(),
                            history.bloodGlucose.toFloat()
                        )

                        // Prediksi dengan data yang sudah disusun
                        val predictionResult = diabetesClassifier.predict(predictionInputData)

                        val predictionPercentage =
                            predictionResult?.replace("%", "")?.toIntOrNull() ?: 0

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
                    }

                    is Result.Error -> {
                        Log.e("DetailActivity", "Error: Data is invalid")
                    }

                    is Result.Loading -> {
                        // Tampilkan loading jika sedang mengambil data
                    }
                }
            }
        }

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

        // Toolbar setup
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

