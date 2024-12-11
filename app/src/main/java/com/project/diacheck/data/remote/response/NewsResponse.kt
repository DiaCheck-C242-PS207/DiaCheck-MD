package com.project.diacheck.data.remote.response

data class NewsResponse(
    val message: String,
    val data:  List<ListNewsItem>
)

data class ListNewsItem(
    val id_article: Int,
    val id_users: Int,
    val thumbnail: String,
    val title: String,
    val body: String,
    val created_at: String,
    val updated_at: String
)
