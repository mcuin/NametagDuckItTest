package com.nametag.nametagduckittest

import android.os.Build.VERSION_CODES.Q
import com.nametag.nametagduckittest.utils.EncryptionRepository
import com.nametag.nametagduckittest.utils.NametagDuckItTestSignInOrUpRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@Config(sdk = [Q], application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class NametagDuckItTestSignInOrUpViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Mock
    lateinit var mockNametagDuckItTestSignInOrUpRepository: NametagDuckItTestSignInOrUpRepository
    lateinit var mockEncryptionRepository: EncryptionRepository
    private lateinit var nametagDuckItTestSignInOrUpViewModel: NametagDuckItTestSignInOrUpViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        hiltRule.inject()
        mockEncryptionRepository = Mockito.mock(EncryptionRepository::class.java)
        nametagDuckItTestSignInOrUpViewModel = NametagDuckItTestSignInOrUpViewModel(
            mockNametagDuckItTestSignInOrUpRepository,
            mockEncryptionRepository
        )
    }

    @Test
    fun testEmailStateChange() {
        val signInOrUpUiState: SignInOrSignUpUiState
        nametagDuckItTestSignInOrUpViewModel.updateEmailText("Test")
        signInOrUpUiState = nametagDuckItTestSignInOrUpViewModel.signInOrUpUiState.value
        assert(signInOrUpUiState.emailText == "Test")
    }

    @Test
    fun testEmailErrorStateChange() {
        val signInOrUpUiState: SignInOrSignUpUiState
        nametagDuckItTestSignInOrUpViewModel.updateEmailText("")
        signInOrUpUiState = nametagDuckItTestSignInOrUpViewModel.signInOrUpUiState.value
        assert(signInOrUpUiState.emailError)
    }

    @Test
    fun testPasswordStateChange() {
        val signInOrUpUiState: SignInOrSignUpUiState
        nametagDuckItTestSignInOrUpViewModel.updatePasswordText("Test")
        signInOrUpUiState = nametagDuckItTestSignInOrUpViewModel.signInOrUpUiState.value
        assert(signInOrUpUiState.passwordText == "Test")
    }

    @Test
    fun testPasswordErrorStateChange() {
        val signInOrUpUiState: SignInOrSignUpUiState
        nametagDuckItTestSignInOrUpViewModel.updatePasswordText("")
        signInOrUpUiState = nametagDuckItTestSignInOrUpViewModel.signInOrUpUiState.value
        assert(signInOrUpUiState.passwordError)
    }

    @Test
    fun testResetLoginState() {
        var signInOrUpUiState = nametagDuckItTestSignInOrUpViewModel.signInOrUpUiState.value.copy(
            loginState = LoginState.Error(500)
        )
        assert(signInOrUpUiState.loginState is LoginState.Error)
        nametagDuckItTestSignInOrUpViewModel.resetLoginState()
        signInOrUpUiState = nametagDuckItTestSignInOrUpViewModel.signInOrUpUiState.value
        assert(signInOrUpUiState.loginState is LoginState.Ready)
    }

    @Test
    fun testResetSignUpState() {
        var signInOrUpUiState = nametagDuckItTestSignInOrUpViewModel.signInOrUpUiState.value.copy(signUpState = SignUpState.Error(500))
        assert(signInOrUpUiState.signUpState is SignUpState.Error)
        nametagDuckItTestSignInOrUpViewModel.resetSignUpState()
        signInOrUpUiState = nametagDuckItTestSignInOrUpViewModel.signInOrUpUiState.value
        assert(signInOrUpUiState.signUpState is SignUpState.Ready)
    }
}
