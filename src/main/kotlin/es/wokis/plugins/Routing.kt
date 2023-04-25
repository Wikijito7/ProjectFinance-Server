package es.wokis.plugins

import es.wokis.routing.setUpInvoicesRouting
import es.wokis.routing.setUpAuthRouting
import es.wokis.routing.setUpUserRouting
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        setUpAuthRouting()
        setUpInvoicesRouting()
        setUpUserRouting()
    }
}
