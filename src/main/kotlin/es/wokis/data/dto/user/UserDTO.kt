package es.wokis.data.dto.user

import es.wokis.data.constants.ServerConstants
import java.util.*

data class UserDTO(
    val id: String,
    val username: String,
    val email: String,
    val image: String = ServerConstants.EMPTY_TEXT,
    val lang: String,
    val createdOn: Long = Date().time,
    val emailVerified: Boolean = false,
    val badges: List<BadgeDTO> = emptyList(),
    val devices: List<String> = emptyList()
)

data class BadgeDTO(
    val id: Int,
    val color: String
)