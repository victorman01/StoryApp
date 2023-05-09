package com.dicoding.storyapp.tools

import android.content.Context
import com.dicoding.storyapp.api.ApiConfig

object Injection {
    fun provideRepository(context: Context): Repository {
        val preferences = Preferences.getInstance(context)
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(preferences, apiService)
    }
}