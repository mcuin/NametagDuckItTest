package com.nametag.nametagduckittest

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nametag.nametagduckittest.utils.NametagDuckItTestSignInOrUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NametagDuckItTestSignInOrUpViewModel @Inject constructor(private val repository: NametagDuckItTestSignInOrUpRepository) : ViewModel() {

    var emailText by mutableStateOf("")
        private set
    var emailError by mutableStateOf(false)
        private set

    var passwordText by mutableStateOf("")
        private set
    var passwordError by mutableStateOf(false)
        private set

    fun updateEmailText(newText: String) {
        emailError = newText.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(newText).matches()
        emailText = newText
    }

    fun updatePasswordText(newText: String) {
        passwordError = newText.isBlank() || newText.length < 8
        passwordText = newText
    }
}