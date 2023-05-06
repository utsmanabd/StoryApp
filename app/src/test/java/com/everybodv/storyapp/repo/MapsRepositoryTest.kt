package com.everybodv.storyapp.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.everybodv.storyapp.data.Dummy
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.data.repository.MapsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsRepositoryTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository : MapsRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getStoryReturnSuccess()= runTest {
        val observer = Observer<List<ListStoryItem>>{}
        val dummy = Dummy.dummyStory()

        try {
            val expectedData = MutableLiveData<List<ListStoryItem>>()
            expectedData.value = dummy

            `when`(repository.getStoriesWithLoc("token")).thenReturn(expectedData)

            val actualData = repository.getStoriesWithLoc("token").observeForever(observer)
            Mockito.verify(repository).getStoriesWithLoc("token")
            assertNotNull(actualData)
        } finally {
            repository.getStoriesWithLoc("token").removeObserver(observer)
        }
    }
}