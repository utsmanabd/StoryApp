package com.everybodv.storyapp.util

import android.content.Context
import com.everybodv.storyapp.data.AuthPreferences
import com.everybodv.storyapp.data.local.StoriesDatabase
import com.everybodv.storyapp.data.remote.retrofit.ApiConfig
import com.everybodv.storyapp.data.repository.MainRepository
import com.everybodv.storyapp.data.repository.MapsRepository
import com.everybodv.storyapp.data.repository.UploadRepository

object Injection {

    fun storiesRepo(authPreferences: AuthPreferences, context: Context): MainRepository {
        val storiesDatabase = StoriesDatabase.getInstance(context)
        val apiService = ApiConfig.getApiService()
        return MainRepository(storiesDatabase, apiService, authPreferences)
    }

    fun mapsRepo(): MapsRepository {
        return MapsRepository()
    }

    fun uploadRepo(): UploadRepository {
        return UploadRepository()
    }
}