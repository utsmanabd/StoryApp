package com.everybodv.storyapp.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.everybodv.storyapp.data.AuthPreferences
import com.everybodv.storyapp.data.Dummy
import com.everybodv.storyapp.data.MainDispatcherRule
import com.everybodv.storyapp.data.getOrAwait
import com.everybodv.storyapp.data.remote.response.LoginResponse
import com.everybodv.storyapp.data.remote.response.RegisterResponse
import com.everybodv.storyapp.data.repository.AuthRepository
import com.everybodv.storyapp.view.model.AuthViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: AuthRepository
    private lateinit var preference: AuthPreferences
    private lateinit var viewModel: AuthViewModel
    private val dummyLogin = Dummy.loginResult()
    private val dummyRegister = Dummy.register()
    private val dummyEmail = "didi@mail.com"
    private val dummyPassword = "didi1234"

    @Before
    fun setup(){
        preference = AuthPreferences(Mockito.mock(android.content.Context::class.java))
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown(){
    }

    @Test
    fun loginSuccess() = runTest {
        val expectedData = MutableLiveData<LoginResponse>()
        expectedData.value = dummyLogin
        Mockito.`when`(repository.login(dummyEmail, dummyPassword)).thenReturn(expectedData)

        val actualData = viewModel.login(dummyEmail,dummyPassword).getOrAwait()
        Mockito.verify(repository).login(dummyEmail,dummyPassword)
        assertNotNull(actualData)
        assertEquals(expectedData.value, actualData)
    }
    @Test
    fun registerSuccess() = runTest {
        val expectedData = MutableLiveData<RegisterResponse>()
        expectedData.value = dummyRegister
        Mockito.`when`(repository.register("didi",dummyEmail, dummyPassword)).thenReturn(expectedData)

        val actualData = viewModel.register("didi",dummyEmail,dummyPassword).getOrAwait()
        Mockito.verify(repository).register("didi",dummyEmail,dummyPassword)
        assertNotNull(actualData)
        assertEquals(expectedData.value, actualData)
    }
}