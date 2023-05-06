package com.everybodv.storyapp.model

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: MainRepository
    private lateinit var viewModel : MainViewModel

    @Before
    fun setup(){
        viewModel = MainViewModel(repository)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getStoryReturnSuccess() = runTest {
        val dummy = Dummy.dummyStory()
        val data = PagingSource.snapshot(dummy)

        val expectedData = MutableLiveData<PagingData<ListStoryItem>>()
        expectedData.value = data

        `when`(viewModel.getStories()).thenReturn(expectedData)
        viewModel.getStories()
        val actualData = viewModel.getStories().getOrAwait()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = MainAdapter.noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualData)

        assertNotNull(differ.snapshot())
        assertEquals(dummy,differ.snapshot())
        assertEquals(dummy.size,differ.snapshot().size)
        assertEquals(dummy[0], differ.snapshot()[0])

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getStoryWithNoDataReturnSuccess() = runTest {
        val dummy = Dummy.dummyStoryNull
        val data = PagingSource.snapshot(dummy)
        val expectedData = MutableLiveData<PagingData<ListStoryItem>>()
        expectedData.value = data

        `when`(viewModel.getStories()).thenReturn(expectedData)
        viewModel.getStories()
        val actualData = viewModel.getStories().getOrAwait()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = MainAdapter.noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualData)

        assertEquals(0, differ.snapshot().size)

    }
}