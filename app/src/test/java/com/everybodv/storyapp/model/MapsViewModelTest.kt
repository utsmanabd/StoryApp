package com.everybodv.storyapp.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.everybodv.storyapp.data.Dummy
import com.everybodv.storyapp.data.getOrAwait
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.data.repository.MapsRepository
import com.everybodv.storyapp.view.model.MapsViewModel
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

@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest{

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository : MapsRepository
    private lateinit var viewModel : MapsViewModel
    private val dummy = Dummy.dummyStory()

    @Before
    fun setup(){
        viewModel = MapsViewModel(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getStoryWithMapsReturnSuccess()= runTest {

        val expectedData = MutableLiveData<List<ListStoryItem>>()
        expectedData.postValue( dummy)
        `when`(repository.getStoriesWithLoc("token")).thenReturn(expectedData)
        val actualData = viewModel.getStoryWithLoc("token").getOrAwait()
        Mockito.verify(repository).getStoriesWithLoc("token")
        assertNotNull(actualData)
        assertEquals(actualData,expectedData.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getMarkerMapsReturnSuccess()= runTest {

        val expectedData = MutableLiveData<List<ListStoryItem>>()
        expectedData.postValue( dummy)
        `when`(repository.getStories()).thenReturn(expectedData)
        val actualData = viewModel.getStories().getOrAwait()
        Mockito.verify(repository).getStories()
        assertNotNull(actualData)
        assertEquals(actualData,expectedData.value)
    }
}