package com.george

import com.fasterxml.jackson.databind.SerializationFeature
import com.george.routes.MultiPartsRoutes.multiPartsRoutes
import com.george.models.users.User
import com.george.routes.AuthRoutes.authRoutes
import com.george.mongodb.MongoDataService.Companion.mongoDataService
import com.george.routes.PostRoutes.postsRoutes
import com.george.routes.TestRoutes.testRoutes
import com.george.routes.UsersRoutes.usersRoutes
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
import io.ktor.response.*
import io.ktor.sessions.*
import models.MySession

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(Locations)
    install(DefaultHeaders)
    install(CallLogging)
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
    install(ContentNegotiation) {
        gson {}
        jackson { enable(SerializationFeature.INDENT_OUTPUT) }
    }
    install(Authentication) {
        jwt("jwt") {
            verifier(JwtService.verifier)
            realm = "Note Server"
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val userDoc = mongoDataService.getDocumentByEmail(USERS_COLLECTION,email)
                val user = User(
                    _id = userDoc!!.getValue("_id").toString(),
                    username = userDoc.getValue("username").toString(),
                    email = userDoc.getValue("email").toString(),
                    avatar = userDoc.get("avatar").toString(),
                    phone = userDoc.getValue("phone").toString(),
                    hashPassword = userDoc.getValue("hashPassword").toString()
                )
                user
            }
        }
    }

    routing {

        testRoutes()

        authRoutes(mongoDataService)

        postsRoutes(mongoDataService)

        usersRoutes(mongoDataService)

        multiPartsRoutes()


    }

}
