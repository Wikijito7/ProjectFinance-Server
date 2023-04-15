package es.wokis.services

import com.google.cloud.storage.Acl.User
import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import dev.turingcomplete.kotlinonetimepassword.OtpAuthUriBuilder
import es.wokis.data.bo.response.AcknowledgeBO
import es.wokis.data.bo.user.TOTPResponseBO
import es.wokis.data.bo.user.UserBO
import es.wokis.data.constants.ServerConstants.EMPTY_TEXT
import es.wokis.data.dto.user.auth.TOTPRequestDTO
import es.wokis.data.repository.user.UserRepository
import es.wokis.plugins.config
import es.wokis.plugins.issuer
import es.wokis.utils.HashGenerator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.StringUtils
import java.util.*

const val AUTHENTICATOR_HEADER = "WWW-Authenticate"
const val TIMESTAMP_HEADER = "timestamp"
const val GOOGLE_AUTHENTICATOR = "Google"
const val TOTP_HEADER = "2FA"

class TOTPService(private val userRepository: UserRepository) {
    /**
     * Sets up TOTP for User's account.
     * @throws IllegalStateException if repository cannot update user with TOTP
     * @return TOTPResponseBO with encodedSecret and totp url
     */
    suspend fun setUpTOTP(user: UserBO): TOTPResponseBO {
        val plainSecretCode = HashGenerator.generateHash(10).toByteArray(Charsets.UTF_8)
        val encodedSecret = Base32().encode(plainSecretCode)
        val totpUrl = GoogleAuthenticator(encodedSecret)
            .otpAuthUriBuilder()
            .issuer(config.issuer)
            .buildToString()
        val acknowledge = userRepository.saveTOTPEncodedSecret(user, encodedSecret).acknowledge
        if (acknowledge) {
            return TOTPResponseBO(
                StringUtils.newStringUtf8(encodedSecret),
                totpUrl
            )
        }
        throw IllegalStateException()
    }

    suspend fun removeTOTP(user: UserBO): AcknowledgeBO = userRepository.removeTOTP(user)

}

suspend inline fun PipelineContext<Unit, ApplicationCall>.withAuthenticator(user: UserBO, block: () -> Unit) {
    val secret = user.totpEncodedSecret
    if (secret == null) {
        block()
        return
    }

    checkTOTP(secret) {
        block()
    }
}

suspend inline fun PipelineContext<Unit, ApplicationCall>.checkTOTP(
    secret: ByteArray,
    block: () -> Unit
) {
    val googleAuthenticator = GoogleAuthenticator(secret)
    val code = call.request.header(TOTP_HEADER)
    val timeStamp = call.request.header(TIMESTAMP_HEADER)?.toLongOrNull()

    code?.let { codeNotNull ->
        timeStamp?.let { timeStampNotNull ->
            if (googleAuthenticator.isValid(codeNotNull, Date(timeStampNotNull))) {
                block()

            } else {
                respondNotAuthorization()
            }
        } ?: respondNotAuthorization()
    } ?: respondNotAuthorization()
}

suspend fun PipelineContext<Unit, ApplicationCall>.respondNotAuthorization() {
    call.response.header(AUTHENTICATOR_HEADER, GOOGLE_AUTHENTICATOR)
    call.respond(HttpStatusCode.Unauthorized, TOTPRequestDTO(GOOGLE_AUTHENTICATOR, System.currentTimeMillis()))
}