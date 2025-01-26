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
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NametagDuckItTestSignInOrUpViewModel @Inject constructor(private val repository: NametagDuckItTestSignInOrUpRepository, private val encryptionRepository: EncryptionRepository) : ViewModel() {

    var emailText by mutableStateOf("")
        private set
    var emailError by mutableStateOf(false)
        private set

    var passwordText by mutableStateOf("")
        private set
    var passwordError by mutableStateOf(false)
        private set

    private val _loginSuccess = MutableSharedFlow<Int>()
    val loginSuccess = _loginSuccess.asSharedFlow()
    private val _signUpSuccess = MutableSharedFlow<Int>()
    val signUpSuccess = _signUpSuccess.asSharedFlow()

    fun updateEmailText(newText: String) {
        emailError = newText.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(newText).matches()
        emailText = newText
    }

    fun updatePasswordText(newText: String) {
        passwordError = newText.isBlank() || newText.length < 8
        passwordText = newText
    }

    fun signIn() {
        val signInRequest = SignInRequest(emailText, passwordText)
        viewModelScope.launch {
            repository.signIn(signInRequest).map { response ->
                println("Token to encrypt: " + response.body()!!.token)
                when (response.code()) {
                    200 -> {
                        encryptionRepository.encryptData(response.body()!!.token)
                        _signUpSuccess.emit(response.code())
                    }
                    404 -> {
                        signUp()
                        _signUpSuccess.emit(response.code())
                    }
                    else -> _signUpSuccess.emit(response.code())
                }
            }.shareIn(viewModelScope, SharingStarted.Eagerly)
        }
    }

    private suspend fun signUp() {
        val signUpRequest = SignUpRequest(emailText, passwordText)
        repository.signUp(signUpRequest).map { response ->
            when (response.code()) {
                200 -> {
                    encryptionRepository.encryptData(response.body()!!.token)
                    _signUpSuccess.emit(response.code())
                }
                else -> _signUpSuccess.emit(response.code())
            }
        }.shareIn(viewModelScope, SharingStarted.Eagerly)
    }
}