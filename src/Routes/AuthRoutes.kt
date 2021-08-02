package com.george.routes

import com.george.models.authRequests.request.LoginRequest
import com.george.models.users.User
import com.george.models.authRequests.request.RegisterRequest
import com.george.models.authRequests.response.AuthResponse
import com.george.mongodb.MongoDataService
import com.george.utiles.Constants.USERS_COLLECTION
import com.george.utiles.ExtensionFunctionHelper.respondJsonResponse
import com.george.utiles.ExtensionFunctionHelper.toJson
import com.george.utiles.ExtensionFunctionHelper.userDataExtractor
import com.george.utiles.JwtService.createHash
import com.george.utiles.JwtService.generatorToken
import com.george.utiles.StatusCodesHelper.HTTP_BAD_REQUEST
import com.george.utiles.StatusCodesHelper.HTTP_CONFLICT
import com.george.utiles.StatusCodesHelper.HTTP_OK
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import org.bson.types.ObjectId

object AuthRoutes {

    fun Route.authRoutes(db: MongoDataService) {

        ////////////////////////////////////////////////////////////
        ///////////////////// REGISTER_REQUEST /////////////////////
        ////////////////////////////////////////////////////////////
        post<ApplicationLocations.AuthRegisterRoute> {
            val requestRequest = try {
                call.receive<RegisterRequest>()
            } catch (e: Exception) {
               badRequestHandler("Missing Some Fields")
                return@post
            }

            val doc = User(
                username = requestRequest.username,
                email = requestRequest.email,
                phone = requestRequest.phone,
                hashPassword = createHash(requestRequest.password)
            )
            val oidOrErrorMessage = db.saveNewDocument(USERS_COLLECTION, doc.toJson())
            val user = User(
                _id = oidOrErrorMessage,
                username = requestRequest.username,
                email = requestRequest.email,
                phone = requestRequest.phone,
                hashPassword = createHash(requestRequest.password)
            )
            if (ObjectId.isValid(oidOrErrorMessage)) {
                okHttpHandler(user,generatorToken(user))
            } else {
                conflictRequestHandler("$oidOrErrorMessage -- bad Request")
            }

        }

        ////////////////////////////////////////////////////////////
        ////////////////////// LOGIN_REQUEST ///////////////////////
        ////////////////////////////////////////////////////////////
        post<ApplicationLocations.AuthLoginRoute> {
            val loginRequest = try {
                call.receive<LoginRequest>()
            } catch (e: Exception) {
                badRequestHandler("Missing Some Fields")
                return@post
            }

            val doc = db.getDocumentByEmail(USERS_COLLECTION, loginRequest.email)
            println("loginResponse: $doc")

            if (doc == null) {
                badRequestHandler("Wrong Email")
            } else {

                val user = userDataExtractor(doc)
                if (user.hashPassword == createHash(loginRequest.password)) {
                    okHttpHandler(user,generatorToken(user))
                } else {
                    badRequestHandler("Wrong Password")
                }

            }

        }


    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.okHttpHandler(data:Any, message:String) {
        call.respondJsonResponse(AuthResponse(true, data as User, "Bearer $message"), HTTP_OK)
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.badRequestHandler(message:String) {
        call.respondJsonResponse(AuthResponse(false, null, message), HTTP_BAD_REQUEST)
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.conflictRequestHandler(message:String) {
        call.respondJsonResponse(AuthResponse(false, null, message), HTTP_CONFLICT)
    }


}