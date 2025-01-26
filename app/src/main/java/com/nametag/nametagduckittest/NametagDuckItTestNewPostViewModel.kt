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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NametagDuckItTestNewPostViewModel @Inject constructor(private val newPostRepository: NametagDuckItTestNewPostRepository,
                                                            private val encryptionRepository: EncryptionRepository) : ViewModel() {

    var headlineText = mutableStateOf("")
        private set
    var headLineError = mutableStateOf(false)
        private set

    var imageData = mutableStateOf("")
        private set
    var imageError = mutableStateOf(false)
        private set

    private val _uploadSuccess = MutableSharedFlow<Boolean>()
    val uploadSuccess = _uploadSuccess.asSharedFlow()

    fun updateHeadlineText(newHeadlineText: String) {
        headLineError.value = newHeadlineText.isBlank()
        headlineText.value = newHeadlineText
    }

    fun updateImageUri(newImageUrl: String) {
        imageError.value = newImageUrl.isBlank() || !Patterns.WEB_URL.matcher(newImageUrl).matches()
        imageData.value = newImageUrl
    }

    fun imageNotFound(coilError: Boolean) {
        imageError.value = coilError
    }

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