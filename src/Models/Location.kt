package com.george.Models
import kotlinx.serialization.Serializable



@Serializable
data class Location(
    val lat: Double,
    val long: Double
)