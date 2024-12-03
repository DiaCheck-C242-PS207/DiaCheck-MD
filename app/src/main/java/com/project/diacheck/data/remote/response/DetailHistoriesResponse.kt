package com.project.diacheck.data.remote.response

data class DetailHistoriesResponse(
    val gender: Int,
    val age: Int,
    val hypertension: Int,
    val heartDisease: Int,
    val bmi: Float,
    val hbA1c: Float,
    val bloodGlucose: Float
)