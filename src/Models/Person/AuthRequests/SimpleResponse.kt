package com.george.Models.Person.AuthRequests

import com.george.Models.Person.users.User
import kotlinx.serialization.Serializable

@Serializable
data class SimpleResponse(
    val success: Boolean,
    val user: User? = null,
    val message: String
)
