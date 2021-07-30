package com.george.utiles

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.george.models.users.User
import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.text.toByteArray

object JwtService {

    /**
     * # Claims
     *
     * ## Registered Claims
     * #### __they not necessary to use but they recommended to use__
     * * iss (issuer)
     * * exp (expiration time)
     * * sub (subject)
     * * aud (audience)
     *
     * ## Public Claims
     * * IANA JSON Web Token Registry
     *
     * ## Private Claims
     * * Email
     * * Registration number
     *
     * */

    private val issuer = "noteServer"
    private val jwtSecret = System.getenv("JWT_SECRET")
    private val algorithm = Algorithm.HMAC256(jwtSecret)

    val verifier : JWTVerifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generatorToken(user: User) =
        JWT.create()
            .withSubject("WeCanTypeAnySubject")
            .withIssuer(issuer)
            .withClaim("email",user.email)
            /*.withExpiresAt(Date(System.currentTimeMillis() + 60000))*/
            .sign(algorithm)!!

    private val hashKey = System.getenv("HASH_SECRET_KEY").toByteArray()
    private val hmacKey = SecretKeySpec(hashKey,"HmacSHA1")

    fun createHash(password:String):String {
        val hmac = Mac.getInstance("HmacSHA1")
        hmac.init(hmacKey)
        return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
    }

}