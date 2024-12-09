package com.project.diacheck.data.remote.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.project.diacheck.data.Result
import com.project.diacheck.data.local.entity.HistoryEntity
import com.project.diacheck.data.remote.request.CreateHistoryRequest
import com.project.diacheck.data.remote.response.AddHistoryResponse
import com.project.diacheck.data.remote.response.DetailHistoriesResponse
import com.project.diacheck.data.remote.response.ListFormItem
import com.project.diacheck.data.remote.response.SubmitFormItem
import com.project.diacheck.data.remote.retrofit.ApiService
import retrofit2.HttpException
import retrofit2.Response

class FormRepository private constructor(
    private val apiService: ApiService
) {

    suspend fun getFormById(userId: Int): List<ListFormItem> {
        val response = apiService.getFormById(userId)
        return response.listHistory?.filterNotNull() ?: emptyList()
    }

    suspend fun createHistory(request: CreateHistoryRequest): AddHistoryResponse {
        return apiService.createHistory(request)
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