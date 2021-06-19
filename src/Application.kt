package com.george

import com.george.Models.Location
import com.george.Models.User
import com.google.gson.Gson
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

        var users = generateRandomUsers()

        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/users") {
            call.respond(
                status = HttpStatusCode.OK,
                message = Gson().toJson(users)
            )
        }

        get("/users/{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText("pad request",status = HttpStatusCode.BadRequest)
            val user = users.find {
                it.uid == id
            } ?: return@get call.respondText("Not Found",status = HttpStatusCode.NotFound)
            call.respond(
                status = HttpStatusCode.OK,
                message = Gson().toJson(user)
            )
        }

        delete("/users/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respondText("pad request",status = HttpStatusCode.BadRequest)
            val user = users.find {
                it.uid == id
            } ?: return@delete call.respondText("Not Found",status = HttpStatusCode.NotFound)
            if (users.removeIf { it == user}) {
                call.respondText ("Deleted ${user.name}")
            }
        }

    }
}

fun generateRandomUsers(): MutableList<User> {
    val users = mutableListOf<User>()
    for (i in 1..11) {
        users.add(
            User(
                "$i",
                "user num $i",
                "user_$i@gamil.com",
                "010-0000-00$i",
                Location(
                    lat = 21.05248495,
                    long = 32.89951685
                )
            )
        )
    }
    return users
}

