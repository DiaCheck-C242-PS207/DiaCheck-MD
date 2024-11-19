package com.capstone.diacheck.data.remote.retrofit

import com.capstone.diacheck.data.remote.response.FormResponse
import com.capstone.diacheck.data.remote.response.LoginResponse
import com.capstone.diacheck.data.remote.response.NewsResponse
import com.capstone.diacheck.data.remote.response.SignupResponse
import com.capstone.diacheck.data.remote.response.UploadFormResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SignupResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

// Get Artikel or News

    @GET("news")
    suspend fun getNews(): NewsResponse

// Get History Diacheck

    @GET("dia/1")
    suspend fun getAllForm(): FormResponse

// Upload Form

    @Multipart
    @POST("dia")
    suspend fun uploadForm(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): UploadFormResponse
}