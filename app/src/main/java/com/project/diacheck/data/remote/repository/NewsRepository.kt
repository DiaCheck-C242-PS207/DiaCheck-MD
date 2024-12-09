package com.project.diacheck.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.project.diacheck.data.Result
import com.project.diacheck.data.local.entity.NewsEntity
import com.project.diacheck.data.local.room.NewsDao
import com.project.diacheck.data.remote.response.ListNewsItem
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

    fun searchNews(query: String): LiveData<Result<List<ListNewsItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.searchNews(query)
            val listNews = response.data
            emit(Result.Success(listNews ?: emptyList()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getDetailNews(newsId: Int): LiveData<Result<ListNewsItem>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailNews(newsId)
            val newsDetail = response.listNews
            if (newsDetail != null) {
                emit(Result.Success(newsDetail))
            } else {
                emit(Result.Error("News detail not found"))
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