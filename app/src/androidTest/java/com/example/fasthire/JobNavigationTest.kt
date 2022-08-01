package com.example.fasthire

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class JobNavigationTest(){

    private lateinit var scenario: FragmentScenario<ApplicantHomePageFragment>

    @Test
    fun isJobDisplayed() {
        onView(withId(R.id.recentJobCardRecycle)).check(matches(isDisplayed()))
    }

    @Test
    fun testJobClick(){
        onView(withId(R.id.recentJobCardRecycle)).perform(actionOnItemAtPosition<JobAdapter.ViewHolder>(0, click()))
        onView(withId(R.id.jobPositionNameText)).check(matches(withText("Washer")))
    }

    @Test
    fun testJobBackNavigration(){
        onView(withId(R.id.recentJobCardRecycle)).perform(actionOnItemAtPosition<JobAdapter.ViewHolder>(0, click()))
        onView(withId(R.id.jobPositionNameText)).check(matches(withText("Washer")))
        pressBack()
        onView(withId(R.id.recentJobCardRecycle)).check(matches(isDisplayed()))
    }






}