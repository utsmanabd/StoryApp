package com.everybodv.storyapp.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.everybodv.storyapp.data.AuthPreferences
import com.everybodv.storyapp.view.model.AuthViewModel

class PreferencesFactory(private val authPreferences: AuthPreferences): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authPreferences) as T
        }
        throw IllegalArgumentException("Note ${modelClass.name}")
    }

}