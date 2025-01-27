package com.nametag.nametagduckittest.utils

import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

/**
 * Repository for the api calls and data transactions for the sign in or up screen
 * @param apiService The api service to make the api calls injected by hilt
 */
class NametagDuckItTestSignInOrUpRepository @Inject constructor(private val apiService: APIService) {

    /**
     * Sign in a user to the app
     * @param signInRequest The sign in request to send to the api containing username and password
     * @return Response<SignInOrUpResponse> The sign in response from the api
     */
    suspend fun signIn(signInRequest: SignInRequest): Response<SignInOrUpResponse> = try {
        apiService.signIn(signInRequest)
    } catch (e: HttpException) {
        Response.error(e.code(), e.response()?.errorBody() ?: "".toResponseBody(null))
    }

    /**
     * Sign up a user to the app
     * @param signUpRequest The sign up request to send to the api containing username, password and email
     * @return Response<SignInOrUpResponse> The sign up response from the api
     */
    suspend fun signUp(signUpRequest: SignUpRequest): Response<SignInOrUpResponse> = try {
        apiService.signUp(signUpRequest)
    } catch (e: HttpException) {
        Response.error(e.code(), e.response()?.errorBody() ?: "".toResponseBody(null))
    }
}