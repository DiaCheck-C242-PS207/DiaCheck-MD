package com.capstone.diacheck.data.remote.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.capstone.diacheck.data.remote.response.FormResponse
import com.capstone.diacheck.data.remote.response.UploadFormResponse
import com.capstone.diacheck.data.remote.retrofit.ApiService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class FormRepository private constructor(
    private val apiService: ApiService
) {

    fun getAllForm(): LiveData<Result<FormResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAllForm()
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.e("getAllStories", "HTTP Exception: ${e.message}")
            try {
                val errorResponse = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parsedError = gson.fromJson(errorResponse, FormResponse::class.java)
                emit(Result.Success(parsedError))
            } catch (e: Exception) {
                Log.e("getAllStories", "Error parsing error response: ${e.message}")
                emit(Result.Error("Error: ${e.message}"))
            }
        } catch (e: Exception) {
            Log.e("getAllStories", "General Exception: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun getListStory(): List<ListFormItem?>? {
        return apiService.getAllForm().listStory
    }

    fun uploadStory(
        imageFile: File,
        description: String
    ): LiveData<Result<UploadFormResponse>> = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val response = apiService.uploadForm(multipartBody, requestBody)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.e("uploadStory", "HTTP Exception: ${e.message}")
            try {
                val errorResponse = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parsedError = gson.fromJson(errorResponse, UploadFormResponse::class.java)
                emit(Result.Success(parsedError))
            } catch (e: Exception) {
                Log.e("uploadStory", "Error parsing error response: ${e.message}")
                emit(Result.Error("Error: ${e.message}"))
            }
        } catch (e: Exception) {
            Log.e("uploadStory", "General Exception: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
    }


    companion object {
        @Volatile
        private var instance: FormRepository? = null
        fun getInstance(
            apiService: ApiService
        ): FormRepository =
            instance ?: synchronized(this) {
                instance ?: FormRepository(apiService)
            }.also { instance = it }
    }
}