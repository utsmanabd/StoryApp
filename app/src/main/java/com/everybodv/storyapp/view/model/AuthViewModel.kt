package com.everybodv.storyapp.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.everybodv.storyapp.data.remote.response.LoginResult
import com.everybodv.storyapp.data.remote.response.RegisterResponse
import com.everybodv.storyapp.data.repository.AuthRepository
import com.everybodv.storyapp.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val regMsg: LiveData<Event<String>>
        get() = authRepository.regMsg
    val logMsg: LiveData<Event<String>>
        get() = authRepository.logMsg

    val loginUser: LiveData<LoginResult> = authRepository.loginUser
    val registerUser: LiveData<RegisterResponse> = authRepository.registerUser
    val isEnabled: LiveData<Boolean> = authRepository.isEnabled
    val isLoading: LiveData<Boolean> = authRepository.isLoading

    fun login(email: String, password: String) =
        authRepository.login(email, password)

    fun register(username: String, email: String, password: String) =
        authRepository.register(username, email, password)
}