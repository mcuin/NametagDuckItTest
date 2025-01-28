package com.nametag.nametagduckittest.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import javax.inject.Inject

/**
 * Repository for the api calls and data transactions for the posts list screen
 * @param apiService The api service to make the api calls injected by hilt
 */
class NametagDuckItTestPostsListRepository @Inject constructor(private val apiService: APIService) {

    /**
     * Get the posts from the api and emit them as a flow so that the UI can react to changes when a post is added or removed
     * @return Flow<Response<PostsResponse>> The flow of posts
     */
    fun getPosts(token: String?): Flow<Response<PostsResponse>> = flow {
        emit(apiService.getPosts(token = token))
    }.catch {
        emit(Response.error(500, "".toResponseBody(null)))
    }

    /**
     * Upvote a post
     * @param token The token to use for the api call
     * @param postId The id of the post to upvote
     * @return Response<VotesResponse> The upvote response from the api
     */
    suspend fun upVotePost(token : String, postId: String): Response<VotesResponse> {
        return try {
            apiService.upvotePost(token = "Bearer \"$token\"", postId = postId)
        } catch (e: NoNetworkException) {
            Response.error(e.code, e.response)
        }
    }

    /**
     * Downvote a post
     * @param token The token to use for the api call
     * @param postId The id of the post to downvote
     * @return Response<VotesResponse> The downvote response from the api
     */
    suspend fun downVotePost(token: String, postId: String): Response<VotesResponse> {
        return try {
            apiService.downvotePost(token = "Bearer \"$token\"", postId = postId)
        } catch (e: NoNetworkException) {
            Response.error(e.code, e.response)
        }
    }
}