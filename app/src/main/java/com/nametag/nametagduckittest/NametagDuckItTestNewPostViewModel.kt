package com.nametag.nametagduckittest

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nametag.nametagduckittest.utils.EncryptionRepository
import com.nametag.nametagduckittest.utils.NametagDuckItTestNewPostRepository
import com.nametag.nametagduckittest.utils.NewPostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    //Holds the state of the headline text field, and error state
    var headlineText = mutableStateOf("")
        private set
    var headLineError = mutableStateOf(false)
        private set

    //Holds the state of the image link text field, and error state
    var imageData = mutableStateOf("")
        private set
    var imageError = mutableStateOf(false)
        private set

    //Shared flow for upload success
    private val _uploadSuccess = MutableSharedFlow<Boolean>()
    val uploadSuccess = _uploadSuccess.asSharedFlow()

    /**
     * Updates the headline text field and checks if it is valid.
     * @param newHeadlineText The new headline text to set
     */
    fun updateHeadlineText(newHeadlineText: String) {
        headLineError.value = newHeadlineText.isBlank()
        headlineText.value = newHeadlineText
    }

    /**
     * Updates the image link text field and checks if it is valid.
     * @param newImageUrl The new image URL to set
     */
    fun updateImageUri(newImageUrl: String) {
        imageError.value = newImageUrl.isBlank() || !Patterns.WEB_URL.matcher(newImageUrl).matches()
        imageData.value = newImageUrl
    }

    /**
     * Updates the image error state depending on if coil can find the image
     * @param coilError The error state to set from coil
     */
    fun imageNotFound(coilError: Boolean) {
        imageError.value = coilError
    }

    /**
     * Submits the post to the server, and sets the upload success state accordingly.
     */
    fun submitPost() {
        val newPostRequest = NewPostRequest(headline = headlineText.value, image = imageData.value)
        viewModelScope.launch {
            val decryptedToken = encryptionRepository.decryptData()
            if (decryptedToken != null) {
                val response = newPostRepository.createPost(token = decryptedToken, newPostRequest)
                if (response.isSuccessful) {
                    _uploadSuccess.emit(true)
                } else {
                    _uploadSuccess.emit(false)
                }
            }
        }
    }
}