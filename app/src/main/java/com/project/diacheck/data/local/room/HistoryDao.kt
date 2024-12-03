package com.project.diacheck.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.project.diacheck.data.local.entity.HistoryEntity

@Dao
interface HistoryDao {
    @Query("SELECT * FROM histories")
    fun getHistory(): LiveData<List<HistoryEntity>>

    @Query("SELECT * FROM histories WHERE result LIKE :query")
    fun searchHistory(query: String): LiveData<List<HistoryEntity>>

    @Query("DELETE FROM histories")
    suspend fun deleteHistory()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHistory(history: kotlin.collections.List<com.project.diacheck.data.local.entity.HistoryEntity>)

    @Update
    suspend fun updateHistory(history: HistoryEntity)

    @Query("SELECT * FROM histories WHERE id_history = :id")
    fun getNewsById(id: String): LiveData<HistoryEntity>

}