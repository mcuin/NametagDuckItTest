package com.nametag.nametagduckittest

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nametag.nametagduckittest.utils.DataStoreRepository
import com.nametag.nametagduckittest.utils.EncryptionRepository
import com.nametag.nametagduckittest.utils.NametagDuckItPostsListRepository
import com.nametag.nametagduckittest.utils.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View model for the posts list screen to handle data and actions.
 * @param duckItPostsListRepository The repository to get get and send data to the api.
 */
@HiltViewModel
class NametagDuckItTestPostsListScreenViewModel @Inject constructor(private val duckItPostsListRepository: NametagDuckItPostsListRepository,
                                                                    private val dataStoreRepository: DataStoreRepository,
                                                                    private val encryptionRepository: EncryptionRepository
) : ViewModel() {

    //Mutable state flow to represent the state of the posts list screen
    private val _uiState = MutableStateFlow<DuckItPostsUIState>(DuckItPostsUIState.Loading)
    val uiState = _uiState.asStateFlow()

    //Flow for checking if the user is logged in or not by checking for token in data store
    private val isLoggedIn = dataStoreRepository.isLoggedInFlow().stateIn(viewModelScope, SharingStarted.Eagerly, false)

    //Flow for getting the posts from the api and updating the state of the posts list screen
    private val posts = duckItPostsListRepository.getPosts().map { response ->
        when (response.code()) {
            200 -> {
                response.body()!!.Posts.toMutableList()
            }
            else -> mutableListOf()
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, mutableListOf())

    private val states = posts.combine(isLoggedIn) { posts, isLoggedIn ->
        when {
            posts.isEmpty() -> _uiState.value = DuckItPostsUIState.Error
            else -> _uiState.value = DuckItPostsUIState.Success(posts, isLoggedIn)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DuckItPostsUIState.Loading)

    /**
     * Function to upvote a post.
     * @param postId The id of the post to upvote.
     */
    fun upVotePost(postId: String) {
        if (_uiState.value is DuckItPostsUIState.Success) {
            viewModelScope.launch {
                val decryptedToken = encryptionRepository.decryptData()
                if (decryptedToken != null) {
                    val response = duckItPostsListRepository.upVotePost(decryptedToken, postId)
                    when (response.code()) {
                        200 -> {
                            _uiState.update { currentState ->
                                (currentState as DuckItPostsUIState.Success).copy(
                                    currentState.postsList.toMutableList().also {
                                        it.indexOfFirst { post -> post.id == postId }.also { index ->
                                            it[index] = it[index].copy(upvotes = response.body()!!.upvotes)
                                        }
                                    })
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    /**
     * Function to downvote a post.
     * @param postId The id of the post to downvote.
     */
    fun downVotePost(postId: String) {
        if (_uiState.value is DuckItPostsUIState.Success) {
            viewModelScope.launch {
                val decryptedToken = encryptionRepository.decryptData()
                if (decryptedToken != null) {
                    val response = duckItPostsListRepository.downVotePost(decryptedToken, postId)
                    when (response.code()) {
                        200 -> {
                            _uiState.update { currentState ->
                                (currentState as DuckItPostsUIState.Success).copy(
                                    currentState.postsList.toMutableList().also {
                                        it.indexOfFirst { post -> post.id == postId }.also { index ->
                                            it[index] = it[index].copy(upvotes = response.body()!!.upvotes)
                                        }
                                    })
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

/**
 * Sealed interface to represent the state of the posts list screen.
 */
@Immutable
sealed class DuckItPostsUIState {
    data class Success(val postsList: List<Post>, val isLoggedIn: Boolean) : DuckItPostsUIState()
    data object Error : DuckItPostsUIState()
    data object Loading : DuckItPostsUIState()
}