package com.project.diacheck.data.remote.response

import com.google.gson.annotations.SerializedName

data class UploadFormResponse (

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)