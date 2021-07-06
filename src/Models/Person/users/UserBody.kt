package com.george.Models.Person.users

import kotlinx.serialization.Serializable

@Serializable
data class UserBody(
    val username: String,
    val email: String,
    val password: String,
    val phone: String
)