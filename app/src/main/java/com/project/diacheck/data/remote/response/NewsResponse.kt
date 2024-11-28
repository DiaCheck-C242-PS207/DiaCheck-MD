package com.project.diacheck.data.remote.response

import com.google.gson.annotations.SerializedName

data class NewsResponse(

    @field:SerializedName("listStory")
    val listNews: List<ListNewsItem?>? = null,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)


data class ListNewsItem(

    @field:SerializedName("id_article")
    val id: Int,

    @field:SerializedName("thumbnail")
    val thumbnail: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("body")
    val body: String,

    @field:SerializedName("update_at")
    val date: String
)