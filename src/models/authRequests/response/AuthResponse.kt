package com.george.models.authRequests.response

import com.george.models.users.User
import com.george.models.ParentResponse
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    override val success: Boolean,
    val data: User? = null,
    override val message: String
) : ParentResponse()
