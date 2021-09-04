package com.george.utiles

import com.pusher.rest.Pusher

object PusherConfiguration {

    private const val PUSHER_APP_ID = "1257442"
    private const val PUSHER_APP_KEY = "7fc3e37ce61b12aca4c4"
    private const val PUSHER_APP_SECRET = "2cd894c165cea5f91d6e"
    const val PUSHER_CLUSTER = "eu"

    val pusher = Pusher(PUSHER_APP_ID, PUSHER_APP_KEY, PUSHER_APP_SECRET).apply {
        setCluster(PUSHER_CLUSTER)
        setEncrypted(true)
    }

}