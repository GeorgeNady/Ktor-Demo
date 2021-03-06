package com.george.models.posts

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class DbPost(
    @BsonId val _id: String? = null,
    val user_email: String,
    val content: String,
    val likes_count: Int,
    val likes_users_emails: MutableList<String>,
    val dislike_count: Int,
    val dislike_users_emails: MutableList<String>,
    val created_at: String,
    val modified_at: String
)
