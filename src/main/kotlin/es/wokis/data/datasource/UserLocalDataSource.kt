package es.wokis.data.datasource

import com.mongodb.client.MongoCollection
import es.wokis.data.bo.user.UserBO
import es.wokis.data.constants.ServerConstants.EMPTY_TEXT
import es.wokis.data.dbo.user.UserDBO
import es.wokis.data.mapper.user.toBO
import es.wokis.data.mapper.user.toDBO
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.toId
import java.util.regex.Pattern

interface UserLocalDataSource {
    suspend fun getAllUsers(): List<UserBO>
    suspend fun getUserById(id: String): UserBO?
    suspend fun getUserByEmail(email: String): UserBO?
    suspend fun getUserByUsername(username: String): UserBO?

    suspend fun getUserByUsernameOrEmail(username: String, email: String = EMPTY_TEXT): UserBO?

    suspend fun createUser(user: UserBO): Boolean
    suspend fun updateUser(user: UserBO): Boolean
}

class UserLocalDataSourceImpl(private val userCollection: MongoCollection<UserDBO>) : UserLocalDataSource {

    private val getCaseInsensitive: (element: String) -> Pattern = {
        Pattern.compile(it, Pattern.CASE_INSENSITIVE)
    }

    override suspend fun getAllUsers(): List<UserBO> = userCollection.find().map {
        it.toBO()
    }.toList()

    override suspend fun getUserById(id: String): UserBO? {
        val bsonId: Id<UserDBO> = ObjectId(id).toId()
        return userCollection.findOne(UserDBO::id eq bsonId)?.toBO()
    }

    override suspend fun getUserByEmail(email: String): UserBO? =
        userCollection.findOne(UserDBO::email.regex(getCaseInsensitive(email)))?.toBO()

    override suspend fun getUserByUsername(username: String): UserBO? =
        userCollection.findOne(UserDBO::username.regex(getCaseInsensitive(username)))?.toBO()

    override suspend fun getUserByUsernameOrEmail(username: String, email: String): UserBO? =
        userCollection.findOne(
            or(
                UserDBO::username.regex(getCaseInsensitive(username)),
                UserDBO::email.regex(getCaseInsensitive(email.takeIf { it.isNotBlank() } ?: username))
            )
        )?.toBO()

    override suspend fun createUser(user: UserBO): Boolean {
        return try {
            userCollection.insertOne(user.toDBO()).wasAcknowledged()

        } catch (e: Throwable) {
            println(e.stackTraceToString())
            false
        }
    }

    override suspend fun updateUser(user: UserBO): Boolean {
        val bsonId: Id<UserDBO> = ObjectId(user.id).toId()
        return userCollection.updateOne(UserDBO::id eq bsonId, user.toDBO()).wasAcknowledged()
    }
}