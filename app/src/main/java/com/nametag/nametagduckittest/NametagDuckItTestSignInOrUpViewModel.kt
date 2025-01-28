package com.nametag.nametagduckittest

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nametag.nametagduckittest.utils.EncryptionRepository
import com.nametag.nametagduckittest.utils.NametagDuckItTestSignInOrUpRepository
import com.nametag.nametagduckittest.utils.SignInRequest
import com.nametag.nametagduckittest.utils.SignUpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View model for the sign in or up screen
 */
@HiltViewModel
class NametagDuckItTestSignInOrUpViewModel @Inject constructor(private val repository: NametagDuckItTestSignInOrUpRepository, private val encryptionRepository: EncryptionRepository) : ViewModel() {

    //Mutable state flow for the sign in or up screen state
    private val _signInOrUpUiState = MutableStateFlow(SignInOrSignUpUiState(emailError = false, passwordError = false, loading = false, loginState = LoginState.Ready, signUpState =SignUpState.Ready, emailText = "", passwordText = ""))
    val signInOrUpUiState = _signInOrUpUiState.asStateFlow()

    /**
     * Function to update the email text and check if it is valid
     */
    fun updateEmailText(newText: String) {
        _signInOrUpUiState.update { currentState ->
            currentState.copy(emailError = newText.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(newText).matches(),
                emailText = newText)
        }
    }

    /**
     * Function to update the password text and check if it is valid
     */
    fun updatePasswordText(newText: String) {
        _signInOrUpUiState.update { currentState ->
            currentState.copy(passwordError = newText.isBlank() || newText.length < 8,
                passwordText = newText)
        }
    }

    /**
     * Function to reset the login state in case of another error as the state flow would not trigger again for another error
     */
    fun resetLoginState() {
        _signInOrUpUiState.update { currentState ->
            currentState.copy(loginState = LoginState.Ready)
        }
    }

    /**
     * Function to reset the sign up state in case of another error as the state flow would not trigger again for another error
     */
    fun resetSignUpState() {
        _signInOrUpUiState.update { currentState ->
            currentState.copy(signUpState = SignUpState.Ready)
        }
    }

    /**
     * Function to sign in with the current email and password
     */
    fun signIn() {
        _signInOrUpUiState.update { currentState ->
            currentState.copy(loading = true)
        }
        val signInRequest = SignInRequest(_signInOrUpUiState.value.emailText, _signInOrUpUiState.value.passwordText)
        viewModelScope.launch {
            val signInResponse = repository.signIn(signInRequest)
            when (signInResponse.code()) {
                200 -> {
                    encryptionRepository.encryptData(signInResponse.body()!!.token)
                    _signInOrUpUiState.update { currentState ->
                        currentState.copy(loginState = LoginState.Success, loading = false)
                    }
                }
                403 -> {
                    _signInOrUpUiState.update { currentState ->
                        currentState.copy(loginState = LoginState.Error(signInResponse.code()), loading = false)
                    }
                }
                404 -> {
                    _signInOrUpUiState.update { currentState ->
                        currentState.copy(loginState = LoginState.Error(signInResponse.code()))
                    }
                    signUp()
                }
                else -> {
                    _signInOrUpUiState.update { currentState ->
                        currentState.copy(loginState = LoginState.Error(signInResponse.code()), loading = false)
                    }
                }
            }
        }
    }

    /**
     * Function to call sign up, only used at the moment if log in failed
     */
    private suspend fun signUp() {
        val signUpRequest = SignUpRequest(_signInOrUpUiState.value.emailText, _signInOrUpUiState.value.passwordText)
        val signUpResponse = repository.signUp(signUpRequest)
        when (signUpResponse.code()) {
            200 -> {
                encryptionRepository.encryptData(signUpResponse.body()!!.token)
                _signInOrUpUiState.update { currentState ->
                    currentState.copy(signUpState = SignUpState.Success, loading = false)
                }
            }
            else -> {
                _signInOrUpUiState.update {
                    currentState -> currentState.copy(signUpState = SignUpState.Error(code = signUpResponse.code()), loading = false)
                }
            }
        }
    }
}

/**
 * Data class for the sign in or up screen state
 * @param emailError Whether the email is invalid
 * @param passwordError Whether the password is invalid
 * @param loading Whether the sign in or up is loading
 * @param loginState The login state
 * @param signUpState The sign up state
 * @param emailText The email text
 * @param passwordText The password text
 */
data class SignInOrSignUpUiState (
    val emailError: Boolean,
    val passwordError: Boolean,
    val loading: Boolean,
    val loginState: LoginState,
    val signUpState: SignUpState,
    val emailText: String,
    val passwordText: String
)

/**
 * Sealed class for the login state
 */
sealed class LoginState {
    object Success : LoginState()
    data class Error(val code: Int) : LoginState()
    object Ready : LoginState()
}

/**
 * Sealed class for the sign up state
 */
sealed class SignUpState {
    object Success : SignUpState()
    data class Error(val code: Int) : SignUpState()
    object Ready : SignUpState()
}