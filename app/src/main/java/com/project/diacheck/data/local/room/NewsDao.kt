package com.project.diacheck.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.project.diacheck.data.local.entity.NewsEntity

@Dao
interface NewsDao {
    @Query("SELECT * FROM articles")
    fun getNews(): LiveData<List<NewsEntity>>

    @Query("SELECT * FROM articles WHERE title LIKE :query")
    fun searchNews(query: String): LiveData<List<NewsEntity>>

    @Query("DELETE FROM articles")
    suspend fun deleteNews()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNews(news: kotlin.collections.List<com.project.diacheck.data.local.entity.NewsEntity>)

    @Update
    suspend fun updateNews(news: NewsEntity)

    @Query("SELECT * FROM articles WHERE id_article = :newsId")
    fun getNewsById(newsId: String): LiveData<NewsEntity>

}