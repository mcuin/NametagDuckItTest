package com.nametag.nametagduckittest

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NametagDuckItTestPostsListScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test fun nametagDuckItPostsListScreenToSignUp() {
        composeTestRule.onNodeWithTag("loginButton").performClick()
        composeTestRule.onNodeWithTag("signInOrUpScreen").assertExists()
    }

    @Test fun nameTagDuckItPostsListScreenLoggedOutAdd() {
        composeTestRule.onNodeWithTag("newPostFAB").assertDoesNotExist()
    }
}