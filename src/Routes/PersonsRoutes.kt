package com.george.Routes

import com.george.data.mongo.AuthenticationException
import com.george.data.mongo.AuthorizationException
import com.george.data.mongo.MongoDataService
import com.george.utiles.ExtensionFunctionHelper.getMapValue
import com.george.utiles.ExtensionFunctionHelper.respondJsonResponse
import com.george.utiles.ExtensionFunctionHelper.toJson
import com.george.utiles.StatusCodesHelper.HTTP_BAD_REQUEST
import com.george.utiles.StatusCodesHelper.HTTP_CREATED
import com.george.utiles.StatusCodesHelper.HTTP_FORBIDDEN
import com.george.utiles.StatusCodesHelper.HTTP_NO_CONTENT
import com.george.utiles.StatusCodesHelper.HTTP_NOT_FOUND
import com.george.utiles.StatusCodesHelper.HTTP_OK
import com.george.utiles.StatusCodesHelper.HTTP_UNAUTHORIZED
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.bson.types.ObjectId

//object PersonsRoutes {
//
//    fun Route.personsRoutes(db: MongoDataService) {
//
//
//
//        route("/persons") {
//
//            install(StatusPages) {
//                exception<AuthenticationException> {
//                    call.respond(HTTP_UNAUTHORIZED)
//                }
//                exception<AuthorizationException> {
//                    call.respond(HTTP_FORBIDDEN)
//                }
//
//            }
//
//            get {
//
//                val mappedPersons = db.allFromCollection("persons")
//
//                val persons = mutableListOf<Person>()
//
//                mappedPersons.forEach {
//                    persons.add(
//                        Person(
//                            _id = it.getMapValue("_id"),
//                            name = it.getMapValue("name"),
//                            email = it.getMapValue("email"),
//                            phone = it.getMapValue("phone"),
//                        )
//                    )
//                }
//
//                val personResponse = PersonResponse(
//                    success = true,
//                    persons = persons,
//                    message = "all persons result : ${mappedPersons.size}"
//                )
//                call.respondJsonResponse(personResponse,HTTP_OK)
//
//                /*call.respondText(
//                    text = personResponse.toJson(),
//                    status = HttpOk,
//                    contentType = application_json
//                )*/
//
//            }
//
//            get("/{id}") {
//                val id: String? = call.parameters["id"]
//                val docs = db.getDocumentById("persons", id)
//
//
//                if (docs!!.isNotEmpty()) {
//                    val person = Person(
//                        _id = docs.getMapValue("_id"),
//                        name = docs.getMapValue("name"),
//                        email = docs.getMapValue("email"),
//                        phone = docs.getMapValue("phone"),
//                    )
//                    val personResponses = personResponse(true, mutableListOf(person), "")
//                    call.respondJsonResponse(personResponses, HTTP_CREATED)
//                } else {
//                    val personResponses = personResponse(false, mutableListOf(), "this id not found 404")
//                    call.respondJsonResponse(personResponses, HTTP_NOT_FOUND)
//                }
//            }
//
//            post {
//
//                val person = call.receive<Person>()
//
//                val oidOrErrorMessage = db.saveNewDocument("persons", person.toJson())
//
//                if (ObjectId.isValid(oidOrErrorMessage)) {
//
//                    val personResponse = personResponse(
//                        true,
//                        mutableListOf(person),
//                        "$oidOrErrorMessage -- created"
//                    )
//
//                    call.respondJsonResponse(personResponse,HTTP_CREATED)
//
//                } else {
//
//                    val personResponse = personResponse(false, mutableListOf(),"$oidOrErrorMessage -- bad Request")
//
//                    call.respondJsonResponse(personResponse,HTTP_BAD_REQUEST)
//                }
//            }
//
//            patch("/{id}") {
//                val id: String? = call.parameters["id"]
//                val documentAsString = call.receiveText()
//                val (updatedRecords, message) =
//                    db.updateExistingDocument("persons", id, documentAsString)
//                when (updatedRecords) {
//                    -1 -> call.respond(HTTP_BAD_REQUEST, message)
//                    0 -> call.respond(HTTP_NOT_FOUND, message)
//                    1 -> call.respond(HTTP_NO_CONTENT)
//                }
//            }
//
//            delete("/{id}") {
//                val id: String? = call.parameters["id"]
//                val (updatedRecords, message) =
//                    db.deleteDocument("persons", id)
//                when (updatedRecords) {
//                    0 -> call.respond(HTTP_NOT_FOUND, message)
//                    1 -> call.respond(HTTP_NO_CONTENT)
//                }
//            }
//
//        }
//
//    }
//
//    private fun personResponse(success:Boolean,persons:MutableList<Person>,message:String) =
//        PersonResponse(success,persons,message)
//
//
//}