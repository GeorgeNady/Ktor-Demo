package com.george.routes

import com.george.mongodb.MongoDataService
import com.george.utiles.Constants.GET_USER_REQUEST
import com.george.utiles.Constants.EDIT_USER_REQUEST
import io.ktor.auth.*
import io.ktor.routing.*

object UsersRoutes {

    fun Route.usersRoutes(db: MongoDataService) {

        authenticate("jwt") {

            get("$GET_USER_REQUEST/{id}") {

            }

            patch("$EDIT_USER_REQUEST/{id}") {

            }

        }

    }

}