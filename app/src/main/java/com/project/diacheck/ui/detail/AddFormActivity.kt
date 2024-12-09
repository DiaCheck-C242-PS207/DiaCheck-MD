package com.project.diacheck.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
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
import com.project.diacheck.data.Result
import com.project.diacheck.data.remote.request.CreateHistoryRequest
import com.project.diacheck.data.remote.response.SubmitFormItem
import com.project.diacheck.data.remote.retrofit.ApiML
import com.project.diacheck.databinding.ActivityAddFormBinding
import com.project.diacheck.ui.ViewModelFactory
import com.project.diacheck.ui.form.FormViewModel
import com.project.diacheck.ui.profile.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException

class AddFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddFormBinding
    private val viewModel by viewModels<FormViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var userId: Int? = null

    @SuppressLint("DefaultLocale")
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

        lifecycleScope.launch {
            try {
                profileViewModel.getUserSession().collectLatest { user ->
                    userId = user.id_users
                }
            } catch (e: Exception) {
                Log.e("AddFormActivity", "Failed to get userId: ${e.message}")
                Toast.makeText(
                    this@AddFormActivity,
                    "Gagal mengambil informasi pengguna. Coba lagi.",
                    Toast.LENGTH_LONG
                ).show()
            }
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
                    etAge.text.toString().toIntOrNull() ?: throw Exception("Age is required")
                val bmi =
                    etBMI.text.toString().toFloatOrNull() ?: throw Exception("BMI is required")
                val hbA1c =
                    etHbA1c.text.toString().toFloatOrNull() ?: throw Exception("HbA1c is required")
                val bloodGlucose = etBloodGlucose.text.toString().toIntOrNull()
                    ?: throw Exception("Blood glucose is required")

                val gender = when (rgGender.checkedRadioButtonId) {
                    R.id.rbMale -> 1
                    R.id.rbFemale -> 0
                    else -> throw Exception("Gender not selected")
                }

                val hypertension = when (rgHypertension.checkedRadioButtonId) {
                    R.id.rbHypertensionYes -> 1
                    R.id.rbHypertensionNo -> 0
                    else -> throw Exception("Hypertension not selected")
                }

                val heartDisease = when (rgHeartDisease.checkedRadioButtonId) {
                    R.id.rbHeartDiseaseYes -> 1
                    R.id.rbHeartDiseaseNo -> 0
                    else -> throw Exception("Heart disease not selected")
                }

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
                            val probability = response.data?.probability ?: 0.0
                            val formattedProbability = String.format("%.2f", probability)
                            val historyMessage = "${response.data?.message} Dengan tingkat kemungkinan $formattedProbability%"
                            val prediction = response.data?.prediction ?: 0
                            intent.putExtra("input_age", age)
                            intent.putExtra("input_gender", gender)
                            intent.putExtra("input_hypertension", hypertension)
                            intent.putExtra("input_heartDisease", heartDisease)
                            intent.putExtra("input_bmi", bmi)
                            intent.putExtra("input_hbA1c", hbA1c)
                            intent.putExtra("input_bloodGlucose", bloodGlucose)
                            intent.putExtra("prediction", prediction)
                            intent.putExtra("prediction_message", historyMessage)
                            intent.putExtra("prediction_probability", probability)

                            val request = CreateHistoryRequest(
                                id_users = userId!!,
                                history = historyMessage,
                                gender = gender,
                                hypertension = hypertension,
                                heart_disease = heartDisease,
                                age = age,
                                bmi = bmi,
                                hbA1c = hbA1c,
                                blood_glucose = bloodGlucose,
                                result = prediction
                            )

                            viewModel.createHistory(request)
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
                        showLoading(false)
                        Log.e("AddFormActivity", "Request timeout: ${e.message}")
                        Toast.makeText(
                            this@AddFormActivity,
                            "Server terlalu lama merespons. Coba lagi nanti.",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: IOException) {
                        showLoading(false)
                        Log.e("AddFormActivity", "Error jaringan: ${e.message}")
                        Toast.makeText(
                            this@AddFormActivity,
                            "Terjadi masalah koneksi. Periksa jaringan Anda.",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        showLoading(false)
                        Log.e("AddFormActivity", "Error tidak terduga: ${e.message}", e)
                        Toast.makeText(
                            this@AddFormActivity,
                            "Terjadi error: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Please fill all fields correctly. Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.createHistoryResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "History created successfully!", Toast.LENGTH_LONG).show()
                }

                is Result.Error -> {
                    showLoading(false)
                }
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.linearProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
