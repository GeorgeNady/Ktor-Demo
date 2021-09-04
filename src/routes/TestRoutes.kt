package com.george.routes

import com.george.utiles.ExtensionFunctionHelper.respondJsonResponse
import com.george.utiles.StatusCodesHelper
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import models.MySession

object TestRoutes {

    fun Route.testRoutes() {

        get("/api/v1/test") {
            call.respondJsonResponse(mapOf("message" to "Hello World!"), StatusCodesHelper.HTTP_OK)
        }

        get("/api/v1/session/increment") {
            val session = call.sessions.get<MySession>() ?: MySession()
            call.sessions.set(session.copy(count = session.count + 1))
            call.respondText("Counter is ${session.count}. Refresh to increment")
        }

    }

}