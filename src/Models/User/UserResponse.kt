package com.george.Models.User

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val success: Boolean,
    val persons: MutableList<User>,
    val message: String
)

