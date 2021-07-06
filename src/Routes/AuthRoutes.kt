package com.george.Routes

import com.george.Models.Person.PersonResponse
import com.george.Models.Person.users.User
import com.george.Models.Person.users.UserBody
import com.george.Models.Person.users.UserResponse
import com.george.Models.User.Customer.Person
import com.george.data.mongo.AuthenticationException
import com.george.data.mongo.AuthorizationException
import com.george.data.mongo.MongoDataService
import com.george.utiles.ExtensionFunctionHelper.respondJsonResponse
import com.george.utiles.ExtensionFunctionHelper.toJson
import com.george.utiles.JwtService
import com.george.utiles.StatusCodesHelper
import com.george.utiles.StatusCodesHelper.HttpBadRequest
import com.george.utiles.StatusCodesHelper.HttpCreated
import com.george.utiles.StatusCodesHelper.HttpForbidden
import com.george.utiles.StatusCodesHelper.HttpOk
import com.george.utiles.StatusCodesHelper.HttpUnauthorized
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.bson.types.ObjectId

object AuthRoutes {

    fun Route.authRoutes(db: MongoDataService) {

        route("/api/auth") {

            install(StatusPages) {
                exception<AuthenticationException> {
                    call.respond(HttpUnauthorized)
                }
                exception<AuthorizationException> {
                    call.respond(HttpForbidden)
                }
            }

            post("/register") {
                val userBody = call.receive<UserBody>()
                // Check username and password
                // ...
                val user = User(
                    username = userBody.username,
                    email = userBody.email,
                    phone = userBody.phone,
                    token = JwtService().generatorToken(userBody),
                    password = JwtService().createHash(userBody.password)
                )
                val oidOrErrorMessage = db.saveNewDocument("users", user.toJson())
                if (ObjectId.isValid(oidOrErrorMessage)) {
                    val authRespond = authRespond(
                        success = true,
                        user = user,
                        message = "$oidOrErrorMessage -- created"
                    )
                    call.respondJsonResponse(authRespond, HttpCreated)
                } else {
                    val authRespond = authRespond(false, user, "$oidOrErrorMessage -- bad Request")
                    call.respondJsonResponse(authRespond, HttpBadRequest)
                }
            }

            post("/login") {

            }


        }

    }

    private fun authRespond(success:Boolean, user:User, message:String) =
        UserResponse(success,user,message)


}