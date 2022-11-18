package es.wokis.plugins

import es.wokis.routing.setUpInvoicesRouting
import es.wokis.routing.setUpAuthRouting
import es.wokis.routing.setUpUserRouting
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World from Project Finance!")
        }

        setUpAuthRouting()
        setUpInvoicesRouting()
        setUpUserRouting()
    }
}
