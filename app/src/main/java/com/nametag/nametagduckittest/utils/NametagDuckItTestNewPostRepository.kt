package com.nametag.nametagduckittest.utils

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class NametagDuckItTestNewPostRepository @Inject constructor(private val apiService: APIService) {

    suspend fun createPost(token: String, newPostRequest: NewPostRequest): Response<NewPostResponse> {
        return apiService.createPost(token = "Bearer $token", newPostRequest)
    }
}