package com.nametag.nametagduckittest

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nametag.nametagduckittest.utils.EncryptionRepository
import com.nametag.nametagduckittest.utils.NametagDuckItTestNewPostRepository
import com.nametag.nametagduckittest.utils.NewPostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View model for the new post screen.
 * @param newPostRepository The repository for the new post screen.
 * @param encryptionRepository The repository for the encryption.
 */
@HiltViewModel
class NametagDuckItTestNewPostViewModel @Inject constructor(private val newPostRepository: NametagDuckItTestNewPostRepository,
                                                            private val encryptionRepository: EncryptionRepository) : ViewModel() {

    private val _newPostUIState = MutableStateFlow(NewPostUiState(headlineError = false,
        imageError = false,
        loading = false,
        headlineText = "",
        imageLink = "",
        successState = UploadSuccessState.Ready))
    val newPostUIState = _newPostUIState.asStateFlow()

    /**
     * Updates the headline text field and checks if it is valid.
     * @param newHeadlineText The new headline text to set
     */
    fun updateHeadlineText(newHeadlineText: String) {
        _newPostUIState.update { currentState ->
            currentState.copy(headlineError = newHeadlineText.isBlank(), headlineText = newHeadlineText)
        }
    }

    /**
     * Updates the image link text field and checks if it is valid.
     * @param newImageUrl The new image URL to set
     */
    fun updateImageUri(newImageUrl: String) {
        _newPostUIState.update { currentState ->
            currentState.copy(imageError = newImageUrl.isBlank() || !Patterns.WEB_URL.matcher(newImageUrl).matches(), imageLink = newImageUrl)
        }
    }

    /**
     * Updates the image error state depending on if coil can find the image
     * @param coilError The error state to set from coil
     */
    fun imageNotFound(coilError: Boolean) {
        _newPostUIState.update { currentState ->
            currentState.copy(imageError = coilError)
        }
    }

    fun updateReadyState() {
        _newPostUIState.update { currentState ->
            currentState.copy(successState = UploadSuccessState.Ready)
        }
    }

    /**
     * Submits the post to the server, and sets the upload success state accordingly.
     */
    fun submitPost() {
        val newPostRequest = NewPostRequest(headline = _newPostUIState.value.headlineText, image = _newPostUIState.value.imageLink)
        viewModelScope.launch {
            val decryptedToken = encryptionRepository.decryptData()
            if (decryptedToken != null) {
                val response = newPostRepository.createPost(token = decryptedToken, newPostRequest)
                if (response.isSuccessful) {
                    _newPostUIState.update { currentState ->
                        currentState.copy(successState = UploadSuccessState.Success)
                    }
                } else {
                    _newPostUIState.update { currentState ->
                        currentState.copy(successState = UploadSuccessState.Error)
                    }
                }
            }
        }
    }
}

data class NewPostUiState(val headlineError: Boolean,
                          val imageError: Boolean,
                          val loading: Boolean,
                          val headlineText: String,
                          val imageLink: String,
                          val successState: UploadSuccessState)

sealed class UploadSuccessState {
    object Success : UploadSuccessState()
    object Error : UploadSuccessState()
    object Ready : UploadSuccessState()
}