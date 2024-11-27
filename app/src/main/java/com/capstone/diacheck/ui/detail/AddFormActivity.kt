package com.capstone.diacheck.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.capstone.diacheck.R
import com.capstone.diacheck.databinding.ActivityAddFormBinding
import com.capstone.diacheck.ml.DiabetesClassifierHelper
import com.capstone.diacheck.ui.ViewModelFactory
import com.capstone.diacheck.ui.form.FormViewModel
import com.capstone.diacheck.ui.main.MainViewModel

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
                // Get inputs
                val age = etAge.text.toString().toFloatOrNull() ?: throw Exception("Age is required")
                val bmi = etBMI.text.toString().toFloatOrNull() ?: throw Exception("BMI is required")
                val hbA1c = etHbA1c.text.toString().toFloatOrNull() ?: throw Exception("HbA1c is required")
                val bloodGlucose = etBloodGlucose.text.toString().toFloatOrNull() ?: throw Exception("Blood glucose is required")

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

                // Prepare input array
                val input = floatArrayOf(gender, age, hypertension, heartDisease, bmi, hbA1c, bloodGlucose)

                // Pass result to DetailActivity


            } catch (e: Exception) {
                Toast.makeText(this, "Please fill all fields correctly. Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
