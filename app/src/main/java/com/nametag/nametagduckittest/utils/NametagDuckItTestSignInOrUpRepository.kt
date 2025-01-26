package com.nametag.nametagduckittest.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NametagDuckItTestSignInOrUpRepository @Inject constructor(private val apiService: APIService) {

    fun signIn(signInRequest: SignInRequest): Flow<Response<SignInOrUpResponse>> = flow {
        emit(apiService.signIn(signInRequest))
    }

    fun signUp(signUpRequest: SignUpRequest): Flow<Response<SignInOrUpResponse>> = flow {
        emit(apiService.signUp(signUpRequest))
    }
}