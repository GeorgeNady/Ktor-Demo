package com.george.utiles

import com.cloudinary.Cloudinary

object CloudinaryConfig {

    const val CLOUDINARY_API = "https://api.cloudinary.com/v1_1/ktordemoapp/image/upload"
    const val CLOUDINARY_PRESET  = "q4yrspsn/image/upload"

    val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "ktordemoapp",
            "api_key" to "163176344476878",
            "api_secret" to "Q1yR_fGt9ZKIjRSb2I42gyI54gU"
        )
    )




}