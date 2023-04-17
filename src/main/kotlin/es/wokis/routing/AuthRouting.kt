package es.wokis.routing

import es.wokis.data.dto.user.auth.*
import es.wokis.data.mapper.invoice.toDTO
import es.wokis.data.mapper.user.toBO
import es.wokis.data.repository.user.UserRepository
import es.wokis.data.repository.verify.VerifyRepository
import es.wokis.services.EmailService
import es.wokis.services.ImageService
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
    val verifyRepository by inject<VerifyRepository>()
    val emailService by inject<EmailService>()
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
                try {
                    emailService.sendEmail(user.toBO())

                } catch (exc: Exception) {
                    // no-op: as it isn't critical at this time
                }
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

        get("/verify/{token}") {
            val token = call.parameters["token"]

            token?.let {
                try {
                    call.respond(verifyRepository.verify(token))

                } catch (exc: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        authenticate {
            post("/verify") {
                val user = call.user
                user?.let {
                    try {
                        emailService.sendEmail(it)?.also { verification ->
                            call.respond(HttpStatusCode.OK, verification)

                        } ?: call.respond(HttpStatusCode.ServiceUnavailable)

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