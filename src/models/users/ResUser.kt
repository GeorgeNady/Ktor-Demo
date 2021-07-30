package com.george.models.users

import io.ktor.auth.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class ResUser(
    @BsonId val id: String,
    val username: String,
    val email: String,
    val phone: String,
    val hashPassword: String?
)