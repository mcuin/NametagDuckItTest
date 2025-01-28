package com.nametag.nametagduckittest

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
    val context = LocalContext.current
    //Collects the ui state from the view model as a state
    val postsStates by nametagDuckItTestPostsListScreenViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        nametagDuckItTestPostsListScreenViewModel.getPosts()
    }

    when (val postState = postsStates) {
        is DuckItPostsUIState.Loading ->
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(modifier = modifier)
                    Text(modifier = modifier.fillMaxWidth().padding(dimensionResource(R.dimen.standard_padding)),
                        textAlign = TextAlign.Center,
                        text = stringResource(id = R.string.post_list_loading),
                        style = MaterialTheme.typography.displaySmall)
                }
            }
        is DuckItPostsUIState.Success -> {
            LaunchedEffect(postState.upVoteState) {
                if (postState.upVoteState is VoteState.UpVoteError) {
                    snackbarHostState.showSnackbar(context.getString(R.string.post_list_upvote_error))
                    nametagDuckItTestPostsListScreenViewModel.resetVoteStates(postState.upVoteState)
                }
            }
            LaunchedEffect(postState.downVoteState) {
                if (postState.downVoteState is VoteState.DownVoteError) {
                    snackbarHostState.showSnackbar(context.getString(R.string.post_list_downvote_error))
                    nametagDuckItTestPostsListScreenViewModel.resetVoteStates(postState.downVoteState)
                }
            }
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
        is DuckItPostsUIState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        modifier = modifier.fillMaxWidth().padding(dimensionResource(R.dimen.standard_padding)),
                        textAlign = TextAlign.Center,
                        text = stringResource(id = R.string.post_list_error),
                        style = MaterialTheme.typography.displaySmall
                    )
                    Button(modifier = modifier.fillMaxWidth().padding(dimensionResource(R.dimen.standard_padding)), onClick = { nametagDuckItTestPostsListScreenViewModel.getPosts() }) {
                        Text(text = stringResource(id = R.string.post_list_error_retry))
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

    Card(modifier = modifier.fillMaxWidth().padding(start = dimensionResource(R.dimen.standard_padding), end = dimensionResource(R.dimen.standard_padding), top = dimensionResource(R.dimen.small_padding), bottom = dimensionResource(R.dimen.small_padding)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = modifier.fillMaxWidth()) {
            Text(modifier = modifier.fillMaxWidth().padding(start = dimensionResource(R.dimen.standard_padding), end = dimensionResource(R.dimen.standard_padding), top = dimensionResource(R.dimen.small_padding)),
                text = post.headline,
                style = MaterialTheme.typography.titleLarge)
            AsyncImage(modifier = modifier.fillMaxWidth().padding(top = dimensionResource(R.dimen.small_padding)), model = post.image, contentDescription = stringResource(id = R.string.post_image_description))
            Row {
                IconButton(modifier = modifier.padding(dimensionResource(R.dimen.tiny_padding)), onClick = {
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
                Text(modifier = modifier.padding(dimensionResource(R.dimen.tiny_padding)).align(Alignment.CenterVertically),
                    text = post.upvotes.toString(),
                    style = MaterialTheme.typography.titleMedium)
                IconButton(modifier = modifier.padding(dimensionResource(R.dimen.tiny_padding)), onClick = {
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
    FloatingActionButton(modifier = modifier.testTag("newPostFAB"), onClick = {
        navController.navigate(Screens.NewPost.route)
    }) {
        Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.new_post_fab_description))
    }
}