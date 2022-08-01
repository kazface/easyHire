package com.example.fasthire

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.hamcrest.Matcher
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class SignInActivityTest{
    private lateinit var username: String
    private lateinit var password: String
    @get:Rule var activityScenarioRule: ActivityScenarioRule<SignInActivity> = ActivityScenarioRule(SignInActivity::class.java)
    @Before
    fun init(){
        username = "bake@gmail.com"
        password = "A12345@"
    }
    @Test
    fun testLogin(){
        onView(withId(R.id.emailInput)).perform(typeText(username), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.passwordInput)).perform(typeText(password), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.signInButton)).perform(click())
        onView(isRoot()).perform(waitFor(5000))
        onView(withId(R.id.helloTextLayout)).check(matches(isDisplayed()))
    }
    fun waitFor(delay: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }

}