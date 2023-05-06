package com.everybodv.storyapp.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PagingModel @Inject constructor(
    mainRepository: MainRepository
) : ViewModel() {

    val getStories: LiveData<PagingData<ListStoryItem>> =
        mainRepository.getStories().cachedIn(viewModelScope)
}