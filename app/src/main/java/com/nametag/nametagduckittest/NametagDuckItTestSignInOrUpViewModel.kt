package com.nametag.nametagduckittest

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nametag.nametagduckittest.utils.EncryptionRepository
import com.nametag.nametagduckittest.utils.NametagDuckItTestSignInOrUpRepository
import com.nametag.nametagduckittest.utils.SignInRequest
import com.nametag.nametagduckittest.utils.SignUpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NametagDuckItTestSignInOrUpViewModel @Inject constructor(private val repository: NametagDuckItTestSignInOrUpRepository, private val encryptionRepository: EncryptionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInOrSignUpUiState(emailError = false, passwordError = false, loading = false, loginCode = -1, signUpCode = -1, emailText = "", passwordText = ""))
    val uiState = _uiState.asStateFlow()

    fun updateEmailText(newText: String) {
        _uiState.update { currentState ->
            currentState.copy(emailError = newText.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(newText).matches(),
                emailText = newText)
        }
    }

    fun updatePasswordText(newText: String) {
        _uiState.update { currentState ->
            currentState.copy(passwordError = newText.isBlank() || newText.length < 8,
                passwordText = newText)
        }
    }

    fun signIn() {
        _uiState.update { currentState ->
            currentState.copy(loading = true)
        }
        val signInRequest = SignInRequest(_uiState.value.emailText, _uiState.value.passwordText)
        viewModelScope.launch {
            val signInResponse = repository.signIn(signInRequest)
            when (signInResponse.code()) {
                200 -> {
                    encryptionRepository.encryptData(signInResponse.body()!!.token)
                    _uiState.update { currentState ->
                        currentState.copy(loginCode = signInResponse.code(), loading = false)
                    }
                }
                404 -> {
                    signUp()
                    _uiState.update { currentState ->
                        currentState.copy(loginCode = signInResponse.code())
                    }
                }
                else -> {
                    _uiState.update { currentState ->
                        currentState.copy(loginCode = signInResponse.code(), loading = false)
                    }
                }
            }
        }
    }

    private suspend fun signUp() {
        val signUpRequest = SignUpRequest(_uiState.value.emailText, _uiState.value.passwordText)
        val signUpResponse = repository.signUp(signUpRequest)
        when (signUpResponse.code()) {
            200 -> {
                encryptionRepository.encryptData(signUpResponse.body()!!.token)
                _uiState.update { currentState ->
                    currentState.copy(signUpCode = signUpResponse.code(), loading = false)
                }
            }
            else -> {
                _uiState.update {
                    currentState -> currentState.copy(signUpCode = signUpResponse.code(), loading = false)
                }
            }
        }
    }
}

data class SignInOrSignUpUiState (
    val emailError: Boolean,
    val passwordError: Boolean,
    val loading: Boolean,
    val loginCode: Int,
    val signUpCode: Int,
    val emailText: String,
    val passwordText: String
)