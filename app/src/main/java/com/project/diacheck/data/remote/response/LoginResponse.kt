package com.project.diacheck.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("loginResult")
    val loginResult: LoginResult? = null,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class LoginResult(

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("avatar")
    val avatar: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("id_users")
    val id_users: Int,

    @field:SerializedName("token")
    val token: String
)

