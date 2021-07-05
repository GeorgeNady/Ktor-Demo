package com.george

import com.george.Application.UserRoutes.UserRoutes.usersRoutes
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.server.netty.EngineMain
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.request.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {


        }
    }

    routing {

        usersRoutes()

        /*val users = generateRandomUsers()

        get("/users/{id}") {
            val id =
                call.parameters["id"] ?: return@get call.respondText("pad request", status = HttpStatusCode.BadRequest)
            val user = users.find {
                it.uid == id
            } ?: return@get call.respondText("Not Found")
            call.respond(
                status = HttpStatusCode.OK,
                message = Gson().toJson(user)
            )
        }

        post {
            val user = call.receive<User>()
            users.add(user)
            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
        }

        delete("/users/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respondText(
                "pad request",
                status = HttpStatusCode.BadRequest
            )
            val user = users.find {
                it.uid == id
            } ?: return@delete call.respondText("Not Found", status = HttpStatusCode.NotFound)
            if (users.removeIf { it == user }) {
                call.respondText("Deleted ${user.name}")
            }
        }*/

    }

}

