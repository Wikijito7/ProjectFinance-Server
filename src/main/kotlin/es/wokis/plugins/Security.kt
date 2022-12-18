package es.wokis.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.google.firebase.auth.hash.Bcrypt
import es.wokis.data.bo.user.UserBO
import es.wokis.data.repository.user.UserRepository
import es.wokis.utils.orGeneratePassword
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject
import org.mindrot.jbcrypt.BCrypt
import java.util.*

private lateinit var jwtIssuer: String
private lateinit var jwtAudience: String
private lateinit var algorithm: Algorithm

fun Application.configureSecurity() {
    val privateKey = config.getString("secretKey")
    val userRepository by inject<UserRepository>()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    jwtIssuer = environment.config.property("jwt.domain").getString()
    jwtAudience = environment.config.property("jwt.audience").getString()
    algorithm = Algorithm.HMAC256(privateKey)

    authentication {
        jwt {
            realm = jwtRealm
            verifier(makeJwtVerifier(jwtIssuer, jwtAudience))
            validate { credential ->
                val name = credential.payload.getClaim("username").asString()
                val password = credential.payload.getClaim("password").asString()

                val user = userRepository.getUserByUsername(name)

                user?.takeIf { password == it.password }
            }
        }
    }

}

private fun makeJwtVerifier(issuer: String, audience: String): JWTVerifier =
    JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer).build()

/**
 * A function that creates a token with given user data.
 *
 * @param user the user whose token is being made
 */

fun makeToken(user: UserBO): String =
    JWT
        .create()
        .withSubject("Authentication")
        .withIssuer(jwtIssuer)
        .withAudience(jwtAudience)
        .withClaim("username", user.username)
        .withClaim("password", user.password.takeIf { it.isNotBlank() }.orGeneratePassword() )
        .withClaim("timestamp", Date().time)
        .sign(algorithm)
