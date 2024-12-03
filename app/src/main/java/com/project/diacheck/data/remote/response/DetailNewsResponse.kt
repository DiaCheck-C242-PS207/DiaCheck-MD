package com.project.diacheck.data.remote.response

data class DetailNewsResponse(
    val error: Boolean? = null,
    val message: String? = null,
    val news: News? = null
)

data class News(
    val id: Int,
    val thumbnail: String,
    val title: String,
    val body: String,
    val date: String
)
