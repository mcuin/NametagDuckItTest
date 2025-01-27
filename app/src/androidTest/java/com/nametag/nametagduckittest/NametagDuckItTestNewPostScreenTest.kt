package com.nametag.nametagduckittest

import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NametagDuckItTestNewPostScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        composeTestRule.activity.apply {
            setContent {
                NametagDuckItTestNewPostScreen(
                    modifier = Modifier,
                    navController = rememberNavController(),
                    nametagDuckItTestNewPostViewModel = hiltViewModel()
                )
            }
        }
    }

    @Test
    fun nametagDuckItTestNewPostScreenEmptyNewPost() {
        composeTestRule.onNodeWithTag("newPostFAB").performClick()
        composeTestRule.onNodeWithTag("newPostSnackbar", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun nametagDuckItTestNewPostScreenHeadlineError() {
        composeTestRule.onNodeWithTag("headlineTextField").performTextInput(" ")
        composeTestRule.onNodeWithTag("headlineError", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun nametagDuckItTestNewPostScreenImageError() {
        composeTestRule.onNodeWithTag("newPostImageTextField").performTextInput(" ")
        composeTestRule.onNodeWithTag("newPostImageError", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("newPostPreviewImageTitle", useUnmergedTree = true).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("newPostPreviewImage", useUnmergedTree = true).assertIsNotDisplayed()
    }
}