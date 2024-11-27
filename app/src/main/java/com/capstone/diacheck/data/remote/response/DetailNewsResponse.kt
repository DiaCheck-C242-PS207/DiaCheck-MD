package com.capstone.diacheck.data.remote.response

data class DetailNewsResponse(
    val error: Boolean? = null,
    val message: String? = null,
    val news: News? = null
)

data class News(
    val id: Int? = null,
    val thumbnail: String? = null,
    val title: String? = null,
    val body: String? = null,
    val date: String? = null
)
