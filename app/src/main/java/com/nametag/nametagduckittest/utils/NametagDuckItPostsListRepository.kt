package com.nametag.nametagduckittest.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repository for the api calls and data transactions for the posts list screen
 * @param apiService The api service to make the api calls injected by hilt
 */
class NametagDuckItPostsListRepository @Inject constructor(private val apiService: APIService) {

    fun getPosts(): Flow<Posts> = flow {
        emit(apiService.getPosts())
    }
}