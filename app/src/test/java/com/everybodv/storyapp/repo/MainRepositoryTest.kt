package com.everybodv.storyapp.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.everybodv.storyapp.data.Dummy
import com.everybodv.storyapp.data.MainDispatcherRule
import com.everybodv.storyapp.data.PagingSource
import com.everybodv.storyapp.data.getOrAwait
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.data.repository.MainRepository
import com.everybodv.storyapp.view.adapter.MainAdapter
import com.everybodv.storyapp.view.model.MainViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainRepositoryTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: MainRepository

    @Before
    fun setup(){

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getStoryReturnSuccess() = runTest {
        val dummy = Dummy.dummyStory()
        val data = PagingSource.snapshot(dummy)
        val viewModel = MainViewModel(repository)


        val expectedData = MutableLiveData<PagingData<ListStoryItem>>()
        expectedData.value = data

        Mockito.`when`(repository.getStories()).thenReturn(expectedData)
        viewModel.getStories()
        val actualData = viewModel.getStories().getOrAwait()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = MainAdapter.noopListUpdateCallback,
            workerDispatcher = Main
        )
        differ.submitData(actualData)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummy,differ.snapshot())
        Assert.assertEquals(dummy.size,differ.snapshot().size)
        Assert.assertEquals(dummy[0].id, differ.snapshot()[0]?.id)

    }
}