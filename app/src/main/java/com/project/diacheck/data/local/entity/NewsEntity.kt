package com.project.diacheck.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class NewsEntity (
    @field:ColumnInfo(name = "id_article")
    @PrimaryKey
    val id: Int,

    @field:ColumnInfo(name = "thumbnail")
    val thumbnail: String,

    @field:ColumnInfo(name = "title")
    val title: String,

    @field:ColumnInfo(name = "body")
    val body: String,

    @field:ColumnInfo(name = "update_at")
    val date: String
)