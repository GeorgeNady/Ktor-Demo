package com.george.routes

import com.george.models.posts.response.PostResponse
import com.george.models.users.User
import com.george.mongodb.MongoDataService
import com.george.models.posts.DbPost
import com.george.models.posts.request.PostRequest
import com.george.models.posts.response.PostsResponse
import com.george.models.posts.response.ResPost
import com.george.models.posts.response.ResUser
import com.george.utiles.ConsoleHelper.printlnDebug
import com.george.utiles.ConsoleHelper.printlnError
import com.george.utiles.ConsoleHelper.printlnSuccess
import com.george.utiles.ConsoleHelper.printlnInfo
import com.george.utiles.Constants.DELETE_POST_REQUEST
import com.george.utiles.Constants.POSTS_COLLECTION
import com.george.utiles.Constants.USERS_COLLECTION
import com.george.utiles.DateHelper.getTimeAgo
import com.george.utiles.ExtensionFunctionHelper.respondJsonResponse
import com.george.utiles.ExtensionFunctionHelper.toJson
import com.george.utiles.StatusCodesHelper.HTTP_BAD_REQUEST
import com.george.utiles.StatusCodesHelper.HTTP_CONFLICT
import com.george.utiles.StatusCodesHelper.HTTP_OK
import com.george.utiles.TypeConverterUtils.toDataClass
import com.github.marlonlom.utilities.timeago.TimeAgo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import java.util.*

object PostRoutes {

    fun Route.postsRoutes(db: MongoDataService) {

        authenticate("jwt") {

            ////////////////////////////////////////////////////////////
            //////////////////////////////////////////////// CREATE POST
            ////////////////////////////////////////////////////////////
            post<ApplicationLocations.PostCreateRoute> {

                val postRequest = try {
                    call.receive<PostRequest>()
                } catch (e: Exception) {
                    badRequestHandler("Missing Fields")
                    return@post
                }


                try {

                    val email = call.principal<User>()!!.email.also { println(it) }

                    val currentDate = Calendar.getInstance().timeInMillis.toString()

                    val dbPost = DbPost(
                        user_email = email,
                        content = postRequest.content,
                        my_react = "",
                        likes_count = 0,
                        likes_users_emails = listOf(),
                        dislike_count = 0,
                        dislike_users_emails = listOf(),
                        created_at = currentDate,
                        modified_at = currentDate
                    ).also { println(it.toJson()) }

                    val oidOrErrorMessage = db.saveNewDocument(POSTS_COLLECTION, dbPost.toJson())

                    // GET USER
                    val userDoc = db.getDocumentByEmail(USERS_COLLECTION, dbPost.user_email)!!
                    val user = userDoc.toDataClass<User>().also { printlnDebug("$it") }
                    val resUser = ResUser(user._id!!, user.username, user.email, user.phone)

                    val createdAt = TimeAgo.using(dbPost.created_at.toLong()).also { printlnError(it) }
                    val modifiedAt = TimeAgo.using(dbPost.modified_at.toLong()).also { printlnError(it) }

                    okHttpHandler(
                        ResPost(
                            id = oidOrErrorMessage,
                            user = resUser,
                            content = dbPost.content,
                            my_react = "",
                            likes_count = dbPost.likes_count,
                            likes_users = listOf(),
                            dislike_count = dbPost.dislike_count,
                            dislike_users = listOf(),
                            created_at = createdAt,
                            modified_at = modifiedAt
                        ), "Post Added Successfully"
                    )

                } catch (e: Exception) {
                    printlnError("$e")
                    conflictRequestHandler("Some Problems Occurred!: $e")
                }

            }

            ////////////////////////////////////////////////////////////
            ////////////////////////////////////////////// GET ALL POSTS
            ////////////////////////////////////////////////////////////
            get<ApplicationLocations.AllPostsGetterRoute> {

                val posts = mutableListOf<ResPost>()
                val page = call.parameters["page"]?.toInt().also { printlnSuccess("$it") } ?: 1

                try {

                    val totalResult = db.countAllDocsFromCollection(POSTS_COLLECTION)
                    val postsDoc = db.getAllDocFromCollectionPaginated(POSTS_COLLECTION, 10, page)

                    postsDoc.forEach { doc ->
                        val post = doc.toDataClass<DbPost>().also { printlnDebug("$it") }
                        val resUser = getResUser(post.user_email, db).also { printlnInfo(it.toJson()) }
                        val likeUsers = getResUsers(post.likes_users_emails, db).also { printlnInfo("$it") }
                        val dislikeUsers = getResUsers(post.dislike_users_emails, db).also { printlnInfo("$it") }
                        posts.add(
                            ResPost(
                                id = post._id!!,
                                user = resUser,
                                content = post.content,
                                my_react = post.my_react,
                                likes_count = post.likes_count,
                                likes_users = likeUsers,
                                dislike_count = post.dislike_count,
                                dislike_users = dislikeUsers,
                                created_at = getTimeAgo(post.created_at.toLong()),
                                modified_at = getTimeAgo(post.modified_at.toLong())
                            ))
                    }

                    okHttpHandler(posts, "success", page, totalResult)

                } catch (e: Exception) {

                    conflictRequestHandler("Some Problems Occurred!: $e")

                }

            }

            ////////////////////////////////////////////////////////////
            /////////////////////////////////////////////// GET MY POSTS
            ////////////////////////////////////////////////////////////
            get<ApplicationLocations.MyPostsGetterRoute> {

                val posts = mutableListOf<ResPost>()
                val page = call.parameters["page"]?.toInt().also { printlnSuccess("$it") } ?: 1

                try {

                    val email = call.principal<User>()!!.email.also { println(it) }

                    val totalResult = db.countMyDocsFromCollection(email)
                    val postsDoc = db.getMyDocFromCollectionPaginated(10, page, email)

                    postsDoc.forEach { doc ->
                        val post = doc.toDataClass<DbPost>().also { printlnDebug("$it") }
                        val resUser = getResUser(post.user_email, db).also { printlnInfo(it.toJson()) }
                        val likeUsers = getResUsers(post.likes_users_emails, db).also { printlnInfo("$it") }
                        val dislikeUsers = getResUsers(post.dislike_users_emails, db).also { printlnInfo("$it") }
                        posts.add(
                            ResPost(
                                id = post._id!!,
                                user = resUser,
                                content = post.content,
                                my_react = post.my_react,
                                likes_count = post.likes_count,
                                likes_users = likeUsers,
                                dislike_count = post.dislike_count,
                                dislike_users = dislikeUsers,
                                created_at = getTimeAgo(post.created_at.toLong())!!,
                                modified_at = getTimeAgo(post.modified_at.toLong())!!
                            )
                        )
                    }

                    okHttpHandler(posts, "success", page, totalResult)

                } catch (e: Exception) {

                    conflictRequestHandler("Some Problems Occurred!: $e")

                }

            }

            ////////////////////////////////////////////////////////////
            //////////////////////////////////////////////// DELETE POST
            ////////////////////////////////////////////////////////////
            delete("$DELETE_POST_REQUEST/{post_id}") {

                try {

                    val email = call.principal<User>()?.email
                    val postId = getRequestPath(DELETE_POST_REQUEST)

                    val doc = db.getDocumentById(POSTS_COLLECTION, postId).also { printlnDebug("$it") }
                    val dbPost = doc?.toDataClass<DbPost>()!!.also { printlnDebug("$it") }

                    if (email == dbPost.user_email && dbPost._id != null) {
                        db.deleteDocument(POSTS_COLLECTION, postId).also {
                            printlnDebug("$it")
                            if (it.second == "success") {

                                val user = getResUser(email, db)
                                val likesUsers = getResUsers(dbPost.likes_users_emails, db)
                                val dislikeUsers = getResUsers(dbPost.dislike_users_emails, db)

                                val resPost = ResPost(
                                    id = dbPost._id,
                                    user = user,
                                    my_react = dbPost.my_react,
                                    content = dbPost.content,
                                    likes_count = dbPost.likes_count,
                                    likes_users = likesUsers,
                                    dislike_count = dbPost.dislike_count,
                                    dislike_users = dislikeUsers,
                                    created_at = dbPost.created_at,
                                    modified_at = dbPost.modified_at
                                )
                                okHttpHandler(resPost, "deleted successfully")
                            }
                        }
                    }

                } catch (e: Exception) {

                    conflictRequestHandler("Some Problems Occurred!: $e")

                }

            }

        }

    }

    private fun PipelineContext<Unit, ApplicationCall>.getRequestPath(firstPart: String): String {
        val path = call.request.path()
        return path.slice((firstPart.length + 1) until path.length)
    }

    private fun getResUsers(emails: List<String>, db: MongoDataService): List<ResUser> {
        val usersList = mutableListOf<ResUser>()
        for (email in emails) {
            val userDoc = db.getDocumentByEmail(USERS_COLLECTION, email)!!
            val user = userDoc.toDataClass<User>()
            val resUser = ResUser(
                id = user._id!!,
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
        val user = userDoc.toDataClass<User>()
        return ResUser(
            id = user._id!!,
            username = user.username,
            email = user.email,
            phone = user.phone
        )
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