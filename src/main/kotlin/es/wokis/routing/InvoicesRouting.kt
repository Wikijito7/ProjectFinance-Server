package es.wokis.routing

import es.wokis.data.repository.user.UserRepository
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.setUpInvoicesRouting() {
    val userRepository by inject<UserRepository>() // TODO: change to invoices repository

}