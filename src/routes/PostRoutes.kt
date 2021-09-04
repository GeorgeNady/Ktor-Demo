package com.george.routes

import com.george.models.posts.response.PostResponse
import com.george.models.users.User
import com.george.mongodb.MongoDataService
import com.george.models.posts.DbPost
import com.george.models.posts.request.PostRequest
import com.george.models.posts.response.PostsResponse
import com.george.models.posts.response.ResPost
import com.george.models.posts.response.ResUser
import com.george.models.react.ReactRequest
import com.george.utiles.ApplicationLocations.PostCreateRoute
import com.george.utiles.ApplicationLocations.AllPostsGetterRoute
import com.george.utiles.ApplicationLocations.MyPostsGetterRoute
import com.george.utiles.ConsoleHelper.printlnDebug
import com.george.utiles.ConsoleHelper.printlnError
import com.george.utiles.ConsoleHelper.printlnSuccess
import com.george.utiles.ConsoleHelper.printlnInfo
import com.george.utiles.Constants.DELETE_POST_REQUEST
import com.george.utiles.Constants.EDIT_POST_REQUEST
import com.george.utiles.Constants.POSTS_COLLECTION
import com.george.utiles.Constants.REACT_POST_REQUEST
import com.george.utiles.Constants.USERS_COLLECTION
import com.george.utiles.DateHelper.getTimeAgo
import com.george.utiles.ExtensionFunctionHelper.respondJsonResponse
import com.george.utiles.ExtensionFunctionHelper.toJson
import com.george.utiles.PusherConfiguration.pusher
import com.george.utiles.StatusCodesHelper.HTTP_BAD_REQUEST
import com.george.utiles.StatusCodesHelper.HTTP_CONFLICT
import com.george.utiles.StatusCodesHelper.HTTP_NOT_FOUND
import com.george.utiles.StatusCodesHelper.HTTP_OK
import com.george.utiles.StatusCodesHelper.HTTP_UNAUTHORIZED
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
            post<PostCreateRoute> {

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
                        likes_count = 0,
                        likes_users_emails = mutableListOf(),
                        dislike_count = 0,
                        dislike_users_emails = mutableListOf(),
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

                    val resPost = ResPost(
                        _id = oidOrErrorMessage,
                        user = resUser,
                        content = dbPost.content,
                        my_react = "",
                        likes_count = dbPost.likes_count,
                        likes_users = listOf(),
                        dislike_count = dbPost.dislike_count,
                        dislike_users = listOf(),
                        created_at = createdAt,
                        modified_at = modifiedAt
                    )

                    okHttpHandler(resPost, "Post Added Successfully")
                    pusher.trigger("my-channel","my-event", resPost)


                } catch (e: Exception) {
                    printlnError("$e")
                    conflictRequestHandler("Some Problems Occurred!: $e")
                }

            }

            ////////////////////////////////////////////////////////////
            ////////////////////////////////////////////// GET ALL POSTS
            ////////////////////////////////////////////////////////////
            get<AllPostsGetterRoute> {

                val posts = mutableListOf<ResPost>()
                val page = call.parameters["page"]?.toInt().also { printlnSuccess("$it") } ?: 1

                try {

                    val email = call.principal<User>()!!.email
                    val totalResult = db.countAllDocsFromCollection(POSTS_COLLECTION)
                    val postsDoc = db.getAllDocFromCollectionPaginated(POSTS_COLLECTION, 10, page)

                    postsDoc.forEach { doc ->
                        val post = doc.toDataClass<DbPost>().also { printlnDebug("$it") }
                        val resUser = getResUser(post.user_email, db).also { printlnInfo(it.toJson()) }
                        val likeUsers = getResUsers(post.likes_users_emails, db).also { printlnInfo("$it") }
                        val dislikeUsers = getResUsers(post.dislike_users_emails, db).also { printlnInfo("$it") }
                        val myReact = getMyReact(likeUsers, dislikeUsers, email)
                        posts.add(
                            ResPost(
                                _id = post._id!!,
                                user = resUser,
                                content = post.content,
                                my_react = myReact,
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
            get<MyPostsGetterRoute> {

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
                        val myReact = getMyReact(likeUsers, dislikeUsers, email)

                        posts.add(
                            ResPost(
                                _id = post._id!!,
                                user = resUser,
                                content = post.content,
                                my_react = myReact,
                                likes_count = post.likes_count,
                                likes_users = likeUsers,
                                dislike_count = post.dislike_count,
                                dislike_users = dislikeUsers,
                                created_at = getTimeAgo(post.created_at.toLong()),
                                modified_at = getTimeAgo(post.modified_at.toLong())
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
                    val postId = call.parameters["post_id"]

                    val doc = db.getDocumentById(POSTS_COLLECTION, postId).also { printlnDebug("$it") }
                    val dbPost = doc?.toDataClass<DbPost>()!!.also { printlnDebug("$it") }



                    if (email == dbPost.user_email && dbPost._id != null) {
                        db.deleteDocument(POSTS_COLLECTION, postId).also {
                            printlnDebug("$it")
                            if (it.second == "success") {

                                val user = getResUser(email, db)
                                val likesUsers = getResUsers(dbPost.likes_users_emails, db)
                                val dislikeUsers = getResUsers(dbPost.dislike_users_emails, db)
                                val myReact = getMyReact(likesUsers, dislikeUsers, email)

                                val resPost = ResPost(
                                    _id = dbPost._id,
                                    user = user,
                                    my_react = myReact,
                                    content = dbPost.content,
                                    likes_count = likesUsers.size,
                                    likes_users = likesUsers,
                                    dislike_count = dislikeUsers.size,
                                    dislike_users = dislikeUsers,
                                    created_at = dbPost.created_at,
                                    modified_at = dbPost.modified_at
                                )
                                okHttpHandler(resPost, "deleted successfully")
                            }
                        }

                    } else unauthorizedRequestHandler("you are not unauthorized to delete this post")

                } catch (e: Exception) {

                    conflictRequestHandler("Some Problems Occurred!: $e")

                }

            }

            ////////////////////////////////////////////////////////////
            //////////////////////////////////////////////// UPDATE POST
            ////////////////////////////////////////////////////////////
            patch("$EDIT_POST_REQUEST/{post_id}") {

                val postRequest = try {
                    call.receive<PostRequest>()
                } catch (e: Exception) {
                    badRequestHandler("Missing Fields")
                    return@patch
                }

                val postId = call.parameters["post_id"]
                val email = call.principal<User>()?.email

                try {

                    val doc = db.getDocumentById(POSTS_COLLECTION, postId).also { printlnDebug("$it") }
                    val dbPost = doc?.toDataClass<DbPost>()!!.also { printlnDebug("$it") }

                    val currentDate = Calendar.getInstance().timeInMillis.toString()

                    // preparing post to update
                    val updates = DbPost(
                        _id = dbPost._id,
                        user_email = dbPost.user_email,
                        content = postRequest.content,
                        likes_count = dbPost.likes_count,
                        likes_users_emails = dbPost.likes_users_emails,
                        dislike_count = dbPost.dislike_count,
                        dislike_users_emails = dbPost.dislike_users_emails,
                        created_at = dbPost.created_at,
                        modified_at = currentDate,
                    )

                    if (email == dbPost.user_email && dbPost._id != null) {

                        db.updateExistingDocument(POSTS_COLLECTION, postId, updates.toJson()).also {

                            if (it.first == 1) {

                                // preparing post to responde
                                // GET USER
                                val userDoc = db.getDocumentByEmail(USERS_COLLECTION, dbPost.user_email)!!
                                val user = userDoc.toDataClass<User>().also { printlnDebug("$it") }
                                val resUser = ResUser(user._id!!, user.username, user.email, user.phone)
                                val likeUsers = getResUsers(dbPost.likes_users_emails, db).also { printlnInfo("$it") }
                                val dislikeUsers =
                                    getResUsers(dbPost.dislike_users_emails, db).also { printlnInfo("$it") }
                                val modifiedAt = TimeAgo.using(dbPost.modified_at.toLong()).also { printlnError(it) }
                                val myReact = getMyReact(likeUsers, dislikeUsers, email)

                                okHttpHandler(
                                    ResPost(
                                        _id = postRequest.content,
                                        user = resUser,
                                        content = dbPost.content,
                                        my_react = myReact,
                                        likes_count = likeUsers.size,
                                        likes_users = likeUsers,
                                        dislike_count = dislikeUsers.size,
                                        dislike_users = dislikeUsers,
                                        created_at = dbPost.created_at,
                                        modified_at = modifiedAt
                                    ), "Post updated Successfully"
                                )

                            } else notFoundRequestHandler("something wnd wrong!")

                        }

                    } else unauthorizedRequestHandler("you are not unauthorized to delete this post")

                } catch (e: Exception) {

                    conflictRequestHandler("Some Problems Occurred!: $e")

                }

            }

            ////////////////////////////////////////////////////////////
            /////////////////////////////////////////////// UPDATE REACT
            ////////////////////////////////////////////////////////////
            patch("$REACT_POST_REQUEST/{post_id}") {

                val reactRequest = try {
                    call.receive<ReactRequest>().also { printlnInfo(it.toJson()) }
                } catch (e: Exception) {
                    badRequestHandler("Missing Fields")
                    return@patch
                }

                val postId = call.parameters["post_id"]
                val email = call.principal<User>()?.email

                try {

                    val doc = db.getDocumentById(POSTS_COLLECTION, postId).also { printlnDebug("$it") }
                    val dbPost = doc?.toDataClass<DbPost>()!!.also { printlnDebug("$it") }

                    // preparing post to update
                    val currentDate = Calendar.getInstance().timeInMillis.toString()

                    val likeUsers = dbPost.likes_users_emails

                    val dislikeUsers = dbPost.dislike_users_emails

                    when (reactRequest.my_react) {
                        "like" -> likeUsers.add(email!!).also {
                            if (likeUsers.contains(email)) {
                                dislikeUsers.remove(email)
                            }
                        }.also { printlnInfo(likeUsers.toJson()) }
                        "dislike" -> dislikeUsers.add(email!!).also {
                            if (dislikeUsers.contains(email)) {
                                likeUsers.remove(email)
                            }
                        }.also { printlnInfo(dislikeUsers.toJson()) }
                    }


                    // preparing post to update
                    val updates = DbPost(
                        _id = dbPost._id,
                        user_email = dbPost.user_email,
                        content = dbPost.content,
                        likes_count = likeUsers.size, // if like ++ || -- || while != 0
                        likes_users_emails = likeUsers, // add user email if like
                        dislike_count = dislikeUsers.size, // if like -- || ++ || while != 0
                        dislike_users_emails = dislikeUsers, // add user email if dislike
                        created_at = dbPost.created_at,
                        modified_at = currentDate, // update
                    )

                    db.updateExistingDocument(POSTS_COLLECTION, postId, updates.toJson()).also {

                        if (it.first == 1) {

                            // preparing post to responde
                            // GET USER
                            val userDoc = db.getDocumentByEmail(USERS_COLLECTION, dbPost.user_email)!!
                            val user = userDoc.toDataClass<User>().also { printlnDebug("$it") }
                            val resUser = ResUser(user._id!!, user.username, user.email, user.phone)
                            val likeUsersData = getResUsers(updates.likes_users_emails, db).also { printlnInfo("$it") }
                            val dislikeUsersData =
                                getResUsers(updates.dislike_users_emails, db).also { printlnInfo("$it") }
                            val modifiedAt = TimeAgo.using(updates.modified_at.toLong()).also { printlnError(it) }
                            val myReact = getMyReact(likeUsersData, dislikeUsersData, email!!)

                            okHttpHandler(
                                ResPost(
                                    _id = postId!!,
                                    user = resUser,
                                    content = dbPost.content,
                                    my_react = myReact,
                                    likes_count = likeUsersData.size,
                                    likes_users = likeUsersData,
                                    dislike_count = dislikeUsersData.size,
                                    dislike_users = dislikeUsersData,
                                    created_at = dbPost.created_at,
                                    modified_at = dbPost.modified_at
                                ), "Successfully $myReact the post"
                            )

                        } else notFoundRequestHandler("Something went wrong!")

                    }

                } catch (e: Exception) {

                    conflictRequestHandler("Some Problems Occurred!: $e")

                }

            }

        }

    }

    // Never Used -- We Can Use call.parameters["path"] instead
    /*private fun PipelineContext<Unit, ApplicationCall>.getRequestPath(firstPart: String): String {
        val path = call.request.path()
        return path.slice((firstPart.length + 1) until path.length)
    }*/

    private fun getMyReact(likeUsers: List<ResUser>, dislikeUsers: List<ResUser>, requestedEmail: String): String {
        var myReact = ""
        val likesEmails = mutableListOf<String>()
        val disLikesEmails = mutableListOf<String>()
        likeUsers.forEach { likesEmails.add(it.email) }
        dislikeUsers.forEach { disLikesEmails.add(it.email) }
        if (likesEmails.contains(requestedEmail)) {
            myReact = "like"
        } else {
            if (disLikesEmails.contains(requestedEmail)) {
                myReact = "dislike"
            }
        }
        return myReact
    }

    private fun getResUsers(emails: List<String>, db: MongoDataService): List<ResUser> {
        val usersList = mutableListOf<ResUser>()
        for (email in emails) {
            val userDoc = db.getDocumentByEmail(USERS_COLLECTION, email)!!
            val user = userDoc.toDataClass<User>()
            val resUser = ResUser(
                _id = user._id!!,
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
            _id = user._id!!,
            username = user.username,
            email = user.email,
            phone = user.phone
        )
    }

    //////////////////////////////////////////////////////////////// SUCCESS STATUS CODES
    // for single post
    private suspend fun PipelineContext<Unit, ApplicationCall>.okHttpHandler(data: ResPost, message: String) {
        call.respondJsonResponse(PostResponse(true, data, message), HTTP_OK)
    }

    // for list of posts
    private suspend fun PipelineContext<Unit, ApplicationCall>.okHttpHandler(data: List<ResPost>, message: String,page:Int, totalResult:Int) {
        call.respondJsonResponse(PostsResponse(true, data,page,totalResult,message), HTTP_OK)
    }

    //////////////////////////////////////////////////////////////// ERROR STATUS CODES
    private suspend fun PipelineContext<Unit, ApplicationCall>.badRequestHandler(message: String) {
        call.respondJsonResponse(PostResponse(false, null, message), HTTP_BAD_REQUEST)
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.conflictRequestHandler(message: String) {
        call.respondJsonResponse(PostResponse(false, null, message), HTTP_CONFLICT)
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.unauthorizedRequestHandler(message: String) {
        call.respondJsonResponse(PostResponse(false, null, message), HTTP_UNAUTHORIZED)
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.notFoundRequestHandler(message: String) {
        call.respondJsonResponse(PostResponse(false, null, message), HTTP_NOT_FOUND)
    }


}