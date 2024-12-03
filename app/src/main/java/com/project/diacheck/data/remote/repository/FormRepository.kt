package com.project.diacheck.data.remote.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.project.diacheck.data.Result
import com.project.diacheck.data.local.entity.HistoryEntity
import com.project.diacheck.data.remote.response.DetailHistoriesResponse
import com.project.diacheck.data.remote.response.ListFormItem
import com.project.diacheck.data.remote.response.SubmitFormItem
import com.project.diacheck.data.remote.retrofit.ApiService
import retrofit2.HttpException
import retrofit2.Response

class FormRepository private constructor(
    private val apiService: ApiService
) {

    fun getFormById(userId: String): LiveData<Result<List<HistoryEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getFormById(userId)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.e("getFormsByUserId", "HTTP Exception: ${e.message}")
            emit(Result.Error(e.message()))
        } catch (e: Exception) {
            Log.e("getFormsByUserId", "General Exception: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun submitForm(formItem: SubmitFormItem): Response<ListFormItem> {
        return apiService.submitHistory(formItem)
    }

    suspend fun getHistoryById(id: Int): Result<DetailHistoriesResponse> {
        return try {
            val response = apiService.getHistoryById(id)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Error: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message.toString())
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