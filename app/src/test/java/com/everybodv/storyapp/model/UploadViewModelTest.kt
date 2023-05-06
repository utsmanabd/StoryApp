package com.everybodv.storyapp.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.everybodv.storyapp.data.Dummy
import com.everybodv.storyapp.data.MainDispatcherRule
import com.everybodv.storyapp.data.getOrAwait
import com.everybodv.storyapp.data.remote.response.StoryUploadResponse
import com.everybodv.storyapp.data.repository.UploadRepository
import com.everybodv.storyapp.view.model.UploadViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UploadViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Mock
    private lateinit var repository : UploadRepository
    private lateinit var viewModel : UploadViewModel
    private val dummy = Dummy.uploadStory()
    private val token = "token"
    private val image = Dummy.dummyImage()
    private val desc = Dummy.dummyDesc()
    private val lat = -1.348994
    private val lon = 115.6112

    @Before
    fun setup(){
        viewModel = UploadViewModel(repository)
    }
    @Test
    fun addStorySuccess() = runTest {
        val expectedData = MutableLiveData<StoryUploadResponse>()
        expectedData.postValue(dummy)
        `when`(repository.uploadStory(token, image, desc, lat, lon)).thenReturn(expectedData)
        val actualData = viewModel.uploadStory(token, image, desc, lat, lon).getOrAwait()
        Mockito.verify(repository).uploadStory(token, image, desc, lat, lon)
        assertNotNull(actualData)
        assertEquals(actualData, expectedData.value)
    }

}