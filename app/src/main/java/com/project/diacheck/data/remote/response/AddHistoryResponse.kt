package com.project.diacheck.data.remote.response

data class AddHistoryResponse(
    val message: String,
    val data: CreateHistoryData
)

data class CreateHistoryData(
    val id_users: Int,
    val history: String,
    val gender: Int,
    val hypertension: Int,
    val heart_disease: Int,
    val age: Int,
    val bmi: Float,
    val hbA1c: Float,
    val blood_glucose: Float,
    val result: Int
)
