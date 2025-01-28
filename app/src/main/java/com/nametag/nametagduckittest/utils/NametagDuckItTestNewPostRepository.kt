package com.nametag.nametagduckittest.utils

import retrofit2.Response
import javax.inject.Inject

/**
 * Repository for the api calls and data transactions for the new post screen
 * @param apiService The api service to make the api calls injected by hilt
 */
class NametagDuckItTestNewPostRepository @Inject constructor(private val apiService: APIService) {

    /**
     * Send a new post to the api
     * @param token The token to use for the api call
     * @param newPostRequest The new post request to send to the api containing headline and image url
     * @return Response<NewPostResponse> The new post response from the api
     */
    suspend fun createPost(token: String, newPostRequest: NewPostRequest): Response<NewPostResponse> {
        return try {
            apiService.createPost(token = "Bearer $token", newPostRequest)
        } catch (e: NoNetworkException) {
            Response.error(e.code, e.response)
        }
    }
}