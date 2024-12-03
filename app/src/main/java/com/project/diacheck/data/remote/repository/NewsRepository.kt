package com.project.diacheck.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.project.diacheck.data.Result
import com.project.diacheck.data.local.entity.NewsEntity
import com.project.diacheck.data.local.room.NewsDao
import com.project.diacheck.data.remote.retrofit.ApiService

class NewsRepository private constructor(
    private val apiService: ApiService,
    private val newsDao: NewsDao
) {
    fun getNews(): LiveData<Result<List<NewsEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getNews()
            val listNews = response.listNews
            val newsList = listNews!!.map { newsItem ->
                NewsEntity(
                    newsItem!!.id,
                    newsItem.thumbnail,
                    newsItem.title,
                    newsItem.body
                )
            }
            newsDao.insertNews(newsList)
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun searchNews(query: String, isNews: Boolean): LiveData<Result<List<NewsEntity>>> {
        val result = MediatorLiveData<Result<List<NewsEntity>>>()
        result.value = Result.Loading

        val searchResults =
            if (query.isEmpty()) {
                if (isNews) newsDao.getNews() else newsDao.getNews()
            } else {
                if (isNews) newsDao.searchNews("%$query%") else newsDao.searchNews("%$query%")
            }

        result.addSource(searchResults) { news ->
            result.value =
                if (news.isNotEmpty()) Result.Success(news) else Result.Error("No news found")
        }

        return result
    }

    @Suppress("KotlinDeprecation")
    fun getDetailNews(newsId: String): LiveData<Result<NewsEntity>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailNews(newsId)
            val newsDetail = response.news
            newsDetail?.let { news ->
                val newsEntity = NewsEntity(
                    news.id,
                    news.thumbnail,
                    news.title,
                    news.body
                )
                newsDao.insertNews(listOf(newsEntity))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
        val localData = newsDao.getNewsById(newsId).map { newsEntity ->
            Result.Success(newsEntity) as Result<NewsEntity>
        }
        emitSource(localData)
    }

    companion object {
        @Volatile
        private var instance: NewsRepository? = null
        fun getInstance(
            apiService: ApiService,
            newsDao: NewsDao
        ): NewsRepository =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(apiService, newsDao)
            }.also { instance = it }
    }
}