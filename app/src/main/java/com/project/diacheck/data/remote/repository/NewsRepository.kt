package com.project.diacheck.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.project.diacheck.data.Result
import com.project.diacheck.data.local.entity.NewsEntity
import com.project.diacheck.data.local.room.NewsDao
import com.project.diacheck.data.remote.response.News
import com.project.diacheck.data.remote.retrofit.ApiService

class NewsRepository private constructor(
    private val apiService: ApiService,
    private val newsDao: NewsDao
) {
    private val _showToastMessage = MutableLiveData<String>()
    val showToastMessage: LiveData<String> get() = _showToastMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _detailNews = MutableLiveData<Result<News>>()

    fun getNews(): LiveData<Result<List<NewsEntity>>> = liveData {
//        emit(Result.Loading)
//        try {
//            val response = apiService.getNews()
//            val listNews = response.listNews
//            val newsList = listNews?.map { news ->
//                news?.let {
//                    NewsEntity(
//                        news.id,
//                        news.thumbnail,
//                        news.title,
//                        news.body,
//                        news.date
//                    )
//                }
//            }
//            newsDao.deleteNews()
//            newsDao.insertNews(newsList)
//        } catch (e: Exception) {
//            emit(Result.Error(e.message.toString()))
//        }
//
//        val localData: LiveData<Result<List<NewsEntity>>> =
//            newsDao.getNews().map { Result.Success(it) }
//        emitSource(localData)
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
//        emit(Result.Loading)
//        try {
//            val response = apiService.getDetailNews(newsId)
//            val newsDetail = response.news
//            newsDetail?.let { news ->
//                val newsEntity = NewsEntity(
//                    news.id,
//                    news.thumbnail,
//                    news.title,
//                    news.body,
//                    news.date
//                )
//                newsDao.insertNews(listOf(newsEntity))
//            }
//        } catch (e: Exception) {
//            emit(Result.Error(e.message.toString()))
//        }
//        val localData = newsDao.getNewsById(newsId).map { newsEntity ->
//            Result.Success(newsEntity) as Result<NewsEntity>
//        }
//        emitSource(localData)
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