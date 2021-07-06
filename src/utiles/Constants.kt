package com.george.utiles

import com.george.Models.User.Location
import com.george.Models.User.User
import kotlin.random.Random

object Constants {

    private fun randomPhoneNumber() = Random.nextInt(10000000,99999999).toString()
    private val providers = listOf("010","011","012","015")
    private fun randomProvider() = providers[Random.nextInt(3)]

    fun generateRandomUsers(): MutableList<User> {
        val users = mutableListOf<User>()
        for (i in 1..30) {
            users.add(
                User(
                    i,
                    "user num $i",
                    "user_${i*2}@gmail.com",
                    "${randomProvider()}${randomPhoneNumber()}",
                    Location(
                        lat = 21.05248495,
                        long = 32.89951685
                    )
                )
            )
        }
        return users
    }

}