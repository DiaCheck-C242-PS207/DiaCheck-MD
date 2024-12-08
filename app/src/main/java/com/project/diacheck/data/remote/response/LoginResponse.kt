package com.project.diacheck.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("token")
    val token: String,

    @field:SerializedName("users")
    val loginResult: LoginResult
)

data class LoginResult(

    @field:SerializedName("id")
    val id_users: Int,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("avatar")
    val avatar: String? = null
)


