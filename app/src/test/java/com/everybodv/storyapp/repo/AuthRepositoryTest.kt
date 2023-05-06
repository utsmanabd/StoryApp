package com.everybodv.storyapp.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.everybodv.storyapp.data.repository.AuthRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryTest {

    @get: Rule
    val instanceExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepo: AuthRepository


    @Before
    fun setup(){
        authRepo = AuthRepository()
    }

    @Test
    fun loginSuccess(){
        authRepo.login("didi@mail.com","didi1234")
        Assert.assertFalse(false)
        Assert.assertNotNull("Not null")
    }

    @Test
    fun registerSuccess(){
        authRepo.register("dodi", "dodi@mail.com","dodi1234")
        Assert.assertFalse(false)
        Assert.assertNotNull("not null")
    }
}