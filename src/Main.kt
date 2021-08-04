package com.george

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.io.File

import com.fasterxml.jackson.databind.SerializationFeature
import com.george.models.users.User
import com.george.routes.AuthRoutes.authRoutes
import com.george.mongodb.MongoDataService.Companion.mongoDataService
import com.george.routes.PostRoutes.postsRoutes
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
                    phone = userDoc.getValue("phone").toString(),
                    hashPassword = userDoc.getValue("hashPassword").toString()
                )
                user
            }
        }
    }

    // Upload a file
    /*runBlocking {
        val client = HttpClient(CIO)

        val response: HttpResponse = client.submitFormWithBinaryData(
            url = "http://localhost:6060/upload",
            formData = formData {
                append("description", "Ktor logo")
                append("image", File("ktor_logo.png").readBytes(), Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=ktor_logo.png")
                })
            }
        ) {
            onUpload { bytesSentTotal, contentLength ->
                println("Sent $bytesSentTotal bytes from $contentLength")
            }
        }

        println(response.readText())
    }*/

    routing {

        get("/api/v1/test") {
            call.respondJsonResponse(mapOf("message" to "Hello World!"), HTTP_OK)
        }

        authRoutes(mongoDataService)

        postsRoutes(mongoDataService)


    }

}
