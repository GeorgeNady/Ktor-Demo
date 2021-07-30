package com.george.models.posts.response

import com.george.models.ParentResponse
import kotlinx.serialization.Serializable

@Serializable
data class PostsResponse(
    override val success: Boolean,
    val data: List<ResPost>? = null,
    val page: Int,
    val totalResult:Int,
    override val message: String
) : ParentResponse()
