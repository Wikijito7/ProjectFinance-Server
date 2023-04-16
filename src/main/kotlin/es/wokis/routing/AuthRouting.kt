package es.wokis.routing

import es.wokis.data.dto.user.auth.*
import es.wokis.data.mapper.invoice.toDTO
import es.wokis.data.mapper.user.toBO
import es.wokis.data.repository.user.UserRepository
import es.wokis.services.EmailService
import es.wokis.services.withAuthenticator
import es.wokis.utils.user
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.setUpAuthRouting() {
    val userRepository by inject<UserRepository>()
    rateLimit(RateLimitName("auth")) {
        post("/login") {
            val user = call.receive<LoginDTO>()
            val token: String? = userRepository.login(user)

            token?.let {
                call.respond(HttpStatusCode.OK, AuthResponseDTO(it))
            } ?: run {
                call.respond(HttpStatusCode.NotFound, "Wrong username or password")
            }
        }

        post("/register") {
            val user = call.receive<RegisterDTO>()
            val token: String? = userRepository.register(user)
            token?.let {
                EmailService.sendEmail(user.toBO())
                call.respond(HttpStatusCode.OK, AuthResponseDTO(it))

            } ?: run {
                call.respond(HttpStatusCode.Conflict, "That user already exists")
            }
        }

        post("/google-auth") {
            val googleToken = call.receive<GoogleAuthDTO>()
            val token: String? = userRepository.loginWithGoogle(googleToken)
            token?.let {
                call.respond(HttpStatusCode.OK, AuthResponseDTO(it))
            } ?: call.respond(HttpStatusCode.NotFound, "That user doesn't exists.")
        }

        authenticate {
            post("/verify") {
                val user = call.user
                user?.let {
                    try {
                        val verificationBO = EmailService.sendEmail(it)
                        call.respond(HttpStatusCode.OK, verificationBO)

                    } catch (exc: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, exc.stackTraceToString())
                    }


                } ?: call.respond(HttpStatusCode.ExpectationFailed)
            }

            post("/change-pass") {
                val user = call.user
                val changePass: ChangePassRequestDTO = call.receive()
                user?.let {
                    withAuthenticator(it) {
                        try {
                            call.respond(userRepository.changePass(user, changePass).toDTO())

                        } catch (exc: Exception) {
                            call.respond(HttpStatusCode.Conflict)
                        }
                    }
                } ?: call.respond(HttpStatusCode.ExpectationFailed)
            }

            post("/logout") {
                val user = call.user
                user?.let {
                    call.respond(userRepository.logout(user).toDTO())
                } ?: call.respond(HttpStatusCode.ExpectationFailed)
            }

            delete("/sessions") {
                val user = call.user
                user?.let {
                    call.respond(userRepository.closeAllSessions(user).toDTO())
                } ?: call.respond(HttpStatusCode.ExpectationFailed)
            }
        }
    }
}