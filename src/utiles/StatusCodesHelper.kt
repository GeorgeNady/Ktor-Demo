package com.george.utiles

import io.ktor.http.*

object StatusCodesHelper {

    val HttpOk : HttpStatusCode = HttpStatusCode.OK
    val HttpCreated : HttpStatusCode = HttpStatusCode.Created
    val HttpNotFound : HttpStatusCode = HttpStatusCode.NotFound
    val HttpBadRequest : HttpStatusCode = HttpStatusCode.BadRequest
    val HttpUnauthorized : HttpStatusCode = HttpStatusCode.Unauthorized
    val HttpForbidden : HttpStatusCode = HttpStatusCode.Forbidden

    val application_json = ContentType.Application.Json

}