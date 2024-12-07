package com.project.diacheck.data.remote.response

import com.google.gson.annotations.SerializedName

data class Response(

	@field:SerializedName("response_code")
	val responseCode: Int? = null,

	@field:SerializedName("data")
	val data: Any? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
