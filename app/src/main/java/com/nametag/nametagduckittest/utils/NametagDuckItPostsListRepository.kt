package com.nametag.nametagduckittest.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NametagDuckItPostsListRepository @Inject constructor(private val apiService: APIService) {

    fun getPosts(): Flow<Posts> = flow {
        emit(apiService.getPosts())
    }
}