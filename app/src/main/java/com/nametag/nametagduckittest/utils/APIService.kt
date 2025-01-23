package com.nametag.nametagduckittest.utils

import retrofit2.http.GET

/**
 * Interface for the Retrofit API service.
 */
interface APIService {

    @GET("posts")
    suspend fun getPosts(): Posts
}