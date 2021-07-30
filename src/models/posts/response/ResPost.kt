package com.george.models.posts.response

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class ResPost(
    @BsonId val id: String,
    val user: ResUser,
    val content: String,
    val likes_count: Int,
    val likes_users: List<ResUser>? = null,
    val dislike_count: Int,
    val dislike_users: List<ResUser>? = null,
    val created_at: String,
    val modified_at: String
)
