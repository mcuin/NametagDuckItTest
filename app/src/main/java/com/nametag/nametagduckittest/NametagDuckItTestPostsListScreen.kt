package com.nametag.nametagduckittest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NametagDuckItTestPostsListScreen(modifier: Modifier, nametagDuckItTestPostsListScreenViewModel: NametagDuckItTestPostsListScreenViewModel = hiltViewModel()) {

    Scaffold { paddingValues ->
        Column(modifier = modifier.fillMaxSize().padding(paddingValues)) {

            val postsStates by nametagDuckItTestPostsListScreenViewModel.states.collectAsStateWithLifecycle()

            when (val postState = postsStates) {
                is DuckItPostsUIState.Error -> {}
                is DuckItPostsUIState.Loading -> CircularProgressIndicator()
                is DuckItPostsUIState.Success -> {
                    println(postState.posts)
                }
            }
        }
    }
}