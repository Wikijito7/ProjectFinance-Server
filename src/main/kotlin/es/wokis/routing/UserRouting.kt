package es.wokis.routing

import es.wokis.data.constants.ServerConstants.EMPTY_TEXT
import es.wokis.data.dto.user.update.UpdateUserDTO
import es.wokis.data.mapper.invoice.toDTO
import es.wokis.data.mapper.user.toBO
import es.wokis.data.mapper.user.toDTO
import es.wokis.data.repository.user.UserRepository
import es.wokis.services.ImageService
import es.wokis.utils.user
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.setUpUserRouting() {
    val userRepository by inject<UserRepository>()

    route("/user/{id}/avatar") {
        get {
            val id = call.parameters["id"]

            id?.let {
                call.respondFile(ImageService.getAvatar(id))
            }
        }
    }

    authenticate {
        get("/users") {
            val users = userRepository.getUsers().toDTO()
            call.respond(users)
        }

        route("/user") {
            get {
                val callUser = call.user
                callUser?.id?.let {
                    val user = userRepository.getUserById(it)
                    user?.let {
                        call.respond(HttpStatusCode.OK, user.toDTO())
                    } ?: call.respond(HttpStatusCode.NotFound, callUser)

                } ?: call.respond(HttpStatusCode.Unauthorized)
            }

            put {
                val callUser = call.user
                val updatedUser = call.receive<UpdateUserDTO>()
                callUser?.let {
                    val acknowledged = userRepository.updateUser(callUser, updatedUser.toBO())
                    call.respond(HttpStatusCode.OK, acknowledged.toDTO())

                } ?: call.respond(HttpStatusCode.Unauthorized)
            }

            route("/image") {
                post {
                    val multipartData = call.receiveMultipart()
                    val callUser = call.user
                    callUser?.let { user ->
                        val image: PartData.FileItem = multipartData.readAllParts()
                            .find { part ->
                                part is PartData.FileItem
                            }?.takeIf { part ->
                                part.contentType.toString().startsWith("image")
                            } as? PartData.FileItem ?: run {
                            call.respond(HttpStatusCode.UnsupportedMediaType)
                            return@post
                        }

                        user.id?.let {
                            ImageService.insertAvatar(it, image).also { avatarUrl ->
                                // this is used to reload the image as we always use the same name
                                val url = "$avatarUrl?${System.currentTimeMillis()}"
                                call.respond(HttpStatusCode.OK, userRepository.updateUserAvatar(user, url))
                            }
                        } ?: call.respond(HttpStatusCode.UnprocessableEntity)

                    } ?: call.respond(HttpStatusCode.Unauthorized)
                }

                delete {
                    val callUser = call.user
                    callUser?.let { user ->
                        call.respond(HttpStatusCode.OK, userRepository.updateUserAvatar(user, EMPTY_TEXT))
                    } ?: call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
    }
}