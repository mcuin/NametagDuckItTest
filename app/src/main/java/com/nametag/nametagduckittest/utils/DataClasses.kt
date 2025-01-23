package com.nametag.nametagduckittest.utils

import kotlinx.serialization.Serializable
import javax.inject.Singleton

//Data class that represents an Post object
@Serializable
data class Post(val id: String, val headline: String, val image: String, val upvotes: Int, val author: String)

//List of post objects to deserialize from json provided by backend
@Serializable
data class Posts(val Posts: List<Post>)