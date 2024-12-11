package com.project.diacheck.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.diacheck.data.local.entity.NewsEntity

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNews(news: List<NewsEntity>)

    @Query("SELECT * FROM articles WHERE title LIKE :query")
    fun searchNews(query: String): LiveData<List<NewsEntity>>

    @Query("SELECT * FROM articles")
    fun getNews(): LiveData<List<NewsEntity>>

    @Query("SELECT * FROM articles WHERE id_article = :newsId")
    fun getNewsById(newsId: String): LiveData<NewsEntity>
}