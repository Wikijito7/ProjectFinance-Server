package es.wokis.data.mapper.user

import es.wokis.data.bo.user.UserBO
import es.wokis.data.constants.ServerConstants.EMPTY_TEXT
import es.wokis.data.dbo.user.UserDBO
import es.wokis.data.dto.user.UserDTO
import es.wokis.data.dto.user.auth.LoginDTO
import es.wokis.data.dto.user.auth.RegisterDTO
import org.bson.types.ObjectId
import org.litote.kmongo.id.toId
import org.mindrot.jbcrypt.BCrypt

fun RegisterDTO.toBO() = UserBO(
    username = username,
    email = email,
    password = BCrypt.hashpw(password, BCrypt.gensalt()),
)

fun RegisterDTO.toLoginDTO() = LoginDTO(
    username = username,
    password = password
)

fun UserDTO.toBO() = UserBO(
    id = id,
    username = username,
    email = email,
    password = EMPTY_TEXT,
    image = image,
    devices = devices
)

fun UserBO.toDBO() = UserDBO(
    id = id?.let { ObjectId(it).toId() },
    username = username,
    email = email,
    password = password.orEmpty(),
    devices = devices
)

fun UserDBO.toBO() = UserBO(
    id = id.toString(),
    username = username,
    email = email,
    password = password,
    devices = devices
)

fun List<UserBO>?.toDTO() = this?.map { it.toDTO() }.orEmpty()

fun UserBO.toDTO() = UserDTO(
    id = id.orEmpty(),
    username = username,
    email = email,
    image = image,
    devices = devices
)