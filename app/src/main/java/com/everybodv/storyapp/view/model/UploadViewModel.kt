package com.everybodv.storyapp.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.data.remote.response.StoryUploadResponse
import com.everybodv.storyapp.data.repository.UploadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val uploadRepository: UploadRepository
) : ViewModel() {

    val stories: LiveData<List<ListStoryItem>> = uploadRepository.stories
    val storyResponse: LiveData<StoryUploadResponse> = uploadRepository.storyResponse
    val isLoading: LiveData<Boolean> = uploadRepository.isLoading
    val isEnabled: LiveData<Boolean> = uploadRepository.isEnabled


    fun uploadStory(
        token: String,
        image: MultipartBody.Part,
        desc: RequestBody,
        lat: Double? = null,
        lon: Double? = null
    ) = uploadRepository.uploadStory(token, image, desc, lat, lon)
}