package es.wokis.routing

import es.wokis.data.mapper.user.toDTO
import es.wokis.data.repository.user.UserRepository
import es.wokis.utils.user
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.setUpUserRouting() {
    val userRepository by inject<UserRepository>()
    authenticate {
        get("/users") {
            val users = userRepository.getUsers().toDTO()
            call.respond(users)
        }

        authenticate {
            get("/user") {
                val callUser = call.user
                callUser?.id?.let {
                    val user = userRepository.getUserById(it)
                    user?.let {
                        call.respond(HttpStatusCode.OK, user.toDTO())
                    } ?: call.respond(HttpStatusCode.NotFound, callUser)

                } ?: call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}