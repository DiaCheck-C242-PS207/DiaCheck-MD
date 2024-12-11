package com.project.diacheck.data.remote.repository

import com.project.diacheck.data.remote.request.CreateHistoryRequest
import com.project.diacheck.data.remote.response.AddHistoryResponse
import com.project.diacheck.data.remote.response.ListFormItem
import com.project.diacheck.data.remote.retrofit.ApiService

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

    suspend fun deleteHistory(id: Int?) {
        apiService.deleteHistory(id)
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