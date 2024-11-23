package com.capstone.diacheck.di

import android.content.Context
import com.capstone.diacheck.data.remote.repository.UserRepository
import com.capstone.diacheck.data.preference.UserPreference
import com.capstone.diacheck.data.preference.dataStore
import com.capstone.diacheck.data.remote.repository.FormRepository
import com.capstone.diacheck.data.remote.retrofit.ApiConfig

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