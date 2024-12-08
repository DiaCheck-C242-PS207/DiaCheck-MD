package com.project.diacheck.data.remote.retrofit

import com.project.diacheck.data.local.entity.HistoryEntity
import com.project.diacheck.data.remote.request.CreateHistoryRequest
import com.project.diacheck.data.remote.request.LoginRequest
import com.project.diacheck.data.remote.request.RegisterRequest
import com.project.diacheck.data.remote.response.AddHistoryResponse
import com.project.diacheck.data.remote.response.DetailHistoriesResponse
import com.project.diacheck.data.remote.response.DetailNewsResponse
import com.project.diacheck.data.remote.response.ListFormItem
import com.project.diacheck.data.remote.response.LoginResponse
import com.project.diacheck.data.remote.response.NewsResponse
import com.project.diacheck.data.remote.response.PredictionData
import com.project.diacheck.data.remote.response.PredictionResponse
import com.project.diacheck.data.remote.response.SignupResponse
import com.project.diacheck.data.remote.response.SubmitFormItem
import com.project.diacheck.data.remote.response.UploadProfileResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("users/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): SignupResponse

    @POST("users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

// Get Artikel or News

    @GET("articles")
    suspend fun getNews(): NewsResponse

    @GET("articles")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("active") active: Int = 0
    ): NewsResponse

    @GET("articles/{id}")
    suspend fun getDetailNews(@Path("id_articles") newsId: String): DetailNewsResponse

    @Multipart
    @POST("users")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): UploadProfileResponse

    @POST("history/submit")
    suspend fun submitHistory(@Body formItem: SubmitFormItem): Response<ListFormItem>

    @GET("histories/getHistory/{id}")
    suspend fun getFormById(
        @Path("id") userId: String
    ): List<HistoryEntity>

    @POST("histories/create")
    suspend fun createHistory(
        @Body requestBody: CreateHistoryRequest
    ): AddHistoryResponse

    @GET("histories/getHistory/{id}")
    suspend fun getHistoryById(@Path("id") id: Int): Response<DetailHistoriesResponse>

    @POST("/predictions")
    suspend fun predict(@Body formItem: SubmitFormItem): PredictionResponse
}