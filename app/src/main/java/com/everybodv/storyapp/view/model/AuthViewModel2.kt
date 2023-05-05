package com.everybodv.storyapp.view.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.everybodv.storyapp.data.remote.response.LoginResult
import com.everybodv.storyapp.data.remote.response.RegisterResponse
import com.everybodv.storyapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel2 @Inject constructor(
    private val authRepository: AuthRepository) : ViewModel() {

    val loginUser: LiveData<LoginResult> = authRepository.loginUser
    val registerUser: LiveData<RegisterResponse> = authRepository.registerUser
    val isEnabled: LiveData<Boolean> = authRepository.isEnabled
    val isLoading: LiveData<Boolean> = authRepository.isLoading

    fun login(email: String, password: String, context: Context) =
        authRepository.login(email, password, context)

    fun register(username: String, email: String, password: String, context: Context, msg: String) =
        authRepository.register(username, email, password, context, msg)
}