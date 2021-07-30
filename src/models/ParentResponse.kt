package com.george.models

import kotlinx.serialization.Serializable

@Serializable
abstract class ParentResponse {
    abstract val success: Boolean
    abstract val message: String
}