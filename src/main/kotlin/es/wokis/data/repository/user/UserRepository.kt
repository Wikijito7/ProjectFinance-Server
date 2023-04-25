package es.wokis.data.repository.user

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import es.wokis.data.bo.response.AcknowledgeBO
import es.wokis.data.bo.user.BadgeBO
import es.wokis.data.bo.user.UpdateUserBO
import es.wokis.data.bo.user.UserBO
import es.wokis.data.constants.ServerConstants.DEFAULT_LANG
import es.wokis.data.constants.ServerConstants.EMPTY_TEXT
import es.wokis.data.constants.ServerConstants.MAX_OG_DATE
import es.wokis.data.datasource.user.UserLocalDataSource
import es.wokis.data.dto.user.auth.ChangePassRequestDTO
import es.wokis.data.dto.user.auth.GoogleAuthDTO
import es.wokis.data.dto.user.auth.LoginDTO
import es.wokis.data.dto.user.auth.RegisterDTO
import es.wokis.data.enums.BadgesEnum
import es.wokis.data.enums.toBadge
import es.wokis.data.exception.EmailAlreadyExistsException
import es.wokis.data.exception.PasswordConflictException
import es.wokis.data.exception.UsernameAlreadyExistsException
import es.wokis.data.mapper.user.toBO
import es.wokis.data.mapper.user.toLoginDTO
import es.wokis.plugins.config
import es.wokis.plugins.makeToken
import es.wokis.projectfinance.utils.toDate
import es.wokis.services.EmailService
import es.wokis.services.checkTOTP
import es.wokis.utils.HashGenerator
import es.wokis.utils.isEmail
import org.mindrot.jbcrypt.BCrypt
import java.util.*

interface UserRepository {
    suspend fun login(login: LoginDTO, code: String?, timeStamp: Long?): String?
    suspend fun loginWithGoogle(googleToken: GoogleAuthDTO, code: String?, timeStamp: Long?): String?
    suspend fun register(register: RegisterDTO): String?
    suspend fun getUsers(): List<UserBO>
    suspend fun getUserById(id: String?): UserBO?
    suspend fun getUserByUsername(name: String?): UserBO?
    suspend fun getUserByEmail(email: String?): UserBO?
    suspend fun updateUser(user: UserBO, updatedUser: UpdateUserBO? = null): AcknowledgeBO
    suspend fun updateUserAvatar(user: UserBO, avatarUrl: String): AcknowledgeBO
    suspend fun saveTOTPEncodedSecret(user: UserBO, encodedSecret: ByteArray, recoverWords: List<String>): AcknowledgeBO
    suspend fun removeTOTP(user: UserBO): AcknowledgeBO
    suspend fun changePass(user: UserBO, changePass: ChangePassRequestDTO): AcknowledgeBO
    suspend fun logout(user: UserBO): AcknowledgeBO
    suspend fun closeAllSessions(user: UserBO): AcknowledgeBO
    suspend fun getUserByEmailOrUsername(username: String): UserBO?
}

class UserRepositoryImpl(
    private val userLocalDataSource: UserLocalDataSource,
) : UserRepository {
    override suspend fun login(login: LoginDTO, code: String?, timeStamp: Long?): String? {
        val user = getUserByEmailOrUsername(login.username)
        user?.totpEncodedSecret?.let {
            checkTOTP(it, code, timeStamp) {
                return doLogin(user, login)
            }
        }

        return doLogin(user, login)
    }

    private suspend fun doLogin(
        user: UserBO?,
        login: LoginDTO
    ): String? {
        return user?.let {
            if (BCrypt.checkpw(login.password, it.password)) {
                makeJWTToken(user)

            } else {
                null
            }
        }
    }

    override suspend fun getUserByEmailOrUsername(username: String): UserBO? = if (username.isEmail()) {
        userLocalDataSource.getUserByEmail(username)

    } else {
        userLocalDataSource.getUserByUsername(username)
    }

    override suspend fun loginWithGoogle(googleToken: GoogleAuthDTO, code: String?, timeStamp: Long?): String? {
        val verifier: GoogleIdTokenVerifier =
            GoogleIdTokenVerifier.Builder(
                NetHttpTransport(),
                GsonFactory()
            )
                .setAudience(listOf(config.getString("google.clientId")))
                .setIssuer("https://accounts.google.com")
                .build()

        return verifier.verify(googleToken.authToken)?.let {
            val payload: GoogleIdToken.Payload = it.payload

            // Print user identifier
            val userId: String = payload.subject

            // Get profile information from payload
            val email: String = payload.email
            val imageUrl: String = (payload["picture"] as? String).orEmpty()
            val locale: String = payload["locale"] as? String ?: DEFAULT_LANG
            val user = getUserByEmail(email)
            val token = if (user == null) {
                val token = register(
                    RegisterDTO(
                        username = userId,
                        email = email,
                        password = EMPTY_TEXT,
                        isGoogleAuth = true,
                        lang = locale
                    )
                )
                getUserByEmail(email)?.let { userNotNull ->
                    val badges = addOGBadge().addVerifyBadge()
                    updateUser(
                        userNotNull.copy(
                            image = imageUrl,
                            emailVerified = true,
                            badges = badges
                        )
                    )
                }
                token

            } else {
                login(
                    LoginDTO(username = email, password = EMPTY_TEXT, isGoogleAuth = true),
                    code,
                    timeStamp
                )
            }
            token
        }
    }

    override suspend fun register(register: RegisterDTO): String? {
        if (register.email.isEmail().not()) return null
        val currentUser = userLocalDataSource.getUserByUsernameOrEmail(register.username, register.email)
        val badges = addOGBadge()
        val user = register.toBO().copy(badges = badges)
        return if (currentUser == null) {
            val wasRegistered = userLocalDataSource.createUser(user)
            if (wasRegistered) {
                login(register.toLoginDTO(), null, null)

            } else {
                null
            }

        } else {
            null
        }
    }

    private fun addOGBadge(): List<BadgeBO> {
        val maxDate = MAX_OG_DATE.toDate().time
        val today = Date().time

        return listOf(BadgesEnum.OG.toBadge()).takeIf { today < maxDate }.orEmpty()
    }

    override suspend fun getUsers(): List<UserBO> = userLocalDataSource.getAllUsers()

    override suspend fun getUserById(id: String?): UserBO? = id?.let {
        userLocalDataSource.getUserById(id)
    }

    override suspend fun getUserByUsername(name: String?): UserBO? = name?.let {
        userLocalDataSource.getUserByUsername(it)
    }

    override suspend fun getUserByEmail(email: String?): UserBO? = email?.let {
        userLocalDataSource.getUserByEmail(it)
    }

    override suspend fun updateUser(user: UserBO, updatedUser: UpdateUserBO?): AcknowledgeBO {
        val userToUpdate: UserBO = updatedUser?.let {
            getUserByUsername(it.username)?.let {
                throw UsernameAlreadyExistsException
            }
            val updatedEmail = updatedUser.email?.takeIf { email -> email.isEmail() }?.also { email ->
                getUserByEmail(email)?.let {
                    throw EmailAlreadyExistsException
                }
            }
            user.copy(
                username = updatedUser.username ?: user.username,
                email = updatedEmail ?: user.email,
                emailVerified = updatedEmail?.let { false } ?: user.emailVerified
            )
        } ?: user
        return AcknowledgeBO(userLocalDataSource.updateUser(userToUpdate))
    }

    override suspend fun updateUserAvatar(user: UserBO, avatarUrl: String) = updateUser(user.copy(image = avatarUrl))
    override suspend fun saveTOTPEncodedSecret(user: UserBO, encodedSecret: ByteArray, recoverWords: List<String>): AcknowledgeBO {
        if (user.totpEncodedSecret == null) {
            return updateUser(user.copy(totpEncodedSecret = encodedSecret, recoverWords = recoverWords))
        }
        return AcknowledgeBO(false)
    }

    override suspend fun removeTOTP(user: UserBO): AcknowledgeBO = updateUser(user.copy(totpEncodedSecret = null, recoverWords = emptyList()))

    override suspend fun changePass(user: UserBO, changePass: ChangePassRequestDTO): AcknowledgeBO {
        if (BCrypt.checkpw(changePass.oldPass, user.password)) {
            return updateUser(
                user.copy(
                    password = BCrypt.hashpw(changePass.newPass, BCrypt.gensalt()),
                    sessions = emptyList()
                )
            )
        }
        throw PasswordConflictException
    }

    override suspend fun logout(user: UserBO): AcknowledgeBO {
        val updatedUser = user.copy(
            sessions = user.sessions.filter { it != user.currentSession }
        )
        return updateUser(updatedUser)
    }

    override suspend fun closeAllSessions(user: UserBO): AcknowledgeBO {
        val updatedUser = user.copy(
            sessions = emptyList()
        )
        return updateUser(updatedUser)
    }

    private suspend fun makeJWTToken(user: UserBO): String {
        val session = HashGenerator.generateHash(20)
        val userSessions = user.sessions.toMutableList().apply { add(session) }.toList()
        userLocalDataSource.updateUser(
            user.copy(
                sessions = userSessions
            )
        )
        return makeToken(user, session)
    }
}

private fun List<BadgeBO>.addVerifyBadge() = this.toMutableList().apply {
    add(BadgesEnum.VERIFIED.toBadge())
}.toList()

