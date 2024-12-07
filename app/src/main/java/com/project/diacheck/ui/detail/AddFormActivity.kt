package com.project.diacheck.ui.detail

import android.content.Intent
import android.gesture.Prediction
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.project.diacheck.R
import com.project.diacheck.data.remote.response.PredictionData
import com.project.diacheck.data.remote.response.SubmitFormItem
import com.project.diacheck.data.remote.retrofit.ApiML
import com.project.diacheck.databinding.ActivityAddFormBinding
import com.project.diacheck.ui.ViewModelFactory
import com.project.diacheck.ui.form.FormViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException

class AddFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddFormBinding
    private val viewModel by viewModels<FormViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(
                ContextCompat.getDrawable(
                    this@AddFormActivity,
                    R.drawable.ic_arrow_back
                )
            )
            title = getString(R.string.title_add)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val etAge: EditText = findViewById(R.id.etAge)
        val etBMI: EditText = findViewById(R.id.etBMI)
        val etHbA1c: EditText = findViewById(R.id.etHbA1c)
        val etBloodGlucose: EditText = findViewById(R.id.etBloodGlucose)
        val rgGender: RadioGroup = findViewById(R.id.rgGender)
        val rgHypertension: RadioGroup = findViewById(R.id.rgHypertension)
        val rgHeartDisease: RadioGroup = findViewById(R.id.rgHeartDisease)
        val btnSubmit: Button = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            try {
                val age =
                    etAge.text.toString().toFloatOrNull() ?: throw Exception("Age is required")
                val bmi =
                    etBMI.text.toString().toFloatOrNull() ?: throw Exception("BMI is required")
                val hbA1c =
                    etHbA1c.text.toString().toFloatOrNull() ?: throw Exception("HbA1c is required")
                val bloodGlucose = etBloodGlucose.text.toString().toFloatOrNull()
                    ?: throw Exception("Blood glucose is required")

                val gender = when (rgGender.checkedRadioButtonId) {
                    R.id.rbMale -> 1f
                    R.id.rbFemale -> 0f
                    else -> throw Exception("Gender not selected")
                }

                val hypertension = when (rgHypertension.checkedRadioButtonId) {
                    R.id.rbHypertensionYes -> 1f
                    R.id.rbHypertensionNo -> 0f
                    else -> throw Exception("Hypertension not selected")
                }

                val heartDisease = when (rgHeartDisease.checkedRadioButtonId) {
                    R.id.rbHeartDiseaseYes -> 1f
                    R.id.rbHeartDiseaseNo -> 0f
                    else -> throw Exception("Heart disease not selected")
                }

//                val formItem = SubmitFormItem(
//                    gender = gender,
//                    age = age,
//                    hypertension = hypertension,
//                    heartDisease = heartDisease,
//                    bmi = bmi,
//                    HbA1cLevel = hbA1c,
//                    bloodGlucoseLevel = bloodGlucose
//                )
//
//                viewModel.submitForm(formItem)
//
//                val result = PredictionData(
//                    message = ,
//                    probability = ,
//                    prediction =
//                )

                val submitData =
                    SubmitFormItem(
                        gender,
                        age,
                        hypertension,
                        heartDisease,
                        bmi,
                        hbA1c,
                        bloodGlucose
                    )

                lifecycleScope.launch {
                    try {
                        showLoading(true)
                        val response = ApiML.getApiML().predict(submitData)
                        showLoading(false)

                        if (response.success == true) {
                            val intent = Intent(this@AddFormActivity, DetailActivity::class.java)
                            intent.putExtra("input_age", age)
                            intent.putExtra("input_gender", gender)
                            intent.putExtra("input_hypertension", hypertension)
                            intent.putExtra("input_bmi", 25.0f)
                            intent.putExtra("prediction", response.data?.prediction)
                            intent.putExtra("prediction_message", response.data?.message)
                            intent.putExtra("prediction_probability", response.data?.probability)
                            startActivity(intent)
                        } else {
                            Log.e("AddFormActivity", "Request gagal: ${response.message}")
                            Toast.makeText(
                                this@AddFormActivity,
                                "Request gagal: ${response.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: SocketTimeoutException) {
                        showLoading(false) // Sembunyikan loading pada error
                        Log.e("AddFormActivity", "Request timeout: ${e.message}")
                        Toast.makeText(
                            this@AddFormActivity,
                            "Server terlalu lama merespons. Coba lagi nanti.",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: IOException) {
                        showLoading(false) // Sembunyikan loading pada error
                        Log.e("AddFormActivity", "Error jaringan: ${e.message}")
                        Toast.makeText(
                            this@AddFormActivity,
                            "Terjadi masalah koneksi. Periksa jaringan Anda.",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        showLoading(false) // Sembunyikan loading pada error
                        Log.e("AddFormActivity", "Error tidak terduga: ${e.message}", e)
                        Toast.makeText(
                            this@AddFormActivity,
                            "Terjadi error: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }


//                viewModel.submitFormToCloud(formItem).observe(this) { result ->
//                    when (result) {
//                        is Result.Success -> {
//                            val prediction = result.data.responseCode
//                            val intent = Intent(this, DetailActivity::class.java).apply {
//                                putExtra("PREDICTION_RESULT", prediction)
//                            }
//                            startActivity(intent)
//                        }
//                        is Result.Error -> {
//                            Toast.makeText(
//                                this,
//                                "Error: ${result.error}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                        is Result.Loading -> {
//                            showLoading(false)
//                        }
//                    }
//                }

//                val input = floatArrayOf(gender, age, hypertension, heartDisease, bmi, hbA1c, bloodGlucose)

//                val intent = Intent(this, DetailActivity::class.java).apply {
//                    putExtra("PREDICTION_INPUT", input)
//                }
//                startActivity(intent)

            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Please fill all fields correctly. Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.linearProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
