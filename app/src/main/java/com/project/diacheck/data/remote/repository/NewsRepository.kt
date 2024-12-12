package com.project.diacheck.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import com.project.diacheck.data.Result
import com.project.diacheck.data.remote.response.ListNewsItem
import com.project.diacheck.data.remote.response.News
import com.project.diacheck.data.remote.retrofit.ApiService

class NewsRepository private constructor(
    private val apiService: ApiService
) {
    fun getNews(): LiveData<Result<List<ListNewsItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getNews()
            val listNews = response.data
            emit(Result.Success(listNews))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun searchNews(query: String): LiveData<Result<List<ListNewsItem>>> {
        val result = MediatorLiveData<Result<List<ListNewsItem>>>()
        result.value = Result.Loading

        if (query.isEmpty()) {
            result.value = Result.Error("Query cannot be empty")
        } else {
            try {
                val response = apiService.searchNews(query)
                result.value = Result.Success(response.data)
            } catch (e: Exception) {
                result.value = Result.Error(e.message ?: "Unknown error")
            }

        }

        return result
    }

    fun getDetailNews(newsId: Int): LiveData<Result<News>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailNews(newsId)
            val newsDetail = response.data
            if (newsDetail != null) {
                emit(Result.Success(newsDetail))
            } else {
                emit(Result.Error("News not found"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: NewsRepository? = null
        fun getInstance(
            apiService: ApiService
        ): NewsRepository =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(apiService)
            }.also { instance = it }
    }
}