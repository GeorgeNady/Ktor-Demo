package com.george

import com.fasterxml.jackson.databind.SerializationFeature
import com.george.Models.Person.users.User
import com.george.Routes.AuthRoutes.authRoutes
import com.george.data.mongo.MongoDataService.Companion.mongoDataService
import com.george.utiles.Constants.USERS_COLLECTION
import com.george.utiles.ExtensionFunctionHelper.respondJsonResponse
import com.george.utiles.JwtService
import com.george.utiles.StatusCodesHelper.HTTP_OK
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
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
        gson {}
        jackson { enable(SerializationFeature.INDENT_OUTPUT) }
    }
    install(Authentication) {
        jwt {
            verifier(JwtService.verifier)
            realm = ""
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val userDoc = mongoDataService.getDocumentByEmail(USERS_COLLECTION,email)
                val user = User(
                    id = userDoc!!.getValue("_id").toString(),
                    username = userDoc.getValue("username").toString(),
                    email = userDoc.getValue("email").toString(),
                    phone = userDoc.getValue("phone").toString(),
                    hashPassword = userDoc.getValue("hashPassword").toString()
                )
                user
            }
        }
    }


    routing {

        get("/api/test") {
            call.respondJsonResponse(mapOf("message" to "Hello World!"),HTTP_OK)
        }

        authRoutes(mongoDataService)

    }

}
