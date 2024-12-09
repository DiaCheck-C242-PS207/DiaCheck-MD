package com.project.diacheck.data.remote.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.project.diacheck.data.Result
import com.project.diacheck.data.preference.UserModel
import com.project.diacheck.data.preference.UserPreference
import com.project.diacheck.data.remote.request.LoginRequest
import com.project.diacheck.data.remote.request.RegisterRequest
import com.project.diacheck.data.remote.response.LoginResponse
import com.project.diacheck.data.remote.response.SignupResponse
import com.project.diacheck.data.remote.response.UploadProfileResponse
import com.project.diacheck.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    fun signup(name: String, email: String, password: String): LiveData<Result<SignupResponse>> = liveData {
        emit(Result.Loading)

        val registerRequest = RegisterRequest(name, email, password)

        try {
            val response = apiService.register(registerRequest)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.e("postRegister", "HTTP Exception: ${e.message}")
            try {
                val errorResponse = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parsedError = gson.fromJson(errorResponse, SignupResponse::class.java)
                emit(Result.Success(parsedError))
            } catch (parseException: Exception) {
                Log.e("postRegister", "Error parsing response: ${parseException.message}")
                emit(Result.Error("Error parsing HTTP exception response"))
            }
        } catch (e: Exception) {
            Log.e("postRegister", "General Exception: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)

        val loginRequest = LoginRequest(email, password)

        try {
            val response = apiService.login(loginRequest)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.e("postLogin", "HTTP Exception: ${e.message}")
            try {
                val errorResponse = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parsedError = gson.fromJson(errorResponse, LoginResponse::class.java)
                emit(Result.Success(parsedError))
            } catch (parseException: Exception) {
                Log.e("postLogin", "Error parsing response: ${parseException.message}")
                emit(Result.Error("Error parsing HTTP exception response"))
            }
        } catch (e: Exception) {
            Log.e("postLogin", "General Exception: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
    }


    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun updateUser(
        userId: Unit,
        name: String,
        imageFile: File
    ): LiveData<Result<UploadProfileResponse>> = liveData {
        emit(Result.Loading)

        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val avatar = MultipartBody.Part.createFormData("avatar", imageFile.name, requestImageFile)

        val nameBody = name.toRequestBody("text/plain".toMediaType())

        try {
            val response = apiService.updateUser(userId, nameBody, avatar)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.e("updateUser", "HTTP Exception: ${e.message}")
            emit(Result.Error("HTTP Error: ${e.message()}"))
        } catch (e: Exception) {
            Log.e("updateUser", "General Exception: ${e.message}")
            emit(Result.Error("Error: ${e.message}"))
        }
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}