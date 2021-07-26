package com.george.Models.Person.users

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val email: String,
    val phone: String,
    val hashPassword: String
)