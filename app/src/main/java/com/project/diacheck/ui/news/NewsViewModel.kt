package com.project.diacheck.ui.news

import androidx.lifecycle.ViewModel
import com.project.diacheck.data.remote.repository.NewsRepository

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    fun findNews() = repository.getNews()
    fun searchNews(query: String) = repository.searchNews(query, isNews = true)
    fun getNewsById(newsId: String) = repository.getDetailNews(newsId)

}