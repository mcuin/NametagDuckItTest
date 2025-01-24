package com.nametag.nametagduckittest.utils

import kotlinx.serialization.Serializable
import javax.inject.Singleton

//Data class that represents an Post object
@Serializable
data class Post(val id: String, val headline: String, val image: String, val upvotes: Int, val author: String)

//List of post objects to deserialize from json provided by backend
@Serializable
data class Posts(val Posts: List<Post>)

@Serializable
data class SignInRequest(val email: String, val password: String)

@Serializable
data class SignUpRequest(val email: String, val password: String)

@Serializable
data class SignInOrUpResponse(val token: String)