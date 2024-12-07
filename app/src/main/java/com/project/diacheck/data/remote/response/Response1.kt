package com.project.diacheck.data.remote.response

import com.google.gson.annotations.SerializedName

data class Response1(

	@field:SerializedName("response_code")
	val responseCode: Int? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Data(

	@field:SerializedName("probability")
	val probability: Any? = null,

	@field:SerializedName("prediction")
	val prediction: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("probabilities")
	val probabilities: List<List<Any?>?>? = null
)
