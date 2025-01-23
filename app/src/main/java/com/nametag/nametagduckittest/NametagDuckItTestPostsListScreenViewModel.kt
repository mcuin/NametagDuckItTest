package com.nametag.nametagduckittest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nametag.nametagduckittest.utils.NametagDuckItPostsListRepository
import com.nametag.nametagduckittest.utils.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NametagDuckItTestPostsListScreenViewModel @Inject constructor(private val duckItPostsListRepository: NametagDuckItPostsListRepository) : ViewModel() {

    val states = duckItPostsListRepository.getPosts().map { posts ->
        if (posts.Posts.isEmpty()) {
            DuckItPostsUIState.Error
        } else {
            DuckItPostsUIState.Success(posts.Posts)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DuckItPostsUIState.Loading)

}

sealed interface DuckItPostsUIState {
    data class Success(val posts: List<Post>) : DuckItPostsUIState
    object Error : DuckItPostsUIState
    object Loading : DuckItPostsUIState
}