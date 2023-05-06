package com.everybodv.storyapp.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val loading = MutableLiveData<Boolean>()

    fun getStories(): LiveData<PagingData<ListStoryItem>> =
        mainRepository.getStories()
}