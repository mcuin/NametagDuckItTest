package com.nametag.nametagduckittest

import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
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
class NametagDuckItTestSignInOrUpScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Before
    fun setUp() {
        composeTestRule.activity.apply {
            setContent {
                NametagDuckItSignInOrUpScreen(
                    modifier = Modifier,
                    navHostController = rememberNavController(),
                    nametagDuckItTestSignInOrUpViewModel = hiltViewModel()
                )
            }
        }
    }

    @Test
    fun testSignInOrUpScreenEmptyLogin() {
        composeTestRule.onNodeWithTag("signInOrUpFAB").performClick()
        composeTestRule.onNodeWithTag("signInOrUpSnackbar", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun testSignInOrUpScreenInvalidEmailError() {
        composeTestRule.onNodeWithTag("emailTextField").performTextInput("invalidEmail")
        composeTestRule.onNodeWithTag("emailErrorText", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun testSignInOrUpScreenInvalidPasswordError() {
        composeTestRule.onNodeWithTag("passwordTextField").performTextInput("1234567")
        composeTestRule.onNodeWithTag("passwordErrorText", useUnmergedTree = true).assertIsDisplayed()
    }
}