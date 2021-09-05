package com.george.models.posts.response

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class ResUser(
    @BsonId val _id: String,
    val username: String,
    val avatar:String,
    val email: String,
    val phone: String
)