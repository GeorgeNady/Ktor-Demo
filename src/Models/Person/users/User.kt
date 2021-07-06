package com.george.Models.Person.users

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val email: String,
    val password: String,
    val token: String,
    val phone: String
)