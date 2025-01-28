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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
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
    val signInOrSignUpUiState by nametagDuckItTestSignInOrUpViewModel.signInOrUpUiState.collectAsStateWithLifecycle()
    //Context to get resources
    val context = LocalContext.current
    //Launched effect to collect the login success state
    LaunchedEffect(signInOrSignUpUiState.loginCode) {
            when (val loginState = signInOrSignUpUiState.loginCode) {
                is LoginState.Success -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.sign_in_success))
                        delay(100)
                    }
                    navHostController.popBackStack()
                }

                is LoginState.Error -> {
                    when (loginState.code) {
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
                        else -> {
                            scope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.unknown_error))
                            }
                        }
                    }
                    nametagDuckItTestSignInOrUpViewModel.resetLoginState()
                }
                is LoginState.Ready -> return@LaunchedEffect
            }
        }

    LaunchedEffect(signInOrSignUpUiState.signUpCode) {
        when (val signUpState = signInOrSignUpUiState.signUpCode) {
            is SignUpState.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.sign_up_success))
                    delay(100)
                }
                navHostController.popBackStack()
            }
            is SignUpState.Error -> {
                when (signUpState.code) {
                    409 -> { /* TODO When an actual sign up screen would be implemented this would be needed to handle */ }
                    else -> {
                        scope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.unknown_error))
                        }
                    }
                }
                nametagDuckItTestSignInOrUpViewModel.resetSignUpState()
            }
            is SignUpState.Ready -> return@LaunchedEffect
        }
    }

    Scaffold(modifier = modifier.testTag("signInOrUpScreen"), topBar = { DuckItTopToolbar(modifier = modifier, titleResourceId = R.string.login_description, navController = navHostController, isLoggedIn = false) },
        floatingActionButton = { SignInOrUpFAB(modifier = modifier,
            nametagDuckItTestSignInOrUpViewModel = nametagDuckItTestSignInOrUpViewModel,
            scope = scope, snackbarHostState = snackbarHostState, context = context,
            signInOrSignUpUiStateEmailText = signInOrSignUpUiState.emailText,
            signInOrSignUpUiStatePasswordText = signInOrSignUpUiState.passwordText,
            signInOrSignUpUiStateEmailError = signInOrSignUpUiState.emailError,
            signInOrSignUpUiStatePasswordError = signInOrSignUpUiState.passwordError,
            signInOrSignUpUiStateLoading = signInOrSignUpUiState.loading) },
        snackbarHost = { SnackbarHost(modifier = modifier.testTag("signInOrUpSnackbar"), hostState = snackbarHostState) }) { paddingValues ->

        Column(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            EmailTextField(modifier = modifier, nametagDuckItTestSignInOrUpViewModel = nametagDuckItTestSignInOrUpViewModel,
                signInOrSignUpUiStateEmailText = signInOrSignUpUiState.emailText, signInOrSignUpUiStateEmailError = signInOrSignUpUiState.emailError)
            PasswordTextField(modifier = modifier, nametagDuckItTestSignInOrUpViewModel = nametagDuckItTestSignInOrUpViewModel,
                signInOrSignUpUiStatePasswordText = signInOrSignUpUiState.passwordText, signInOrSignUpUiStatePasswordError = signInOrSignUpUiState.passwordError)
        }
    }
}

/**
 * Composable for the email text field
 * @param modifier The modifier to apply to the composable
 * @param nametagDuckItTestSignInOrUpViewModel The view model for the sign in or up screen
 */
@Composable
fun EmailTextField(modifier: Modifier,
                   nametagDuckItTestSignInOrUpViewModel: NametagDuckItTestSignInOrUpViewModel,
                   signInOrSignUpUiStateEmailText: String, signInOrSignUpUiStateEmailError: Boolean) {

    OutlinedTextField(modifier = modifier.fillMaxWidth()
        .padding(start = dimensionResource(R.dimen.standard_padding), end = dimensionResource(R.dimen.standard_padding), top = dimensionResource(R.dimen.small_padding), bottom = dimensionResource(R.dimen.small_padding)).testTag("emailTextField"),
        label = { Text(text = stringResource(id = R.string.email_label)) },
        value = signInOrSignUpUiStateEmailText,
        onValueChange = nametagDuckItTestSignInOrUpViewModel::updateEmailText,
        isError = signInOrSignUpUiStateEmailError,
        supportingText = { if (signInOrSignUpUiStateEmailError) Text(modifier = modifier.testTag("emailErrorText"), text = stringResource(id = R.string.email_error), style = MaterialTheme.typography.bodyMedium) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        trailingIcon = {
            if (signInOrSignUpUiStateEmailText.isNotBlank()) {
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
fun PasswordTextField(modifier: Modifier,
                      nametagDuckItTestSignInOrUpViewModel: NametagDuckItTestSignInOrUpViewModel,
                      signInOrSignUpUiStatePasswordText: String, signInOrSignUpUiStatePasswordError: Boolean) {

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(modifier = modifier.fillMaxWidth().padding(start = dimensionResource(R.dimen.standard_padding), end = dimensionResource(R.dimen.standard_padding), top = dimensionResource(R.dimen.small_padding), bottom = dimensionResource(R.dimen.small_padding)).testTag("passwordTextField"),
        label = { Text(text = stringResource(id = R.string.password_label)) },
        value = signInOrSignUpUiStatePasswordText,
        onValueChange = nametagDuckItTestSignInOrUpViewModel::updatePasswordText,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = signInOrSignUpUiStatePasswordError,
        supportingText = { if (signInOrSignUpUiStatePasswordError) Text(modifier = modifier.testTag("passwordErrorText"), text = stringResource(id = R.string.password_error), style = MaterialTheme.typography.bodyMedium) },
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
                  scope: CoroutineScope, snackbarHostState: SnackbarHostState, context: Context,
                  signInOrSignUpUiStateEmailText: String, signInOrSignUpUiStatePasswordText: String,
                  signInOrSignUpUiStateEmailError: Boolean, signInOrSignUpUiStatePasswordError: Boolean,
                  signInOrSignUpUiStateLoading: Boolean) {

    ExtendedFloatingActionButton(modifier = modifier.testTag("signInOrUpFAB"), onClick = {
        when {
            signInOrSignUpUiStateEmailText.isBlank() ||
                    signInOrSignUpUiStatePasswordText.isBlank() ||
                    signInOrSignUpUiStatePasswordError ||
                    signInOrSignUpUiStateEmailError -> {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.invalid_login))
                }
            }
            signInOrSignUpUiStateLoading -> {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.sign_in_or_up_loading))
                }
            }
            else -> {
                nametagDuckItTestSignInOrUpViewModel.signIn()
            }
        }
    }, content = {
        if (!signInOrSignUpUiStateLoading) {
            Icon(
                modifier = modifier.padding(end = dimensionResource(R.dimen.tiny_padding)),
                painter = painterResource(id = R.drawable.ic_login),
                contentDescription = stringResource(id = R.string.login_description)
            )
            Text(modifier = modifier.padding(start = dimensionResource(R.dimen.tiny_padding)), text = stringResource(id = R.string.login_description))
        } else {
            CircularProgressIndicator(modifier = modifier.testTag("signInOrUploadingIndicator"))
        }
    })
}