package com.project.diacheck.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "histories")
data class HistoryEntity(
    @field:ColumnInfo(name = "id_history")
    @PrimaryKey
    val id: Int,

    @field:ColumnInfo(name = "id_users")
    val id_users: Int,

    @field:ColumnInfo(name = "history")
    val history: String,

    @field:ColumnInfo(name = "gender")
    val gender: Int,

    @field:ColumnInfo(name = "hypertension")
    val hypertension: Int,

    @field:ColumnInfo(name = "heart_disease")
    val heart_disease: Int,

    @field:ColumnInfo(name = "age")
    val age: Int,

    @field:ColumnInfo(name = "bmi")
    val bmi: Float,

    @field:ColumnInfo(name = "hbA1c")
    val hbA1c: Float,

    @field:ColumnInfo(name = "blood_glucose")
    val blood_glucose: Int,

    @field:ColumnInfo(name = "result")
    val result: Int,

    @field:ColumnInfo(name = "created_at")
    val created_at: String,

    @field:ColumnInfo(name = "updated_at")
    val updated_at: String

)