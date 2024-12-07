package com.project.diacheck.data.remote.response

import com.google.gson.annotations.SerializedName

data class PredictionResponse(
    @field:SerializedName("response_code")
    val responseCode: Int? = null,

    @field:SerializedName("data")
    val data: PredictionData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class PredictionData(
    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("probability")
    val probability: Float? = null,

    @field:SerializedName("prediction")
    val prediction: Int? = null,

    @field:SerializedName("probabilities")
    val probabilities: List<List<Float>>? = null
)

