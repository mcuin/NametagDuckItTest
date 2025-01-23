package com.nametag.nametagduckittest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NametagDuckItTestApp(navController: NavHostController = rememberNavController()) {

    Column(modifier = Modifier.fillMaxSize()) {

        NavHost(navController = navController, startDestination = Screens.PostsList.route) {
            composable(Screens.PostsList.route) {
                NametagDuckItTestPostsListScreen(modifier = Modifier)
            }
        }
    }
}

sealed class Screens(val route: String) {
    object PostsList : Screens("postsList")
}