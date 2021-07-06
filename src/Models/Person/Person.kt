package com.george.Models.User.Customer

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val _id: String? = null,
    val name: String,
    val email: String,
    val phone: String,
)