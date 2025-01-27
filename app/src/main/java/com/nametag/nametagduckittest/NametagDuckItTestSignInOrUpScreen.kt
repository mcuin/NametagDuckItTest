package com.nametag.nametagduckittest

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Composable for the sign in or up screen
 * @param modifier The modifier to apply to the composable
 * @param navHostController The navigation controller to navigate to other screens
 * @param nametagDuckItTestSignInOrUpViewModel The view model for the sign in or up screen
 */
@Composable
fun NametagDuckItSignInOrUpScreen(modifier: Modifier, navHostController: NavHostController, nametagDuckItTestSignInOrUpViewModel: NametagDuckItTestSignInOrUpViewModel = hiltViewModel()) {

    //Scope for coroutines
    val scope = rememberCoroutineScope()
    //Snackbar host state for the snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    //Flow for the login success state
    val uiState by nametagDuckItTestSignInOrUpViewModel.uiState.collectAsStateWithLifecycle()
    //Context to get resources
    val context = LocalContext.current
    //Launched effect to collect the login success state
    LaunchedEffect(uiState.loginCode) {
            when (uiState.loginCode) {
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

    LaunchedEffect(uiState.signUpCode) {
        when (uiState.signUpCode) {
            200 -> {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.sign_up_success))
                }
                navHostController.popBackStack()
            }

            409 -> { /* TODO When an actual sign up screen would be implemented this would be needed to handle */ }
        }
    }

    Scaffold(modifier = modifier.testTag("signInOrUpScreen"), topBar = { DuckItTopToolbar(modifier = modifier, titleResourceId = R.string.login_description, navController = navHostController, isLoggedIn = false) },
        floatingActionButton = { SignInOrUpFAB(modifier = modifier, nametagDuckItTestSignInOrUpViewModel = nametagDuckItTestSignInOrUpViewModel, scope = scope, snackbarHostState = snackbarHostState, context = context, uiState = uiState) },
        snackbarHost = { SnackbarHost(modifier = modifier.testTag("signInOrUpSnackbar"), hostState = snackbarHostState) }) { paddingValues ->

        Column(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            EmailTextField(modifier = modifier, nametagDuckItTestSignInOrUpViewModel = nametagDuckItTestSignInOrUpViewModel, uiState = uiState)
            PasswordTextField(modifier = modifier, nametagDuckItTestSignInOrUpViewModel = nametagDuckItTestSignInOrUpViewModel, uiState = uiState)
        }
    }
}

/**
 * Composable for the email text field
 * @param modifier The modifier to apply to the composable
 * @param nametagDuckItTestSignInOrUpViewModel The view model for the sign in or up screen
 */
@Composable
fun EmailTextField(modifier: Modifier, nametagDuckItTestSignInOrUpViewModel: NametagDuckItTestSignInOrUpViewModel, uiState: SignInOrSignUpUiState) {

    OutlinedTextField(modifier = modifier.testTag("emailTextField").fillMaxWidth(),
        label = { Text(text = stringResource(id = R.string.email_label)) },
        value = uiState.emailText,
        onValueChange = nametagDuckItTestSignInOrUpViewModel::updateEmailText,
        isError = uiState.emailError,
        supportingText = { if (uiState.emailError) Text(modifier = modifier.testTag("emailErrorText"), text = stringResource(id = R.string.email_error)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        trailingIcon = {
            if (uiState.emailText.isNotBlank()) {
                IconButton(modifier = modifier.testTag("emailClear"), onClick = { nametagDuckItTestSignInOrUpViewModel.updateEmailText("") }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = stringResource(id = R.string.back_description))
                }
            }
        }
    )
}

/**
 * Composable for the password text field
 * @param modifier The modifier to apply to the composable
 * @param nametagDuckItTestSignInOrUpViewModel The view model for the sign in or up screen
 */
@Composable
fun PasswordTextField(modifier: Modifier, nametagDuckItTestSignInOrUpViewModel: NametagDuckItTestSignInOrUpViewModel, uiState: SignInOrSignUpUiState) {

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(modifier = modifier.testTag("passwordTextField").fillMaxWidth(),
        label = { Text(text = stringResource(id = R.string.password_label)) },
        value = uiState.passwordText,
        onValueChange = nametagDuckItTestSignInOrUpViewModel::updatePasswordText,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = uiState.passwordError,
        supportingText = { if (uiState.passwordError) Text(modifier = modifier.testTag("passwordErrorText"), text = stringResource(id = R.string.password_error)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(modifier = modifier.testTag("passwordVisibility"), onClick = { passwordVisible = !passwordVisible }) {
                Icon(painter = if (passwordVisible) painterResource(R.drawable.ic_visibility) else painterResource(R.drawable.ic_visibility_off),
                    contentDescription = if (passwordVisible) stringResource(id = R.string.password_visible_description) else stringResource(id = R.string.password_hidden_description))
            }
        }
    )
}

/**
 * Composable for the sign in or up floating action button
 * @param modifier The modifier to apply to the composable
 * @param nametagDuckItTestSignInOrUpViewModel The view model for the sign in or up screen
 */
@Composable
fun SignInOrUpFAB(modifier: Modifier, nametagDuckItTestSignInOrUpViewModel: NametagDuckItTestSignInOrUpViewModel,
                  scope: CoroutineScope, snackbarHostState: SnackbarHostState, context: Context, uiState: SignInOrSignUpUiState) {

    ExtendedFloatingActionButton(modifier = modifier.testTag("signInOrUpFAB"), onClick = {
        when {
            uiState.emailText.isBlank() ||
                    uiState.passwordText.isBlank() ||
                    uiState.passwordError ||
                    uiState.emailError -> {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.invalid_login))
                }
            }
            uiState.loading -> {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.sign_in_or_up_loading))
                }
            }
            else -> {
                nametagDuckItTestSignInOrUpViewModel.signIn()
            }
        }
    }, content = {
        if (!uiState.loading) {
            Icon(
                painter = painterResource(id = R.drawable.ic_login),
                contentDescription = stringResource(id = R.string.login_description)
            )
            Text(text = stringResource(id = R.string.login_description))
        } else {
            CircularProgressIndicator(modifier = modifier.testTag("signInOrUploadingIndicator"))
        }
    })
}