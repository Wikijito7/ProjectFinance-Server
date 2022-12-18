package es.wokis.plugins

import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

val appModules = module {
    // TODO: Modules
}

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(appModules)
    }
}