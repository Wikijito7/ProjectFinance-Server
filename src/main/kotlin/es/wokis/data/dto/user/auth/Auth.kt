package es.wokis.data.dto.user.auth

data class LoginDTO(
    val username: String,
    val password: String
)

data class RegisterDTO(
    val username: String,
    val email: String,
    val password: String
)