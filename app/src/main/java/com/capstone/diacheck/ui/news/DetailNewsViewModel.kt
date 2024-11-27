package com.capstone.diacheck.ui.news

import androidx.lifecycle.ViewModel
import com.capstone.diacheck.data.remote.repository.NewsRepository

class DetailNewsViewModel (private val repository: NewsRepository) : ViewModel() {

    fun getDetailNews(query: String) = repository.getDetailNews(query)
}