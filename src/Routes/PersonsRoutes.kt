package com.george.Routes

import com.george.Models.Person.PersonResponse
import com.george.Models.User.Customer.Person
import com.george.data.mongo.AuthenticationException
import com.george.data.mongo.AuthorizationException
import com.george.data.mongo.MongoDataService
import com.george.utiles.ExtensionFunctionHelper.getMapValue
import com.george.utiles.ExtensionFunctionHelper.respondJsonResponse
import com.george.utiles.ExtensionFunctionHelper.toJson
import com.george.utiles.StatusCodesHelper.HttpBadRequest
import com.george.utiles.StatusCodesHelper.HttpCreated
import com.george.utiles.StatusCodesHelper.HttpForbidden
import com.george.utiles.StatusCodesHelper.HttpNoContent
import com.george.utiles.StatusCodesHelper.HttpNotFound
import com.george.utiles.StatusCodesHelper.HttpOk
import com.george.utiles.StatusCodesHelper.HttpUnauthorized
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.bson.types.ObjectId

object PersonsRoutes {

    fun Route.personsRoutes(db: MongoDataService) {



        route("/persons") {

            install(StatusPages) {
                exception<AuthenticationException> {
                    call.respond(HttpUnauthorized)
                }
                exception<AuthorizationException> {
                    call.respond(HttpForbidden)
                }

            }

            get {

                val mappedPersons = db.allFromCollection("persons")

                val persons = mutableListOf<Person>()

                mappedPersons.forEach {
                    persons.add(
                        Person(
                            _id = it.getMapValue("_id"),
                            name = it.getMapValue("name"),
                            email = it.getMapValue("email"),
                            phone = it.getMapValue("phone"),
                        )
                    )
                }

                val personResponse = PersonResponse(
                    success = true,
                    persons = persons,
                    message = "all persons result : ${mappedPersons.size}"
                )
                call.respondJsonResponse(personResponse,HttpOk)

                /*call.respondText(
                    text = personResponse.toJson(),
                    status = HttpOk,
                    contentType = application_json
                )*/

            }

            get("/{id}") {
                val id: String? = call.parameters["id"]
                val docs = db.getDocumentById("persons", id)


                if (docs!!.isNotEmpty()) {
                    val person = Person(
                        _id = docs.getMapValue("_id"),
                        name = docs.getMapValue("name"),
                        email = docs.getMapValue("email"),
                        phone = docs.getMapValue("phone"),
                    )
                    val personResponses = personResponse(true, mutableListOf(person), "")
                    call.respondJsonResponse(personResponses, HttpCreated)
                } else {
                    val personResponses = personResponse(false, mutableListOf(), "this id not found 404")
                    call.respondJsonResponse(personResponses, HttpNotFound)
                }
            }

            post {

                val person = call.receive<Person>()

                val oidOrErrorMessage = db.saveNewDocument("persons", person.toJson())

                if (ObjectId.isValid(oidOrErrorMessage)) {

                    val personResponse = personResponse(
                        true,
                        mutableListOf(person),
                        "$oidOrErrorMessage -- created"
                    )

                    call.respondJsonResponse(personResponse,HttpCreated)

                } else {

                    val personResponse = personResponse(false, mutableListOf(),"$oidOrErrorMessage -- bad Request")

                    call.respondJsonResponse(personResponse,HttpBadRequest)
                }
            }

            patch("/{id}") {
                val id: String? = call.parameters["id"]
                val documentAsString = call.receiveText()
                val (updatedRecords, message) =
                    db.updateExistingDocument("persons", id, documentAsString)
                when (updatedRecords) {
                    -1 -> call.respond(HttpBadRequest, message)
                    0 -> call.respond(HttpNotFound, message)
                    1 -> call.respond(HttpNoContent)
                }
            }

            delete("/{id}") {
                val id: String? = call.parameters["id"]
                val (updatedRecords, message) =
                    db.deleteDocument("persons", id)
                when (updatedRecords) {
                    0 -> call.respond(HttpNotFound, message)
                    1 -> call.respond(HttpNoContent)
                }
            }

        }

    }

    private fun personResponse(success:Boolean,persons:MutableList<Person>,message:String) =
        PersonResponse(success,persons,message)


}