package com.george.utiles

import io.ktor.http.*

object StatusCodesHelper {

    val HTTP_OK : HttpStatusCode = HttpStatusCode.OK
    val HTTP_CREATED : HttpStatusCode = HttpStatusCode.Created
    val HTTP_NOT_FOUND : HttpStatusCode = HttpStatusCode.NotFound
    val HTTP_BAD_REQUEST : HttpStatusCode = HttpStatusCode.BadRequest
    val HTTP_UNAUTHORIZED : HttpStatusCode = HttpStatusCode.Unauthorized
    val HTTP_FORBIDDEN : HttpStatusCode = HttpStatusCode.Forbidden
    val HTTP_NO_CONTENT : HttpStatusCode = HttpStatusCode.NoContent
    val HTTP_CONFLICT : HttpStatusCode = HttpStatusCode.Conflict

    val application_json = ContentType.Application.Json

}