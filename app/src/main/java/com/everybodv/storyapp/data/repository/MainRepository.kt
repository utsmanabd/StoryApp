package com.everybodv.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.everybodv.storyapp.data.AuthPreferences
import com.everybodv.storyapp.data.StoryRemoteMediator
import com.everybodv.storyapp.data.local.StoriesDatabase
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.data.remote.retrofit.ApiService
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val storiesDatabase: StoriesDatabase,
    private val apiService: ApiService,
    val authPreferences: AuthPreferences
) {

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(storiesDatabase, apiService, authPreferences),
            pagingSourceFactory = {
                storiesDatabase.storiesDao().getStories()
            }
        ).liveData
    }
}