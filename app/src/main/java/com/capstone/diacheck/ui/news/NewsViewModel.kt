package com.capstone.diacheck.ui.news

import androidx.lifecycle.ViewModel
import com.capstone.diacheck.data.remote.repository.NewsRepository

class NewsViewModel (private val repository: NewsRepository) : ViewModel() {

    fun findNews() = repository.getNews()
    fun searchNews(query: String) = repository.searchNews(query, isNews = true)
}