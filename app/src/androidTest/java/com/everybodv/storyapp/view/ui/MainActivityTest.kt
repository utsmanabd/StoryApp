package com.everybodv.storyapp.view.ui

import android.support.test.espresso.IdlingRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.everybodv.storyapp.util.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.everybodv.storyapp.R

class MainActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun uploadStorySuccess() {
        onView(withId(R.id.fab_upload)).check(matches(isDisplayed()))
        onView(withId(R.id.fab_upload)).perform(click())

        onView(withId(R.id.ib_camerax)).check(matches(isDisplayed()))
        onView(withId(R.id.ib_camerax)).perform(click())
        Thread.sleep(2000)

        onView(withId(R.id.ib_take_photo)).check(matches(isDisplayed()))
        onView(withId(R.id.ib_take_photo)).perform(click())
        Thread.sleep(4000)
        onView(withId(R.id.et_desc)).perform(typeText("Description Test"), closeSoftKeyboard())
        onView(withId(R.id.ib_upload)).perform(click())
        Thread.sleep(4000)
    }
}