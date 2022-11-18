package es.wokis.data.bo.user

import es.wokis.data.constants.ServerConstants.EMPTY_TEXT
import io.ktor.server.auth.*

data class UserBO(
    val id: String? = null,
    val username: String,
    val email: String,
    val password: String,
    val image: String = EMPTY_TEXT,
    val devices: List<String> = emptyList()
) : Principal