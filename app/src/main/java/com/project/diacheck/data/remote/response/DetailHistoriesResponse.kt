package com.project.diacheck.data.remote.response

data class DetailHistoriesResponse(
    val message: String,
    val data: List<History>
)

data class History(
    val id_history: Int,
    val id_users: Int,
    val history: String,
    val gender: Int,
    val hypertension: Int,
    val heart_disease: Int,
    val age: Int,
    val bmi: Float,
    val hbA1c: Float,
    val blood_glucose: Int,
    val result: Int,
    val created_at: String,
    val updated_at: String
)