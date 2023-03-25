package es.wokis.data.repository.user

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import es.wokis.data.bo.user.UserBO
import es.wokis.data.constants.ServerConstants.DEFAULT_LANG
import es.wokis.data.constants.ServerConstants.EMPTY_TEXT
import es.wokis.data.datasource.user.UserLocalDataSource
import es.wokis.data.dto.user.auth.GoogleAuthDTO
import es.wokis.data.dto.user.auth.LoginDTO
import es.wokis.data.dto.user.auth.RegisterDTO
import es.wokis.data.mapper.user.toBO
import es.wokis.data.mapper.user.toLoginDTO
import es.wokis.plugins.config
import es.wokis.plugins.makeToken
import es.wokis.utils.HashGenerator
import es.wokis.utils.isEmail
import org.mindrot.jbcrypt.BCrypt

interface UserRepository {
    suspend fun login(login: LoginDTO): String?
    suspend fun loginWithGoogle(googleToken: GoogleAuthDTO): String?
    suspend fun register(register: RegisterDTO): String?
    suspend fun getUsers(): List<UserBO>
    suspend fun getUserById(id: String?): UserBO?
    suspend fun getUserByUsername(name: String?): UserBO?
    suspend fun getUserByEmail(email: String?): UserBO?
    suspend fun updateUser(user: UserBO): Boolean
}

class UserRepositoryImpl(private val userLocalDataSource: UserLocalDataSource) : UserRepository {
    override suspend fun login(login: LoginDTO): String? {
        val user = if (login.username.isEmail()) {
            userLocalDataSource.getUserByEmail(login.username)

        } else {
            userLocalDataSource.getUserByUsername(login.username)
        }
        return user?.let {
            if (BCrypt.checkpw(login.password, it.password)) {
                makeJWTToken(user)

            } else {
                null
            }
        }
    }

    override suspend fun loginWithGoogle(googleToken: GoogleAuthDTO): String? {
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
            println("User ID: $userId")

            // Get profile information from payload
            val email: String = payload.email
            val imageUrl: String = (payload["picture"] as? String).orEmpty()
            val locale: String = payload["locale"] as? String ?: DEFAULT_LANG
            val username = email.split("@").firstOrNull() ?: HashGenerator.generateHash()
            val user = getUserByEmail(email)
            val token = if (user == null) {
                val token = register(
                    RegisterDTO(
                        username = username,
                        email = email,
                        password = EMPTY_TEXT,
                        isGoogleAuth = true,
                        lang = locale
                    )
                )
                getUserByEmail(email)?.let { userNotNull ->
                    updateUser(
                        userNotNull.copy(
                            image = imageUrl,
                            emailVerified = true
                        )
                    )
                }
                token

            } else {
                login(
                    LoginDTO(username = username, password = EMPTY_TEXT, isGoogleAuth = true)
                )
            }
            token
        }
    }

    override suspend fun register(register: RegisterDTO): String? {
        if (register.email.isEmail().not()) return null
        val currentUser = userLocalDataSource.getUserByUsernameOrEmail(register.username, register.email)
        val user = register.toBO()
        return if (currentUser == null) {
            val wasRegistered = userLocalDataSource.createUser(user)
            if (wasRegistered) {
                login(register.toLoginDTO())

            } else {
                null
            }

        } else {
            null
        }
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

    override suspend fun updateUser(user: UserBO): Boolean = userLocalDataSource.updateUser(user)

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