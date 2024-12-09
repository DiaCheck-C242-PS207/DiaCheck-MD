package com.project.diacheck.data.remote.response


data class NewsResponse(
    val message: String,
    val data:  List<ListNewsItem>,
    val listNews: ListNewsItem
)

data class ListNewsItem(
    val id: Int,
    val title: String,
    val content: String,
    val author: String,
    val date: String
)