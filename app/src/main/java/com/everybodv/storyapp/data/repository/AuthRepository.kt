package com.everybodv.storyapp.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.everybodv.storyapp.data.remote.response.LoginResponse
import com.everybodv.storyapp.data.remote.response.LoginResult
import com.everybodv.storyapp.data.remote.response.RegisterResponse
import com.everybodv.storyapp.data.remote.retrofit.ApiConfig
import com.everybodv.storyapp.util.showToast
import com.everybodv.storyapp.view.model.AuthViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {
    private val login = MutableLiveData<LoginResponse>()

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
        msg: String): LiveData<RegisterResponse> {
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
            })
        return _registerUser
    }

    fun login(
        email: String,
        password: String,
        context: Context,
        msg: String = "Email or password is incorrect"
    ): LiveData<LoginResponse> {
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
        return login
    }

    companion object {
        private const val TAG = "AuthRepository"
    }
}