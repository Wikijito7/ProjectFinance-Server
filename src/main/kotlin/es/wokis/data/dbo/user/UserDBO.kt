package es.wokis.data.dbo.user

import es.wokis.data.constants.ServerConstants
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

data class UserDBO(
    @BsonId
    val id: Id<UserDBO>? = null,
    val username: String,
    val email: String,
    val password: String,
    val lang: String,
    val image: String = ServerConstants.EMPTY_TEXT,
    val devices: List<String> = emptyList()
)