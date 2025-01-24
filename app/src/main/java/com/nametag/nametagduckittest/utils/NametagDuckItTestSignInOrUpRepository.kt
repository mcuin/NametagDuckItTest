package com.nametag.nametagduckittest.utils

import retrofit2.Response
import javax.inject.Inject

class NametagDuckItTestSignInOrUpRepository @Inject constructor(private val apiService: APIService) {

    suspend fun signIn(signInRequest: SignInRequest): Response<SignInOrUpResponse> {
        return apiService.signIn(signInRequest)
    }

    suspend fun signUp(signUpRequest: SignUpRequest): Response<SignInOrUpResponse> {
        return apiService.signUp(signUpRequest)
    }
}