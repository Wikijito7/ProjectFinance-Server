package es.wokis.data.dto.user

import es.wokis.data.constants.ServerConstants

data class UserDTO(
    val id: String,
    val username: String,
    val email: String,
    val image: String = ServerConstants.EMPTY_TEXT,
    val devices: List<String> = emptyList()
)