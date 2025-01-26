package com.nametag.nametagduckittest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.nametag.nametagduckittest.utils.Post
import kotlinx.coroutines.launch

/**
 * Main composable for the posts list screen.
 * @param modifier The modifier to apply to the composable.
 * @param navController The navigation controller to navigate to other screens.
 * @param nametagDuckItTestPostsListScreenViewModel The view model to pass data and actions between the composable and the repository.
 */
@Composable
fun NametagDuckItTestPostsListScreen(modifier: Modifier, navController: NavHostController, nametagDuckItTestPostsListScreenViewModel: NametagDuckItTestPostsListScreenViewModel = hiltViewModel()) {

    //Snackbar host state for showing snackbar messages
    val snackbarHostState = remember { SnackbarHostState() }
    //Collects the ui state from the view model as a state
    val postsStates by nametagDuckItTestPostsListScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val postState = postsStates) {
        is DuckItPostsUIState.Error -> {}
        is DuckItPostsUIState.Loading -> CircularProgressIndicator(modifier = modifier.fillMaxSize())
        is DuckItPostsUIState.Success -> {
            Scaffold(topBar = { DuckItTopToolbar(modifier = modifier, titleResourceId = R.string.posts_screen_title, navController = navController, isLoggedIn = postState.isLoggedIn) },
                floatingActionButton = { if (postState.isLoggedIn) DuckItNewPostFAB(modifier = modifier, navController = navController) },
                snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
                Column(modifier = modifier.fillMaxSize().padding(paddingValues)) {
                    LazyColumn {
                        items(postState.postsList) { post ->
                            DuckItPostCard(modifier = modifier, post = post, isLoggedIn = postState.isLoggedIn, snackbarHostState, nametagDuckItTestPostsListScreenViewModel)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable for the post card that includes headline, image, and vote content.
 * @param modifier The modifier to apply to the composable.
 * @param post The data classed Post to pull data from.
 */
@Composable
fun DuckItPostCard(modifier: Modifier, post: Post, isLoggedIn: Boolean, snackbarHostState: SnackbarHostState, nametagDuckItTestPostsListScreenViewModel: NametagDuckItTestPostsListScreenViewModel) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Card(modifier = modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = modifier.padding(8.dp)) {
            Text(text = post.headline)
            AsyncImage(model = post.image, contentDescription = stringResource(id = R.string.post_image_description))
            Row {
                IconButton(modifier = modifier, onClick = {
                    if (isLoggedIn) {
                        nametagDuckItTestPostsListScreenViewModel.upVotePost(post.id)
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.login_required_upvote))
                        }
                    }
                }) {
                    Icon(painter = painterResource(id = R.drawable.ic_upvote), contentDescription = stringResource(id = R.string.upvote_description))
                }
                Text(modifier = modifier.padding(8.dp).align(Alignment.CenterVertically), text = post.upvotes.toString())
                IconButton(modifier = modifier, onClick = {
                    if (isLoggedIn) {
                        nametagDuckItTestPostsListScreenViewModel.downVotePost(post.id)
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.login_required_downvote))
                        }
                    }
                }) {
                    Icon(painter = painterResource(id = R.drawable.ic_downvote), contentDescription = stringResource(id = R.string.downvote_description))
                }
            }
        }
    }
}

/**
 * Composable for the submit post fab
 * @param modifier The modifier to apply to the composable.
 * @param navController The navigation controller to navigate to other screens.
 */
@Composable
fun DuckItNewPostFAB(modifier: Modifier, navController: NavHostController) {
    FloatingActionButton(modifier = modifier, onClick = {
        navController.navigate(Screens.NewPost.route)
    }) {
        Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.new_post_fab_description))
    }
}