package com.everybodv.storyapp.view.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.everybodv.storyapp.data.AuthPreferences
import com.everybodv.storyapp.data.remote.response.LoginResponse
import com.everybodv.storyapp.data.remote.response.LoginResult
import com.everybodv.storyapp.data.remote.response.RegisterResponse
import com.everybodv.storyapp.data.remote.retrofit.ApiConfig
import com.everybodv.storyapp.util.showToast
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(private val authPreferences: AuthPreferences): ViewModel() {

    private val _registerUser = MutableLiveData<RegisterResponse>()
    val registerUser : LiveData<RegisterResponse> = _registerUser

    private val _loginUser = MutableLiveData<LoginResult>()
    val loginUser : LiveData<LoginResult> = _loginUser

    private val _isEnabled = MutableLiveData<Boolean>()
    val isEnabled : LiveData<Boolean> = _isEnabled

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    fun register(
        name: String,
        email: String,
        password: String,
        context: Context,
        msg: String = "The email is already taken") {
        _isEnabled.value = false
        _isLoading.value = true

        ApiConfig.getApiService().register(name, email, password)
            .enqueue(object : Callback<RegisterResponse> {

                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    _isEnabled.value = true
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _registerUser.postValue(response.body())
                    }
                    else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                        showToast(context, msg)
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    showToast(context, "Failed to load data. Message: ${t.message}")
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            }
        )
    }

    fun login(
        email: String,
        password: String,
        context: Context,
        msg: String = "Email or password is incorrect"
    ) {
        _isEnabled.value = false
        _isLoading.value = true

        ApiConfig.getApiService().login(email, password)
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    _isEnabled.value = true
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        response.body().let { login ->
                            login?.loginResult?.let {
                                _loginUser.value = LoginResult(it.name, it.userId, it.token)
                            }
                        }
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                        showToast(context, msg)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    showToast(context, "Failed to load data. Message: ${t.message}")
                    Log.e(TAG, "onFailure: ${t.message}")
                }

            }
        )
    }

    fun getToken() : LiveData<String> =
        authPreferences.getToken().asLiveData()

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

    companion object {
        private const val TAG = "AuthViewModel"
    }

}