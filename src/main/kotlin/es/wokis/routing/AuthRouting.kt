package es.wokis.routing

import es.wokis.data.dto.user.auth.LoginDTO
import es.wokis.data.dto.user.auth.RegisterDTO
import es.wokis.data.repository.user.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.setUpAuthRouting() {
    val userRepository by inject<UserRepository>()

    post("/login") {
        val user = call.receive<LoginDTO>()
        val token: String? = userRepository.login(user)

        token?.let {
            call.respond(HttpStatusCode.OK, it)
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "Wrong username or password")
        }
    }

    post("/register") {
        val user = call.receive<RegisterDTO>()

        val token: String? = userRepository.register(user)

        token?.let {
            call.respond(HttpStatusCode.OK, it)
        } ?: run {
            call.respond(HttpStatusCode.Conflict, "That user already exists")
        }
    }

    post("/google-auth") {
        val googleToken = call.receive<String>()
        val token: String? = userRepository.loginWithGoogle(googleToken)
        token?.let {
            call.respond(HttpStatusCode.OK, it)
        } ?: call.respond(HttpStatusCode.NotFound, "That user doesn't exists.")
    }
}