package es.wokis.data.repository.user

import es.wokis.data.bo.user.UserBO
import es.wokis.data.datasource.user.UserLocalDataSource
import es.wokis.data.dto.user.auth.LoginDTO
import es.wokis.data.dto.user.auth.RegisterDTO
import es.wokis.data.mapper.user.toBO
import es.wokis.plugins.makeToken
import org.mindrot.jbcrypt.BCrypt

interface UserRepository {
    suspend fun login(login: LoginDTO): String?
    suspend fun register(register: RegisterDTO): String?
    suspend fun getUsers(): List<UserBO>
    suspend fun getUserById(id: String?): UserBO?
    suspend fun getUserByUsername(name: String?): UserBO?
    suspend fun getUserByEmail(email: String?): UserBO?
}

class UserRepositoryImpl(private val userLocalDataSource: UserLocalDataSource) : UserRepository {
    override suspend fun login(login: LoginDTO): String? {
        val user = userLocalDataSource.getUserByUsernameOrEmail(login.username)
        return user?.let {
            if (BCrypt.checkpw(login.password, it.password)) {
                makeToken(it)

            } else {
                null
            }
        }
    }

    override suspend fun register(register: RegisterDTO): String? {
        val currentUser = userLocalDataSource.getUserByUsernameOrEmail(register.username, register.email)
        val user = register.toBO()
        return if (currentUser == null) {
            val wasRegistered = userLocalDataSource.createUser(user)
            if (wasRegistered) {
                makeToken(user)

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
}