package com.project.diacheck.di

import android.content.Context
import com.project.diacheck.data.remote.repository.UserRepository
import com.project.diacheck.data.preference.UserPreference
import com.project.diacheck.data.preference.dataStore
import com.project.diacheck.data.remote.repository.FormRepository
import com.project.diacheck.data.remote.retrofit.ApiConfig

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideFormRepository(context: Context): FormRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return FormRepository.getInstance(apiService)
    }
}