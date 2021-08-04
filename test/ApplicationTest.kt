package com.george

//import e2e.WithTestServer
//import e2e.defaultServer
//import e2e.readString
//import e2e.runGradleAppWaiting
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.junit.Assert.assertEquals
import org.junit.Test

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import kotlin.test.*
import io.ktor.server.testing.*

class ApplicationTest {

    /*override val server = defaultServer {
        routing {
            post("/upload") {
                val data = call.receiveMultipart()
                val descriptionPart = data.readPart() as PartData.FormItem
                val filePart = data.readPart() as PartData.FileItem

                call.respondText {
                    "${descriptionPart.name}:${descriptionPart.value}," +
                            "${filePart.name}:[${filePart.contentType}][${filePart.contentDisposition}]"
                }
            }
        }
    }*/

    /*@Test
    fun clientSendsMultipartData() {
        val output = runGradleAppWaiting().inputStream.readString()
        assertEquals(
            output,
            "description:Ktor logo,image:[image/png][form-data; name=image; filename=ktor_logo.png]\n"
        )
    }*/

    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }
}
