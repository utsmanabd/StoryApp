package com.everybodv.storyapp.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.data.repository.MapsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val mapsRepository: MapsRepository) : ViewModel() {

    fun getStoryWithLoc(token: String): LiveData<List<ListStoryItem>> =
        mapsRepository.getStoriesWithLoc(token)

    fun getStories(): LiveData<List<ListStoryItem>> = mapsRepository.getStories()
}