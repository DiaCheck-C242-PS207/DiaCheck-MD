package com.project.diacheck.data.remote.response

import com.google.gson.annotations.SerializedName

data class FormResponse(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listHistory")
    val listHistory: List<ListFormItem?>? = null
)

data class ListFormItem(

    @field:SerializedName("id_history")
    val id: Int? = null,

    @field:SerializedName("id_users")
    val id_users: Int? = null,

    @field:SerializedName("history")
    val history: String? = null,

    @field:SerializedName("gender")
    val gender: Int,

    @field:SerializedName("hypertension")
    val hypertension: Int,

    @field:SerializedName("heart_disease")
    val heart_disease: Int,

    @field:SerializedName("age")
    val age: Int,

    @field:SerializedName("bmi")
    val bmi: Float,

    @field:SerializedName("hbA1c")
    val hbA1c: Float,

    @field:SerializedName("blood_glucose")
    val blood_glucose: Float,

    @field:SerializedName("result")
    val result: Int? = null,

    @field:SerializedName("created_at")
    val created_at: String? = null,

    @field:SerializedName("updated_at")
    val updated_at: String? = null

)

data class SubmitFormItem(
    @SerializedName("gender") val gender: Int,
    @SerializedName("age") val age: Int,
    @SerializedName("hypertension") val hypertension: Int,
    @SerializedName("heart_disease") val heartDisease: Int,
    @SerializedName("bmi") val bmi: Float,
    @SerializedName("HbA1c_level") val HbA1cLevel: Float,
    @SerializedName("blood_glucose_level") val bloodGlucoseLevel: Int
)

