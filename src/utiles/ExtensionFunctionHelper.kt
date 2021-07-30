package com.george.utiles

import com.george.models.users.User
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import java.lang.Exception

object ExtensionFunctionHelper {

    fun Any.toJson() = Gson().toJson(this)!!

    suspend fun ApplicationCall.respondJsonResponse(text:Any, statusCode: HttpStatusCode) =
        this.respondText(text.toJson(), StatusCodesHelper.application_json, statusCode)

    fun <T> Map<String,Any>.getMapValue(key:String) : T {
        try {
            return this.getValue(key) as T
        } catch (e: Exception) {
            println("Error: $e")
        }
        return "" as T
    }

    fun userDataExtractor(userDoc: Map<String, Any>) = User(
        id = userDoc.getValue("_id").toString(),
        username = userDoc.getValue("username").toString(),
        email = userDoc.getValue("email").toString(),
        phone = userDoc.getValue("phone").toString(),
        hashPassword = userDoc.getValue("hashPassword").toString()
    )

    /*fun postDataExtractor(postDoc: Map<String, Any>) = Post(
        id = postDoc.getValue("_id").toString(),
        user = postDoc.getValue("user") as User,
        content = postDoc.getValue("content").toString(),
        likes_count = postDoc.getValue("likes_count").toString().toInt(),
        created_at = postDoc.getValue("created_at").toString(),
        likes_users = listOf()
    )*/


}