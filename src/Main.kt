package com.george

import com.fasterxml.jackson.databind.SerializationFeature
import com.george.Routes.AuthRoutes.authRoutes
import com.george.data.mongo.MongoDataService.Companion.mongoDataService
import com.george.utiles.ExtensionFunctionHelper.respondJsonResponse
import com.george.utiles.StatusCodesHelper.HTTP_OK
import io.ktor.application.*
import io.ktor.server.netty.EngineMain
import io.ktor.routing.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.jackson.jackson
import io.ktor.locations.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(Locations)
    install(ContentNegotiation) {
        gson {
            // Logic here
        }
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }


    routing {

        get("/api/test") {
            call.respondJsonResponse("{\"message\":\"Hello World!\"}",HTTP_OK)
        }

        authRoutes(mongoDataService)

    }

}
