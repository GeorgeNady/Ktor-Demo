package com.george.Models.Person.users

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val success:Boolean,
    val user:User,
    val message:String
)