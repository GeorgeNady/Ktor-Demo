package com.george.models.posts.response

import com.george.models.ParentResponse
import kotlinx.serialization.Serializable

@Serializable
data class PostResponse (
    override val success: Boolean,
    val data : ResPost? = null,
    override val message: String,
) : ParentResponse()


