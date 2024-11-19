package com.capstone.diacheck.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
//import kotlinx.parcelize.Parcelize

data class FormResponse (

//    @field:SerializedName("listStory")
//    val listStory: List<ListFormItem?>? = null,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

// Example
//@Parcelize
//data class ListFormItem(
//
//    @field:SerializedName("photoUrl")
//    val photoUrl: String? = null,
//
//    @field:SerializedName("createdAt")
//    val createdAt: String? = null,
//
//    @field:SerializedName("name")
//    val name: String? = null,
//
//    @field:SerializedName("description")
//    val description: String? = null,
//
//    @field:SerializedName("lon")
//    val lon: Float? = null,
//
//    @field:SerializedName("id")
//    val id: String? = null,
//
//    @field:SerializedName("lat")
//    val lat: Float? = null
//): Parcelable