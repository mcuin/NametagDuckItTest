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

@HiltViewModel
class NametagDuckItTestSignInOrUpViewModel @Inject constructor(private val repository: NametagDuckItTestSignInOrUpRepository, private val encryptionRepository: EncryptionRepository) : ViewModel() {

    private val _signInOrUpUiState = MutableStateFlow(SignInOrSignUpUiState(emailError = false, passwordError = false, loading = false, loginCode = LoginState.Ready, signUpCode =SignUpState.Ready, emailText = "", passwordText = ""))
    val signInOrUpUiState = _signInOrUpUiState.asStateFlow()

    fun updateEmailText(newText: String) {
        _signInOrUpUiState.update { currentState ->
            currentState.copy(emailError = newText.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(newText).matches(),
                emailText = newText)
        }
    }

    fun updatePasswordText(newText: String) {
        _signInOrUpUiState.update { currentState ->
            currentState.copy(passwordError = newText.isBlank() || newText.length < 8,
                passwordText = newText)
        }
    }

    fun resetLoginState() {
        _signInOrUpUiState.update { currentState ->
            currentState.copy(loginCode = LoginState.Ready)
        }
    }

    fun resetSignUpState() {
        _signInOrUpUiState.update { currentState ->
            currentState.copy(signUpCode = SignUpState.Ready)
        }
    }

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
                        currentState.copy(loginCode = LoginState.Success, loading = false)
                    }
                }
                403 -> {
                    _signInOrUpUiState.update { currentState ->
                        currentState.copy(loginCode = LoginState.Error(signInResponse.code()), loading = false)
                    }
                }
                404 -> {
                    _signInOrUpUiState.update { currentState ->
                        currentState.copy(loginCode = LoginState.Error(signInResponse.code()))
                    }
                    signUp()
                }
                else -> {
                    _signInOrUpUiState.update { currentState ->
                        currentState.copy(loginCode = LoginState.Error(signInResponse.code()), loading = false)
                    }
                }
            }
        }
    }

    private suspend fun signUp() {
        val signUpRequest = SignUpRequest(_signInOrUpUiState.value.emailText, _signInOrUpUiState.value.passwordText)
        val signUpResponse = repository.signUp(signUpRequest)
        when (signUpResponse.code()) {
            200 -> {
                encryptionRepository.encryptData(signUpResponse.body()!!.token)
                _signInOrUpUiState.update { currentState ->
                    currentState.copy(signUpCode = SignUpState.Success, loading = false)
                }
            }
            else -> {
                _signInOrUpUiState.update {
                    currentState -> currentState.copy(signUpCode = SignUpState.Error(code = signUpResponse.code()), loading = false)
                }
            }
        }
    }
}

data class SignInOrSignUpUiState (
    val emailError: Boolean,
    val passwordError: Boolean,
    val loading: Boolean,
    val loginCode: LoginState,
    val signUpCode: SignUpState,
    val emailText: String,
    val passwordText: String
)

sealed class LoginState {
    object Success : LoginState()
    data class Error(val code: Int) : LoginState()
    object Ready : LoginState()
}

sealed class SignUpState {
    object Success : SignUpState()
    data class Error(val code: Int) : SignUpState()
    object Ready : SignUpState()
}