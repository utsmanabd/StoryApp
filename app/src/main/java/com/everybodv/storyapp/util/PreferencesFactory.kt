package com.everybodv.storyapp.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.everybodv.storyapp.data.AuthPreferences
import com.everybodv.storyapp.data.repository.AuthRepository
import com.everybodv.storyapp.view.model.*

class PreferencesFactory(
    private val authPreferences: AuthPreferences,
    private val authRepository: AuthRepository,
    private val context: Context
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository) as T
        }
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(Injection.storiesRepo(authPreferences, context)) as T
        }
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(Injection.mapsRepo()) as T
        }
        if (modelClass.isAssignableFrom(PagingModel::class.java)) {
            return PagingModel(Injection.storiesRepo(authPreferences, context)) as T
        }
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(Injection.uploadRepo()) as T
        }
        throw IllegalArgumentException("error ${modelClass.name}")
    }
}