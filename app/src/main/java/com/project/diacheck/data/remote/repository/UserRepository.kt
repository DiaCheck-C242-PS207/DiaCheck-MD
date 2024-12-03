package com.project.diacheck.data.remote.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.project.diacheck.data.Result
import com.project.diacheck.data.preference.UserModel
import com.project.diacheck.data.preference.UserPreference
import com.project.diacheck.data.remote.response.LoginResponse
import com.project.diacheck.data.remote.response.SignupResponse
import com.project.diacheck.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    fun signup(name: String, email: String, password: String): LiveData<Result<SignupResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.register(name, email, password)
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
        try {
            val response = apiService.login(email, password)
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