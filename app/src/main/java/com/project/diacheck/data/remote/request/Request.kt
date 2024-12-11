package com.project.diacheck.data.remote.request

data class LoginRequest(
    val email: String,
    val password: String
)
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
data class CreateHistoryRequest(
    val id_users: Int,
    val history: String,
    val gender: Int,
    val hypertension: Int,
    val heart_disease: Int,
    val age: Int,
    val bmi: Float,
    val hbA1c: Float,
    val blood_glucose: Int,
    val result: Int
)