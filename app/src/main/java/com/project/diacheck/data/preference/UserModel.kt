package com.project.diacheck.data.preference

data class UserModel(
    val name: String,
    val email: String,
    val token: String,
    val isLogin: Boolean = false,
    val id_users: Int,
    val avatar: String? = null
)