package com.nametag.nametagduckittest

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest

@Composable
fun NametagDuckItTestNewPostScreen(modifier: Modifier, navController: NavHostController) {

    Scaffold(modifier = modifier,
        topBar = { DuckItTopToolbar(modifier, titleResourceId =  R.string.new_post_title, navController = navController, isLoggedIn = true) },
        floatingActionButton = { SubmitPostFAB(modifier = modifier) }) { paddingValues ->
        Column(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            HeadlineTextEntry(modifier = modifier)
            ImageUploadButton(modifier = modifier)
        }
    }
}

@Composable
fun HeadlineTextEntry(modifier: Modifier) {

    OutlinedTextField(modifier = modifier.fillMaxWidth(),
        value = "",
        onValueChange = {},
        label = { Text(text = stringResource(id = R.string.new_post_headline_label)) })
}

@Composable
fun ImageUploadButton(modifier: Modifier) {

    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri
        }
    }

    if (imageUri != null) {
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(data = imageUri)
                .build()
        )
        Image(modifier = modifier.fillMaxWidth(), painter = painter, contentDescription = stringResource(id = R.string.new_post_image_upload_label))
    } else {
        Button(modifier = modifier, onClick = {
            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            Text(text = stringResource(id = R.string.new_post_image_upload_label))
        }
    }
}

@Composable
fun SubmitPostFAB(modifier: Modifier) {
    ExtendedFloatingActionButton(modifier = modifier, onClick = {}) {
        Text(text = stringResource(id = R.string.new_post_submit_label))
    }
}