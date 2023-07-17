package com.example.mystory.api.di

import android.content.Context
import com.example.mystory.api.ApiConfig
import com.example.mystory.api.ApiService
import com.example.mystory.ui.stories.repository.StoryRepository

object Injection {

    private val apiService: ApiService = ApiConfig.createApiService()


    fun provideRepository(context: Context): StoryRepository {
        return StoryRepository(apiService, context)
    }
}
