package es.wokis.data.mapper.user

import es.wokis.data.bo.user.BadgeBO
import es.wokis.data.bo.user.UpdateUserBO
import es.wokis.data.bo.user.UserBO
import es.wokis.data.constants.ServerConstants.EMPTY_TEXT
import es.wokis.data.dbo.user.BadgeDBO
import es.wokis.data.dbo.user.UserDBO
import es.wokis.data.dto.user.BadgeDTO
import es.wokis.data.dto.user.UserDTO
import es.wokis.data.dto.user.auth.LoginDTO
import es.wokis.data.dto.user.auth.RegisterDTO
import es.wokis.data.dto.user.update.UpdateUserDTO
import org.bson.types.ObjectId
import org.litote.kmongo.id.toId
import org.mindrot.jbcrypt.BCrypt

fun RegisterDTO.toBO() = UserBO(
    username = username,
    email = email,
    lang = lang,
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
    lang = lang,
    devices = devices,
    badges = badges.toBO(),
    createdOn = createdOn,
    emailVerified = emailVerified
)

fun UserBO.toDBO() = UserDBO(
    id = id?.let { ObjectId(it).toId() },
    username = username,
    email = email,
    password = password,
    lang = lang,
    image = image,
    devices = devices,
    badges = badges.toDBO(),
    sessions = sessions,
    createdOn = createdOn,
    emailVerified = emailVerified
)

fun UserDBO.toBO() = UserBO(
    id = id.toString(),
    username = username,
    email = email,
    password = password,
    image = image,
    lang = lang,
    devices = devices,
    sessions = sessions,
    badges = badges.toBO(),
    createdOn = createdOn,
    emailVerified = emailVerified
)

fun List<UserBO>?.toDTO() = this?.map { it.toDTO() }.orEmpty()

fun UserBO.toDTO() = UserDTO(
    id = id.orEmpty(),
    username = username,
    email = email,
    image = image,
    lang = lang,
    devices = devices,
    badges = badges.toDTO(),
    createdOn = createdOn,
    emailVerified = emailVerified
)

@JvmName("toBOBadgeDTO")
fun List<BadgeDTO>.toBO() = this.map { it.toBO() }

fun List<BadgeDBO>.toBO() = this.map { it.toBO() }

fun List<BadgeBO>.toDBO() = this.map { it.toDBO() }

@JvmName("toDTOBadgeBO")
fun List<BadgeBO>.toDTO() = this.map { it.toDTO() }

fun BadgeDTO.toBO() = BadgeBO(
    id = id,
    color = color
)

fun BadgeDBO.toBO() = BadgeBO(
    id = id,
    color = color
)

fun BadgeBO.toDBO() = BadgeDBO(
    id = id,
    color = color
)

fun BadgeBO.toDTO() = BadgeDTO(
    id = id,
    color = color
)

fun UpdateUserDTO.toBO() = UpdateUserBO(
    username = username,
    email = email
)