package com.george.Application.UserRoutes

import com.george.Models.User.User
import com.george.data.mongo.MongoDataService
import com.george.utiles.Constants.generateRandomUsers
import com.george.utiles.ExtensionFunctionHelper.toJson
import com.george.utiles.StatusCodesHelper.HttpBadRequest
import com.george.utiles.StatusCodesHelper.HttpCreated
import com.george.utiles.StatusCodesHelper.HttpNotFound
import com.george.utiles.StatusCodesHelper.HttpOk
import com.george.utiles.StatusCodesHelper.application_json
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

object UserRoutes {

    private val users = generateRandomUsers()

    fun Route.usersRoutes() {

        route("/users") {

            get {
                call.respondText(
                    text = users.toJson(),
                    status = HttpOk,
                    contentType = application_json
                )
            }

            get("/{id}") {
                val id =
                    call.parameters["id"] ?: return@get call.respondText("pad request", status = HttpStatusCode.BadRequest)
                val user = users.find {
                    it.uid == id.toInt()
                } ?: return@get call.respondText("Not Found")
                call.respondText(
                    text = user.toJson(),
                    status = HttpOk,
                    contentType = application_json
                )
            }

            post {
                val user = call.receive<User>()
                users.add(user)
                call.respondText(
                    text = "Customer stored correctly\n${user.toJson()}",
                    status = HttpCreated,
                    contentType = application_json
                )
            }

            delete("/{id}") {
                val id = call.parameters["id"] ?: return@delete call.respondText(
                    text = "pad request",
                    status = HttpBadRequest,
                    contentType = application_json
                )
                val user = users.find {
                    it.uid == id.toInt()
                } ?: return@delete call.respondText(
                    text = "Not Found",
                    status = HttpNotFound,
                    contentType = application_json
                )
                if (users.removeIf { it == user }) {
                    call.respondText(
                        text = "Deleted ${user.name}, ${user.email}\n${users.toJson()}",
                        status = HttpOk,
                        contentType = application_json
                    )
                }
            }

        }

    }

}