package com.nametag.nametagduckittest

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Composable function for the new post screen.
 * @param modifier Modifier to be applied to the composable
 * @param navController NavController to be used for navigation
 * @param nametagDuckItTestNewPostViewModel ViewModel for the new post screen provided by Hilt
 */
@Composable
fun NametagDuckItTestNewPostScreen(modifier: Modifier, navController: NavHostController, nametagDuckItTestNewPostViewModel: NametagDuckItTestNewPostViewModel = hiltViewModel()) {

    //Coroutine scope and snackbar host state for showing snackbar messages
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    //Context for getting string resources
    val context = LocalContext.current
    //Collects the upload success state from the view model as a shared flow
    val uploadSuccess by nametagDuckItTestNewPostViewModel.uploadSuccess.collectAsStateWithLifecycle(null)

    //Launched effect to handle the upload success state showing snackbars to the user
    LaunchedEffect(uploadSuccess) {
        when (uploadSuccess) {
            true -> {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.new_post_success))
                    delay(500)
                    navController.popBackStack()
                }
            }
            false -> {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.new_post_error))
                }
            }
            else -> {}
        }
    }

    Scaffold(modifier = modifier,
        topBar = { DuckItTopToolbar(modifier, titleResourceId =  R.string.new_post_title, navController = navController, isLoggedIn = true) },
        floatingActionButton = { SubmitPostFAB(modifier = modifier, nametagDuckItTestNewPostViewModel = nametagDuckItTestNewPostViewModel, scope = scope, snackbarHostState = snackbarHostState, context = context) },
        snackbarHost = { SnackbarHost(modifier = modifier.testTag("newPostSnackbar"), hostState = snackbarHostState) }) { paddingValues ->
        Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(paddingValues).imePadding()) {
            HeadlineTextEntry(modifier = modifier, nametagDuckItTestNewPostViewModel = nametagDuckItTestNewPostViewModel)
            ImageLinkTextEntry(modifier = modifier, nametagDuckItTestNewPostViewModel = nametagDuckItTestNewPostViewModel)
        }
    }
}

/**
 * Composable function for the headline text entry field.
 * @param modifier Modifier to be applied to the composable
 * @param nametagDuckItTestNewPostViewModel ViewModel for the new post screen provided by Hilt
 */
@Composable
fun HeadlineTextEntry(modifier: Modifier, nametagDuckItTestNewPostViewModel: NametagDuckItTestNewPostViewModel) {

    OutlinedTextField(modifier = modifier.fillMaxWidth().testTag("headlineTextField"),
        value = nametagDuckItTestNewPostViewModel.headlineText.value,
        onValueChange = {
            nametagDuckItTestNewPostViewModel.updateHeadlineText(it)
        },
        label = { Text(text = stringResource(id = R.string.new_post_headline_label)) },
        isError = nametagDuckItTestNewPostViewModel.headLineError.value,
        trailingIcon = {
            if (nametagDuckItTestNewPostViewModel.headlineText.value.isNotBlank()) {
                IconButton(modifier = modifier.testTag("newPostHeadlineClear"), onClick = {
                    nametagDuckItTestNewPostViewModel.updateHeadlineText("")
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(id = R.string.clear_description)
                    )
                }
            }
        },
        singleLine = true,
        supportingText = {
            if (nametagDuckItTestNewPostViewModel.headLineError.value) {
                Text(modifier = modifier.testTag("headlineError"), text = stringResource(id = R.string.new_post_headline_error))
            }
        })
}

/**
 * Composable function for the image link text entry field and includes an image preview through Coil
 * @param modifier Modifier to be applied to the composable
 * @param nametagDuckItTestNewPostViewModel ViewModel for the new post screen provided by Hilt
 */
@Composable
fun ImageLinkTextEntry(modifier: Modifier, nametagDuckItTestNewPostViewModel: NametagDuckItTestNewPostViewModel) {

    if (nametagDuckItTestNewPostViewModel.imageData.value.isNotBlank() && !nametagDuckItTestNewPostViewModel.imageError.value) {
        Text(modifier = modifier.fillMaxWidth().testTag("newPostPreviewImageTitle"), text = stringResource(id = R.string.new_post_image_preview))
        AsyncImage(
            modifier = modifier.fillMaxWidth().testTag("newPostPreviewImage"),
            model = nametagDuckItTestNewPostViewModel.imageData.value,
            contentDescription = stringResource(id = R.string.post_image_description),
            onError = {
                nametagDuckItTestNewPostViewModel.imageNotFound(true)
            },
            onSuccess = {
                nametagDuckItTestNewPostViewModel.imageNotFound(false)
            })
    }
    OutlinedTextField(
        modifier = modifier.fillMaxWidth().testTag("newPostImageTextField"),
        value = nametagDuckItTestNewPostViewModel.imageData.value,
        onValueChange = {
            nametagDuckItTestNewPostViewModel.updateImageUri(it)
        },
        label = { Text(text = stringResource(id = R.string.new_post_image_link_label)) },
        isError = nametagDuckItTestNewPostViewModel.imageError.value,
        trailingIcon = {
            if (nametagDuckItTestNewPostViewModel.imageData.value.isNotBlank()) {
                IconButton(modifier = modifier.testTag("newPostLinkClear"), onClick = {
                    nametagDuckItTestNewPostViewModel.updateImageUri("")
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(id = R.string.clear_description)
                    )
                }
            }
        },
        singleLine = false,
        supportingText = {
            if (nametagDuckItTestNewPostViewModel.imageError.value) {
                Text(modifier = modifier.testTag("newPostImageError"), text = stringResource(id = R.string.new_post_link_error))
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done)
    )
}

/**
 * Composable function for the submit post FAB.
 * @param modifier Modifier to be applied to the composable
 * @param nametagDuckItTestNewPostViewModel ViewModel for the new post screen provided by Hilt
 */
@Composable
fun SubmitPostFAB(modifier: Modifier, nametagDuckItTestNewPostViewModel: NametagDuckItTestNewPostViewModel, scope: CoroutineScope, snackbarHostState: SnackbarHostState, context: Context) {

    val loading by nametagDuckItTestNewPostViewModel.loading.collectAsStateWithLifecycle(false)

    ExtendedFloatingActionButton(modifier = modifier.testTag("newPostFAB"), onClick = {
        when {
            !nametagDuckItTestNewPostViewModel.headLineError.value
                    && !nametagDuckItTestNewPostViewModel.imageError.value
                    && nametagDuckItTestNewPostViewModel.imageData.value.isBlank()
                    && nametagDuckItTestNewPostViewModel.headlineText.value.isBlank() -> {
                        scope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.new_post_submit_error))
                        }
                    }
            loading -> {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.new_post_loading_label))
                }
            }
            else -> {
                nametagDuckItTestNewPostViewModel.submitPost()
            }
        }
    }) {
        if (loading) {
            CircularProgressIndicator()
        } else {
            Text(text = stringResource(id = R.string.new_post_submit_label))
        }
    }
}