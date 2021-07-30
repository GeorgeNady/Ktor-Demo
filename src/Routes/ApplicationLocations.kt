package com.george.routes

import com.george.utiles.Constants.CREATE_POST_REQUEST
import com.george.utiles.Constants.DELETE_POST_REQUEST
import com.george.utiles.Constants.EDIT_POST_REQUEST
import com.george.utiles.Constants.GET_MY_POSTS_REQUEST
import com.george.utiles.Constants.GET_POSTS_REQUEST
import com.george.utiles.Constants.GET_POST_REQUEST
import com.george.utiles.Constants.LOGIN_REQUEST
import com.george.utiles.Constants.REGISTER_REQUEST
import io.ktor.locations.*

object ApplicationLocations {

    //////////////////////////////////////////////////////// Authentication
    @Location(REGISTER_REQUEST) class AuthRegisterRoute
    @Location(LOGIN_REQUEST) class AuthLoginRoute

    //////////////////////////////////////////////////////// Posts
    @Location(CREATE_POST_REQUEST) class PostCreateRoute
    @Location(GET_POST_REQUEST) class PostGetterRoute
    @Location(EDIT_POST_REQUEST) class PostEditRoute
    @Location(DELETE_POST_REQUEST) class PostDeleteRoute
    @Location(GET_POSTS_REQUEST) class AllPostsGetterRoute
    @Location(GET_MY_POSTS_REQUEST) class MyPostsGetterRoute
}