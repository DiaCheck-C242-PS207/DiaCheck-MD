package com.project.diacheck.data.remote.retrofit

import com.project.diacheck.data.remote.request.CreateHistoryRequest
import com.project.diacheck.data.remote.request.LoginRequest
import com.project.diacheck.data.remote.request.RegisterRequest
import com.project.diacheck.data.remote.response.AddHistoryResponse
import com.project.diacheck.data.remote.response.FormResponse
import com.project.diacheck.data.remote.response.LoginResponse
import com.project.diacheck.data.remote.response.NewsResponse
import com.project.diacheck.data.remote.response.PredictionResponse
import com.project.diacheck.data.remote.response.SignupResponse
import com.project.diacheck.data.remote.response.SubmitFormItem
import com.project.diacheck.data.remote.response.UploadProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("users/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): SignupResponse

    @POST("users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("articles/getAll")
    suspend fun getNews(): NewsResponse

    @GET("articles/getArticles/{id}")
    suspend fun getDetailNews(@Path("id") newsId: Int): NewsResponse

    @GET("articles/{title}")
    suspend fun searchNews(@Path("title") title: String): NewsResponse

    @Multipart
    @PUT("users/update/{id}")
    fun updateUser(
        @Path("id") userId: Unit,
        @Part("name") name: RequestBody,
        @Part avatar: MultipartBody.Part
    ): UploadProfileResponse

    @GET("histories/getHistory/{id}")
    suspend fun getFormById(
        @Path("id") userId: Int
    ): FormResponse

    @POST("histories/create")
    suspend fun createHistory(
        @Body requestBody: CreateHistoryRequest
    ): AddHistoryResponse

    @POST("/predictions")
    suspend fun predict(@Body formItem: SubmitFormItem): PredictionResponse
}