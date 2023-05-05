package com.everybodv.storyapp.data.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.data.remote.response.StoriesResponse
import com.everybodv.storyapp.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsRepository {

    private val _mapStories = MutableLiveData<List<ListStoryItem>>()

    fun getStoriesWithLoc(token: String): LiveData<List<ListStoryItem>> {
        ApiConfig.getApiService().getStoriesWithLoc(token, 1)
            .enqueue(object : Callback<StoriesResponse> {
                override fun onResponse(
                    call: Call<StoriesResponse>,
                    response: Response<StoriesResponse>
                ) {
                    if (response.isSuccessful) {
                        _mapStories.postValue(response.body()?.listStory)
                    }
                }

                override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                    Log.e(ContentValues.TAG,"onFailure: ${t.message}")
                }

            })
        return _mapStories
    }

    fun getStories(): LiveData<List<ListStoryItem>> {
        return _mapStories
    }
}