package com.george.Routes

import com.george.Models.Person.AuthRequests.LoginRequest
import com.george.Models.Person.users.User
import com.george.Models.Person.AuthRequests.RegisterRequest
import com.george.Models.Person.SimpleResponse
import com.george.data.mongo.MongoDataService
import com.george.utiles.ExtensionFunctionHelper.respondJsonResponse
import com.george.utiles.ExtensionFunctionHelper.toJson
import com.george.utiles.JwtService
import com.george.utiles.JwtService.createHash
import com.george.utiles.JwtService.generatorToken
import com.george.utiles.StatusCodesHelper.HTTP_BAD_REQUEST
import com.george.utiles.StatusCodesHelper.HTTP_CONFLICT
import com.george.utiles.StatusCodesHelper.HTTP_CREATED
import com.george.utiles.StatusCodesHelper.HTTP_OK
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.routing.*
import org.bson.types.ObjectId

object AuthRoutes {

    const val API_VERSION = "/api/v1"
    const val AUTH = "$API_VERSION/auth"
    const val REGISTER_REQUEST = "$AUTH/register"
    const val LOGIN_REQUEST = "$AUTH/login"

    const val USERS_COLLECTION = "users"

    @Location(REGISTER_REQUEST)
    class AuthRegisterRoute

    @Location(LOGIN_REQUEST)
    class AuthLoginRoute


    fun Route.authRoutes(db: MongoDataService) {



        ////////////////////////////////////////////////////////////
        ///////////////////// REGISTER_REQUEST /////////////////////
        ////////////////////////////////////////////////////////////
        post<AuthRegisterRoute> {
            val requestRequest = try {
                call.receive<RegisterRequest>()
            } catch (e: Exception) {
                call.respondJsonResponse(SimpleResponse(false, "mMissing Some Fields"), HTTP_BAD_REQUEST)
                return@post
            }

            val user = User(
                username = requestRequest.username,
                email = requestRequest.email,
                phone = requestRequest.phone,
                hashPassword = createHash(requestRequest.password)
            )
            val oidOrErrorMessage = db.saveNewDocument(USERS_COLLECTION, user.toJson())
            if (ObjectId.isValid(oidOrErrorMessage)) {
                val registerResponse = SimpleResponse(
                    success = true,
                    message = generatorToken(user)
                )
                call.respondJsonResponse(registerResponse, HTTP_CREATED)
            } else {
                val registerResponse = SimpleResponse(
                    success = false,
                    message = "$oidOrErrorMessage -- bad Request"
                )
                call.respondJsonResponse(registerResponse, HTTP_CONFLICT)
            }

        }

        ////////////////////////////////////////////////////////////
        ////////////////////// LOGIN_REQUEST ///////////////////////
        ////////////////////////////////////////////////////////////
        post<AuthLoginRoute> {
            val loginRequest = try {
                call.receive<LoginRequest>()
            } catch (e:Exception) {
                call.respondJsonResponse(SimpleResponse(false, "mMissing Some Fields"), HTTP_BAD_REQUEST)
                return@post
            }

            val doc = db.getDocumentByEmail(USERS_COLLECTION,loginRequest.email)
            println("loginResponse: $doc")

            if (doc == null) {
                call.respondJsonResponse(
                    SimpleResponse(false,"Wrong Email"),
                    HTTP_BAD_REQUEST
                )
            } else {

                val user = User(
                    username = doc.getValue("username").toString(),
                    email = doc.getValue("email").toString(),
                    phone = doc.getValue("phone").toString(),
                    hashPassword = doc.getValue("hashPassword").toString()
                )
                if (user.hashPassword == createHash(loginRequest.password)) {
                    call.respondJsonResponse(
                        SimpleResponse(true, generatorToken(user)),
                        HTTP_OK
                    )
                } else {
                    call.respondJsonResponse(
                        SimpleResponse(false, "Wrong Password"),
                        HTTP_BAD_REQUEST
                    )
                }

            }

        }


    }


}