package com.nametag.nametagduckittest.utils

import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Interface for the Retrofit API service.
 */
interface APIService {

    @GET("posts")
    suspend fun getPosts(): Response<Posts>

    @POST("signin")
    suspend fun signIn(@Body signInRequest: SignInRequest): Response<SignInOrUpResponse>

    @POST("signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignInOrUpResponse>

}