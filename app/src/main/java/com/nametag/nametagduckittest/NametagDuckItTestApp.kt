package com.nametag.nametagduckittest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Main app composable to set up and host navigation.
 * @param navController The navigation controller to hold nav state for all composables.
 */
@Composable
fun NametagDuckItTestApp(navController: NavHostController = rememberNavController()) {

    Column(modifier = Modifier.fillMaxSize()) {

        NavHost(navController = navController, startDestination = Screens.PostsList.route) {
            composable(Screens.PostsList.route) {
                NametagDuckItTestPostsListScreen(modifier = Modifier, navController = navController)
            }
            composable(Screens.SignInOrUp.route) {
                NametagDuckItSignInOrUpScreen(modifier = Modifier, navHostController = navController)
            }
            composable(Screens.NewPost.route) {
                NametagDuckItTestNewPostScreen(modifier = Modifier, navController = navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuckItTopToolbar(modifier: Modifier, titleResourceId: Int, navController: NavHostController, viewModel: NametagDuckItTestAppViewModel = hiltViewModel(), isLoggedIn: Boolean) {

    TopAppBar(modifier = modifier, title = { Text(text = stringResource(id = titleResourceId)) },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_description)
                    )
                }
            }
        },
        actions = {
            if (navController.currentBackStackEntry?.destination?.route != Screens.SignInOrUp.route) {
                if (isLoggedIn) {
                    IconButton(onClick = {
                        viewModel.logout()
                        if (navController.currentBackStackEntry?.destination?.route != Screens.PostsList.route) {
                            navController.navigate(Screens.PostsList.route)
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logout),
                            contentDescription = stringResource(id = R.string.logout_description)
                        )
                    }
                } else {
                    IconButton(onClick = {
                        navController.navigate(Screens.SignInOrUp.route)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_login),
                            contentDescription = stringResource(id = R.string.login_description)
                        )
                    }
                }
            }
        })
}

/**
 * Class to represent all screens in the app.
 * @param route The route name for the screen.
 */
sealed class Screens(val route: String) {
    object PostsList : Screens("postsList")
    object SignInOrUp : Screens("signInOrUp")
    object NewPost : Screens("newPost")
}