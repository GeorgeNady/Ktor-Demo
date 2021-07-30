package com.george.routes

import com.george.models.posts.response.PostResponse
import com.george.models.users.User
import com.george.mongodb.MongoDataService
import com.george.models.posts.DbPost
import com.george.models.posts.request.PostRequest
import com.george.models.posts.response.PostsResponse
import com.george.models.posts.response.ResPost
import com.george.models.posts.response.ResUser
import com.george.utiles.ConsoleHelper.printlnBlue
import com.george.utiles.ConsoleHelper.printlnGreen
import com.george.utiles.ConsoleHelper.printlnYellow
import com.george.utiles.Constants.POSTS_COLLECTION
import com.george.utiles.Constants.USERS_COLLECTION
import com.george.utiles.ExtensionFunctionHelper.respondJsonResponse
import com.george.utiles.ExtensionFunctionHelper.toJson
import com.george.utiles.StatusCodesHelper.HTTP_BAD_REQUEST
import com.george.utiles.StatusCodesHelper.HTTP_CONFLICT
import com.george.utiles.StatusCodesHelper.HTTP_OK
import com.george.utiles.TypeConverterUtils.toDataClass
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import java.text.SimpleDateFormat
import java.util.*

object PostRoutes {

    fun Route.postsRoutes(db: MongoDataService) {

        authenticate("jwt") {

            ////////////////////////////////////////////////////////////
            //////////////////////////////////////////////// CREATE POST
            ////////////////////////////////////////////////////////////
            post<ApplicationLocations.PostCreateRoute> {

                // get content
                // get user email from jwt
                // get user from db with his email
                // get current date
                // create post data
                // save post in db

                // return response to the user

                val postRequest = try {
                    call.receive<PostRequest>()
                } catch (e: Exception) {
                    badRequestHandler("Missing Fields")
                    return@post
                }


                try {

                    val email = call.principal<User>()!!.email.also { println(it) }

                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss").also { println(it) }

                    val currentDate = sdf.format(Date()).also { println(it) }

                    val dbPost = DbPost(
                        user_email = email,
                        content = postRequest.content,
                        likes_count = 0,
                        likes_users_emails = listOf(),
                        dislike_count = 0,
                        dislike_users_emails = listOf(),
                        created_at = currentDate,
                        modified_at = currentDate
                    ).also { println("{it.toJson()}") }

                    val oidOrErrorMessage = db.saveNewDocument(POSTS_COLLECTION, dbPost.toJson())

                    // GET USER
                    val userDoc = db.getDocumentByEmail(USERS_COLLECTION, dbPost.user_email)!!
                    val resUser = ResUser(
                        id = userDoc.getValue("_id") as String,
                        username = userDoc.getValue("username") as String,
                        email = userDoc.getValue("email") as String,
                        phone = userDoc.getValue("phone") as String,
                    ).also { printlnBlue("$it") }

                    okHttpHandler(
                        ResPost(
                            id = oidOrErrorMessage,
                            user = resUser,
                            content = dbPost.content,
                            likes_count = dbPost.likes_count,
                            likes_users = listOf(),
                            dislike_count = dbPost.dislike_count,
                            dislike_users = listOf(),
                            created_at = dbPost.created_at,
                            modified_at = dbPost.modified_at
                        ), "Post Added Successfully"
                    )

                } catch (e: Exception) {
                    conflictRequestHandler("Some Problems Occurred!: $e")
                }

            }

            ////////////////////////////////////////////////////////////
            ////////////////////////////////////////////// GET ALL POSTS
            ////////////////////////////////////////////////////////////
            get<ApplicationLocations.AllPostsGetterRoute> {

                val posts = mutableListOf<ResPost>()
                val page = call.parameters["page"]?.toInt().also { printlnGreen("$it") } ?: 1

                try {

                    val totalResult = db.countFromCollection(POSTS_COLLECTION)
                    val postsDoc = db.paginationFromCollection(POSTS_COLLECTION,10,page)

                    postsDoc.forEach { doc ->
                        val post = doc.toDataClass<DbPost>().also { printlnBlue("$it") }
                        val postId = doc.getValue("_id").toString()
                        val resUser = getResUser(post.user_email, db).also { printlnYellow(it.toJson()) }
                        val likeUsers = getResUsers(post.likes_users_emails, db).also { printlnYellow("$it") }
                        val dislikeUsers = getResUsers(post.dislike_users_emails, db).also { printlnYellow("$it") }
                        posts.add(ResPost(
                                id = postId,
                                user = resUser,
                                content = post.content,
                                likes_count = post.likes_count,
                                likes_users = likeUsers,
                                dislike_count = post.dislike_count,
                                dislike_users = dislikeUsers,
                                created_at = post.created_at,
                                modified_at = post.modified_at
                            ))
                    }

                    okHttpHandler(posts, "success",page,totalResult)

                } catch (e: Exception) {

                    conflictRequestHandler("Some Problems Occurred!: $e")

                }

            }


        }

    }

    private fun getResUsers(emails: List<String>, db: MongoDataService): List<ResUser> {
        val usersList = mutableListOf<ResUser>()
        for (email in emails) {
            val userDoc = db.getDocumentByEmail(USERS_COLLECTION, email)!!
            val id = userDoc.getValue("_id") as String
            val user = userDoc.toDataClass<User>()
            val resUser = ResUser(
                id = id,
                username = user.username,
                email = user.email,
                phone = user.phone
            )
            usersList.add(resUser)
        }
        return usersList
    }

    private fun getResUser(email: String, db: MongoDataService): ResUser {
        val userDoc = db.getDocumentByEmail(USERS_COLLECTION, email)!!
        val id = userDoc.getValue("_id") as String
        val user = userDoc.toDataClass<User>()
        val resUser = ResUser(
            id = id,
            username = user.username,
            email = user.email,
            phone = user.phone
        )
        return resUser
    }

    // for single post
    private suspend fun PipelineContext<Unit, ApplicationCall>.okHttpHandler(data: ResPost, message: String) {
        call.respondJsonResponse(PostResponse(true, data, message), HTTP_OK)
    }

    // for list of posts
    private suspend fun PipelineContext<Unit, ApplicationCall>.okHttpHandler(data: List<ResPost>, message: String,page:Int, totalResult:Int) {
        call.respondJsonResponse(PostsResponse(true, data,page,totalResult,message), HTTP_OK)
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.badRequestHandler(message: String) {
        call.respondJsonResponse(PostResponse(false, null, message), HTTP_BAD_REQUEST)
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.conflictRequestHandler(message: String) {
        call.respondJsonResponse(PostResponse(false, null, message), HTTP_CONFLICT)
    }

}