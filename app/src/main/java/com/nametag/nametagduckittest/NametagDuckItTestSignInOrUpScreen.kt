package com.nametag.nametagduckittest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun NametagDuckItSignInOrUpScreen(modifier: Modifier, navHostController: NavHostController, nametagDuckItTestSignInOrUpViewModel: NametagDuckItTestSignInOrUpViewModel = hiltViewModel()) {

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val signUpSuccess by nametagDuckItTestSignInOrUpViewModel.signUpSuccess.collectAsStateWithLifecycle(initialValue = -1)

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        nametagDuckItTestSignInOrUpViewModel.loginSuccess.collect { code ->
            when (code) {
                200 -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.sign_in_success))
                    }
                    navHostController.popBackStack()
                }

                403 -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.invalid_login))
                    }
                }

                404 -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.account_not_found))
                    }
                }

                -1 -> {}
                else -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.unknown_error))
                    }
                }
            }
        }
    }

    LaunchedEffect(signUpSuccess) {
        when (signUpSuccess) {
            200 -> {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.sign_up_success))
                }
                navHostController.popBackStack()
            }

            409 -> { /* TODO When an actual sign up screen would be implemented this would be needed to handle */ }
        }
    }

    Scaffold(topBar = { DuckItTopToolbar(modifier = modifier, titleResourceId = R.string.login_description, navController = navHostController, isLoggedIn = false) },
        floatingActionButton = { SignInOrUpFAB(modifier = modifier, nametagDuckItTestSignInOrUpViewModel = nametagDuckItTestSignInOrUpViewModel) },
        snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->

        Column(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            EmailTextField(modifier = modifier, nametagDuckItTestSignInOrUpViewModel = nametagDuckItTestSignInOrUpViewModel)
            PasswordTextField(modifier = modifier, nametagDuckItTestSignInOrUpViewModel = nametagDuckItTestSignInOrUpViewModel)
        }
    }
}

@Composable
fun EmailTextField(modifier: Modifier, nametagDuckItTestSignInOrUpViewModel: NametagDuckItTestSignInOrUpViewModel) {

    OutlinedTextField(modifier = modifier.fillMaxWidth(),
        label = { Text(text = stringResource(id = R.string.email_label)) },
        value = nametagDuckItTestSignInOrUpViewModel.emailText,
        onValueChange = nametagDuckItTestSignInOrUpViewModel::updateEmailText,
        isError = nametagDuckItTestSignInOrUpViewModel.emailError,
        supportingText = { if (nametagDuckItTestSignInOrUpViewModel.emailError) Text(text = stringResource(id = R.string.email_error)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        trailingIcon = {
            if (nametagDuckItTestSignInOrUpViewModel.emailText.isNotBlank()) {
                IconButton(onClick = { nametagDuckItTestSignInOrUpViewModel.updateEmailText("") }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = stringResource(id = R.string.back_description))
                }
            }
        }
    )
}

@Composable
fun PasswordTextField(modifier: Modifier, nametagDuckItTestSignInOrUpViewModel: NametagDuckItTestSignInOrUpViewModel) {

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(modifier = modifier.fillMaxWidth(),
        label = { Text(text = stringResource(id = R.string.password_label)) },
        value = nametagDuckItTestSignInOrUpViewModel.passwordText,
        onValueChange = nametagDuckItTestSignInOrUpViewModel::updatePasswordText,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = nametagDuckItTestSignInOrUpViewModel.passwordError,
        supportingText = { if (nametagDuckItTestSignInOrUpViewModel.passwordError) Text(text = stringResource(id = R.string.password_error)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(painter = if (passwordVisible) painterResource(R.drawable.ic_visibility) else painterResource(R.drawable.ic_visibility_off),
                    contentDescription = if (passwordVisible) stringResource(id = R.string.password_visible_description) else stringResource(id = R.string.password_hidden_description))
            }
        }
    )
}

@Composable
fun SignInOrUpFAB(modifier: Modifier, nametagDuckItTestSignInOrUpViewModel: NametagDuckItTestSignInOrUpViewModel) {
    ExtendedFloatingActionButton(modifier = modifier, onClick = {
        nametagDuckItTestSignInOrUpViewModel.signIn()
    }, content = {
        Icon(painter = painterResource(id = R.drawable.ic_login), contentDescription = stringResource(id = R.string.login_description))
        Text(text = stringResource(id = R.string.login_description))
    })
}