package com.everybodv.storyapp.view.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.data.remote.response.StoriesResponse
import com.everybodv.storyapp.data.remote.response.StoryUploadResponse
import com.everybodv.storyapp.data.remote.retrofit.ApiConfig
import com.everybodv.storyapp.util.showToast
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoriesViewModel : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _storyResponse = MutableLiveData<StoryUploadResponse>()
    val storyResponse : LiveData<StoryUploadResponse> = _storyResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _isEnabled = MutableLiveData<Boolean>()
    val isEnabled : LiveData<Boolean> = _isEnabled

    val isFavorite = MutableLiveData(false)

    fun getStories(token: String, context: Context) {
        _isLoading.value = true
        ApiConfig.getApiService().getStories(token, 20)
            .enqueue(object : Callback<StoriesResponse>{
                override fun onResponse(
                    call: Call<StoriesResponse>,
                    response: Response<StoriesResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _stories.postValue(response.body()?.listStory)
                    }
                    else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                    showToast(context, "Failed to load data. Message: ${t.message}")
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })
    }

    fun uploadStory(token: String, image: MultipartBody.Part, desc: RequestBody, context: Context) {
        _isLoading.value = true
        _isEnabled.value = false
        ApiConfig.getApiService().uploadStory(token, image, desc)
            .enqueue(object : Callback<StoryUploadResponse>{
                override fun onResponse(
                    call: Call<StoryUploadResponse>,
                    response: Response<StoryUploadResponse>
                ) {
                    _isLoading.value = false
                    _isEnabled.value = true
                    if (response.isSuccessful) {
                        _storyResponse.postValue(response.body())
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<StoryUploadResponse>, t: Throwable) {
                    showToast(context, "Failed to load data. Message: ${t.message}")
                    Log.e(TAG, "onFailure: ${t.message}")
                }

            })
    }

    companion object {
        private const val TAG = "StoriesViewModel"
    }
}