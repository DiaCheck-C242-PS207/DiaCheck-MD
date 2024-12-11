package com.project.diacheck.data.remote.repository

import androidx.lifecycle.LiveData
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

    fun searchNews(query: String): LiveData<Result<News>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.searchNews(query)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
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