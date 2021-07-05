package com.george.Application.UserRoutes

import com.george.utiles.Constants
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

object UserRoutes {

    fun Route.usersRoutes() {

        route("/users") {

            get {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = Gson().toJson(Constants.generateRandomUsers())
                )
            }

        }

    }

}