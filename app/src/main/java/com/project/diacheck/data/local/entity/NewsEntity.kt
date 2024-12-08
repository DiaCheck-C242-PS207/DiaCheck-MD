package com.project.diacheck.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class NewsEntity(
    @field:ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @field:ColumnInfo(name = "thumbnail")
    val thumbnail: String,

    @field:ColumnInfo(name = "title")
    val title: String,

    @field:ColumnInfo(name = "body")
    val body: String,
)