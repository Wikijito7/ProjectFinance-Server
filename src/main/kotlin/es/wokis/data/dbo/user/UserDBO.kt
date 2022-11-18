package es.wokis.data.dbo.user

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

data class UserDBO(
    @BsonId
    val id: Id<UserDBO>? = null,
    val username: String,
    val email: String,
    val password: String,
    val devices: List<String> = emptyList()
)