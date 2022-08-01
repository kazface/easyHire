package com.example.fasthire

import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import junit.framework.TestCase
import org.hamcrest.Matcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith




@RunWith(AndroidJUnit4ClassRunner::class)
class ApplicantHomePageFragmentTest {
    private lateinit var scenario: FragmentScenario<ApplicantHomePageFragment>

    @Before
    fun setup(){
        var bundle = Bundle()
        var user = User()
        bundle.putSerializable("User", user)
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_FastHire, fragmentArgs = bundle)
        scenario.moveToState(Lifecycle.State.STARTED)
    }
    @Test
    fun isJobDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.recentJobCardRecycle))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}