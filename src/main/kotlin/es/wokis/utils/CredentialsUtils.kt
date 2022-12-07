package es.wokis.utils

import es.wokis.data.bo.user.UserBO
import io.ktor.server.application.*
import io.ktor.server.auth.*

val ApplicationCall.user: UserBO? get() = authentication.principal()