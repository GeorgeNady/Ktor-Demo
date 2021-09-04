package com.george.models.users

import io.ktor.auth.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class User(
    @BsonId val _id: String? = null,
    val username: String,
    val email: String,
    val avatar: String,
    val phone: String,
    val hashPassword: String?
) : Principal