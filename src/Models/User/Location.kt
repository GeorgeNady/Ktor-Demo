package com.george.Models.User
import kotlinx.serialization.Serializable



@Serializable
data class Location(
    val lat: Double,
    val long: Double
)