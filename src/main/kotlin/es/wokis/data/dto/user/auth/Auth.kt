package es.wokis.data.dto.user.auth

import es.wokis.data.constants.ServerConstants.DEFAULT_LANG

data class LoginDTO(
    val username: String,
    val password: String,
    val isGoogleAuth: Boolean = false
)

data class RegisterDTO(
    val username: String,
    val email: String,
    val password: String,
    val lang: String = DEFAULT_LANG,
    val isGoogleAuth: Boolean = false
)