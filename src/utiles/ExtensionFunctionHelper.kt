package com.george.utiles

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
            this.getValue(key) as T
        } catch (e: Exception) {
            println("Error: $e")
        }
        return "" as T
    }
}