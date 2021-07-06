package com.george.Models.Person

import com.george.Models.User.Customer.Person
import kotlinx.serialization.Serializable

@Serializable
data class PersonResponse(
    val success: Boolean,
    val persons: MutableList<Person>,
    val message: String
)