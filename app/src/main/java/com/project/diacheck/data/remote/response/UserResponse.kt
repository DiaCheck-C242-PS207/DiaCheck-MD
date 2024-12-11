package com.project.diacheck.data.remote.response

data class UserResponse(
    val message: String,
    val data: List<UserData>?
)

data class UserData(
    val id_users: Int,
    val name: String,
    val email: String,
    val avatar: String
)