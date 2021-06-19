package com.george.Models


data class User(
    val uid: String,
    val name: String,
    val email: String,
    val phone_number: String,
    val location: Location
)