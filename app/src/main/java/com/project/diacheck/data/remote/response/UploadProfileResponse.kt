package com.project.diacheck.data.remote.response

data class UploadProfileResponse(
    val message: String,
    val data: User
)

data class User(
    val avatar: String?,
    val name: String,
    val email: String,
    val password: String
)