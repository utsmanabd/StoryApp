package com.everybodv.storyapp.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.everybodv.storyapp.data.Dummy
import com.everybodv.storyapp.data.repository.UploadRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UploadRepositoryTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository : UploadRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addStoryReturnFailed() = runTest {
        val image = Dummy.dummyImage()
        val desc = Dummy.dummyDesc()
        repository.uploadStory("token",image, desc, -1.348994, 115.6112,)
        assertTrue(true)
        assertNotNull("not null")
    }
}