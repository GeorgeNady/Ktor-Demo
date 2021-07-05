package com.george.Models
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: Int,
    val name: String,
    val email: String,
    val phone_number: String,
    val location: Location
)