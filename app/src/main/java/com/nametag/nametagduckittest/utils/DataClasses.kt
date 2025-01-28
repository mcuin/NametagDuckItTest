package com.nametag.nametagduckittest.utils

import kotlinx.serialization.Serializable

//Data class that represents an Post object
@Serializable
data class Post(val id: String, val headline: String, val image: String, val upvotes: Int, val author: String)

//List of post objects to deserialize from json provided by backend
@Serializable
data class PostsResponse(val Posts: List<Post>)

//Data class for the sign in request to sent to the backend
@Serializable
data class SignInRequest(val email: String, val password: String)

//Data class for the sign up request to sent to the backend
@Serializable
data class SignUpRequest(val email: String, val password: String)

//Data class for the sign in or up response from the backend
@Serializable
data class SignInOrUpResponse(val token: String)

//Data class for the votes response from the backend
@Serializable
data class VotesResponse(val upvotes: Int)

//Data class for the new post request to sent to the backend
@Serializable
data class NewPostRequest(val headline: String, val image: String)

//Data class for the new post response from the backend
@Serializable
data class NewPostResponse(val id: String)