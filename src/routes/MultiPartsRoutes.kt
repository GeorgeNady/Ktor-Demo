package com.george.routes

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.george.utiles.CloudinaryConfig.CLOUDINARY_API
import com.george.utiles.CloudinaryConfig.CLOUDINARY_PRESET
import com.george.utiles.CloudinaryConfig.cloudinary
import com.george.utiles.ConsoleHelper.printlnPurple
import com.george.utiles.Constants.DOWNLOAD_MULTI_PARTS_REQUEST
import com.george.utiles.Constants.UPLOAD_MULTI_PARTS_REQUEST
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.awaitAll
import java.io.File

object MultiPartsRoutes {

    fun Route.multiPartsRoutes() {

        authenticate("jwt") {

            ////////////////////////////////////////////////////////////
            //////////////////////////////////////////////// UPLOAD FILE
            ////////////////////////////////////////////////////////////
            post(UPLOAD_MULTI_PARTS_REQUEST) {
                // retrieve all multipart data (suspending)
                val multipart = call.receiveMultipart()
                multipart.forEachPart { part ->
                    // if part is a file (could be form item)
                    if (part is PartData.FileItem) {
                        // retrieve file name of upload
                        val name = part.originalFileName!!
                        val file = File("D:\\$name")
                        printlnPurple(file.path)

                        // use InputStream from part to save file
                        part.streamProvider().use { inputStream ->
                            // copy the stream to the file with buffering
                            file.outputStream().buffered().use {
                                // note that this is blocking
                                inputStream.copyTo(it)
                            }
                        }
                    }
                    // make sure to dispose of the part after use to prevent leaks
                    part.dispose()
                    call.respondText("success")
                }

            }


            ////////////////////////////////////////////////////////////
            ////////////////////////////////////////////// DOWNLOAD FILE
            ////////////////////////////////////////////////////////////
            get("$DOWNLOAD_MULTI_PARTS_REQUEST/{name}") {
                // get filename from request url
                val filename = call.parameters["name"]!!
                // construct reference to file
                // ideally this would use a different filename
                val file = File("D:\\$filename")
                if(file.exists()) {
                    call.respondFile(file)
                }
                else call.respond(HttpStatusCode.NotFound)
            }


        }

    }

}