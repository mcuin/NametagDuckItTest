package com.nametag.nametagduckittest.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import retrofit2.Response
import javax.inject.Inject

/**
 * Repository for the api calls and data transactions for the posts list screen
 * @param apiService The api service to make the api calls injected by hilt
 */
class NametagDuckItPostsListRepository @Inject constructor(private val apiService: APIService) {

    fun getPosts(): Flow<Response<PostsResponse>> = flow {
        emit(apiService.getPosts(token = null))
    }

    suspend fun upVotePost(token : String, postId: String): Response<VotesResponse> {
        return apiService.upvotePost(token = "Bearer \"$token\"", postId = postId)
    }

    suspend fun downVotePost(token: String, postId: String): Response<VotesResponse> {
        return apiService.downvotePost(token = "Bearer \"$token\"", postId = postId)
    }
}