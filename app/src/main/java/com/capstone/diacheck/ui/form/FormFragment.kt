package com.capstone.diacheck.ui.form

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.capstone.diacheck.R
import com.capstone.diacheck.ml.DiabetesClassifierHelper

class FormFragment : Fragment() {

    private lateinit var classifierHelper: DiabetesClassifierHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_form, container, false)

        // Initialize classifier
        classifierHelper = DiabetesClassifierHelper(requireContext())

        // Initialize views
        val etAge: EditText = view.findViewById(R.id.etAge)
        val etBMI: EditText = view.findViewById(R.id.etBMI)
        val etHbA1c: EditText = view.findViewById(R.id.etHbA1c)
        val etBloodGlucose: EditText = view.findViewById(R.id.etBloodGlucose)
        val rgGender: RadioGroup = view.findViewById(R.id.rgGender)
        val rgHypertension: RadioGroup = view.findViewById(R.id.rgHypertension)
        val rgHeartDisease: RadioGroup = view.findViewById(R.id.rgHeartDisease)
        val btnSubmit: Button = view.findViewById(R.id.btnSubmit)
        val tvResult: TextView = view.findViewById(R.id.tvResult)

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
                Toast.makeText(context, "Please fill all fields correctly. Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        classifierHelper.close()
    }
}
