package com.everybodv.storyapp.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.everybodv.storyapp.data.AuthPreferences
import kotlinx.coroutines.launch

class Token (private val authPreferences: AuthPreferences): ViewModel() {

    fun setToken(token: String) {
        viewModelScope.launch {
            authPreferences.setToken(token)
        }
    }
    fun delToken() {
        viewModelScope.launch {
            authPreferences.delToken()
        }
    }

    fun getToken() : LiveData<String> = authPreferences.getToken().asLiveData()
}