package com.project.diacheck.data.remote.response

data class DetailNewsResponse(
    val message: String,
    val data: News
)

data class News(
    val id_article: Int,
    val id_users: Int,
    val thumbnail: String,
    val title: String,
    val body: String,
    val created_at: String,
    val updated_at: String
)
