package com.george.utiles

object Constants {

    private const val API_VERSION = "/api/v1"

    private const val AUTH = "$API_VERSION/auth"
    const val REGISTER_REQUEST = "$AUTH/register"
    const val LOGIN_REQUEST = "$AUTH/login"

    private const val SOCIAL = "$API_VERSION/social"
    private const val POSTS = "$SOCIAL/posts"
    const val CREATE_POST_REQUEST = "$POSTS/create-post" // Create a new Post
    const val GET_POST_REQUEST = "$POSTS/get-post" // get post with {id}
    const val EDIT_POST_REQUEST = "$POSTS/edit-post" // edit post with {id}
    const val DELETE_POST_REQUEST = "$POSTS/delete-post" // delete post with {id}
    const val GET_POSTS_REQUEST = POSTS // get all posts
    const val GET_MY_POSTS_REQUEST = "$POSTS/my-posts" // get my posts


    const val USERS_COLLECTION = "users"
    const val POSTS_COLLECTION = "posts"
}