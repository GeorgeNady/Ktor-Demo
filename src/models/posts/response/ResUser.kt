package com.george.models.posts.response

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class ResUser(
    @BsonId val id: String,
    val username: String,
    val email: String,
    val phone: String
)