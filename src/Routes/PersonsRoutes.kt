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

        val mappedPersons = db.allFromCollection("persons")

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


                val persons = mutableListOf<Person>()
                mappedPersons.forEach {
                    val thisPerson = Person(
                        _id = it.getValue("_id") as String,
                        name = it.getValue("name") as String,
                        email = it.getValue("email") as String,
                        phone = it.getValue("phone") as String,
                    )
                    persons.add(thisPerson)
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

                val persons = mutableListOf<Person>()
                val thisPerson = Person(
                    _id = docs?.getMapValue("_id"),
                    name = docs!!.getMapValue("name"),
                    email = docs.getMapValue("email"),
                    phone = docs.getMapValue("phone"),

                )
                persons.add(thisPerson)

                if (docs != null) {
                    call.respond(docs)
                } else {
                    call.respond(HttpNotFound)
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

        }

    }

    private fun personResponse(success:Boolean,persons:MutableList<Person>,message:String) =
        PersonResponse(success,persons,message)



}