package com.project.diacheck.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.diacheck.data.local.entity.HistoryEntity
import com.project.diacheck.data.local.entity.NewsEntity


@Database(entities = [HistoryEntity::class, NewsEntity::class], version = 4, exportSchema = false)
abstract class DiacheckDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun newsDao(): NewsDao

    companion object {
        @Volatile
        private var instance: DiacheckDatabase? = null

        fun getInstance(context: Context): DiacheckDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    DiacheckDatabase::class.java, "diacheck.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}