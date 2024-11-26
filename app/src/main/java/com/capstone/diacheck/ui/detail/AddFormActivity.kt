package com.capstone.diacheck.ui.detail

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.capstone.diacheck.R
import com.capstone.diacheck.ml.DiabetesClassifierHelper

class AddFormActivity : AppCompatActivity() {
    private lateinit var classifierHelper: DiabetesClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_form)

        classifierHelper = DiabetesClassifierHelper(this)

        val etAge: EditText = findViewById(R.id.etAge)
        val etBMI: EditText = findViewById(R.id.etBMI)
        val etHbA1c: EditText = findViewById(R.id.etHbA1c)
        val etBloodGlucose: EditText = findViewById(R.id.etBloodGlucose)
        val rgGender: RadioGroup = findViewById(R.id.rgGender)
        val rgHypertension: RadioGroup = findViewById(R.id.rgHypertension)
        val rgHeartDisease: RadioGroup = findViewById(R.id.rgHeartDisease)
        val btnSubmit: Button = findViewById(R.id.btnSubmit)
        val tvResult: TextView = findViewById(R.id.tvResult)

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

                // Run prediction
                val result = classifierHelper.predict(input)

                // Display result in TextView as percentage
                tvResult.text = "Prediction Result: $result"

            } catch (e: Exception) {
                Toast.makeText(this, "Please fill all fields correctly. Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        classifierHelper.close()
    }
}
