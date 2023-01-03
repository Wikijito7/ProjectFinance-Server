package es.wokis.data.dto.user.auth

import com.google.gson.annotations.SerializedName
import es.wokis.data.constants.ServerConstants.DEFAULT_LANG

data class LoginDTO(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    val isGoogleAuth: Boolean = false
)

data class RegisterDTO(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("lang")
    val lang: String = DEFAULT_LANG,
    val isGoogleAuth: Boolean = false
)

data class AuthResponseDTO(
    @SerializedName("authToken")
    val authToken: String
)

data class GoogleAuthDTO (
    @SerializedName("authToken")
    val authToken: String
)