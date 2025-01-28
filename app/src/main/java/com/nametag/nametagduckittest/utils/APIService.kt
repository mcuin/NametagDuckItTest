package com.nametag.nametagduckittest.utils

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Interface for the Retrofit API service.
 */
interface APIService {

    @GET("posts")
    suspend fun getPosts(@Header("Authorization") token: String?): Response<PostsResponse>

    @POST("signin")
    suspend fun signIn(@Body signInRequest: SignInRequest): Response<SignInOrUpResponse>

    @POST("signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignInOrUpResponse>

    @POST("posts/{id}/upvote")
    suspend fun upvotePost(@Header("Authorization") token: String, @Path("id") postId: String): Response<VotesResponse>

    @POST("posts/{id}/downvote")
    suspend fun downvotePost(@Header("Authorization") token: String, @Path("id") postId: String): Response<VotesResponse>

    @POST("posts")
    suspend fun createPost(@Header("Authorization") token: String, @Body newPostRequest: NewPostRequest): Response<NewPostResponse>
}