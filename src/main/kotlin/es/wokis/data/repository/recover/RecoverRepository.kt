package es.wokis.data.repository.recover

import es.wokis.data.bo.recover.RecoverBO
import es.wokis.data.bo.response.AcknowledgeBO
import es.wokis.data.datasource.recover.RecoverLocalDataSource
import es.wokis.data.dto.user.auth.ChangePassRequestDTO
import es.wokis.data.exception.RecoverCodeNotFoundException
import es.wokis.data.exception.UserNotFoundException
import es.wokis.data.repository.user.UserRepository
import es.wokis.services.EmailService

interface RecoverRepository {
    suspend fun changeUserPassword(changePassRequest: ChangePassRequestDTO): AcknowledgeBO
    suspend fun requestChangePass(email: String): AcknowledgeBO
}

class RecoverRepositoryImpl(
    private val localDataSource: RecoverLocalDataSource,
    private val userRepository: UserRepository,
    private val emailService: EmailService
) : RecoverRepository {

    override suspend fun changeUserPassword(changePassRequest: ChangePassRequestDTO): AcknowledgeBO {
        if (changePassRequest.recoverCode == null) {
            throw RecoverCodeNotFoundException
        }
        val recover = localDataSource.getRecoverByToken(changePassRequest.recoverCode)
        recover?.let {
            val user = userRepository.getUserByEmail(it.email)
            return user?.let {
                userRepository.updateUser(user.copy(password = changePassRequest.newPass, sessions = listOf()))
            } ?: throw UserNotFoundException
        }
        throw RecoverCodeNotFoundException
    }

    override suspend fun requestChangePass(email: String): AcknowledgeBO {
        val user = userRepository.getUserByEmail(email)
        user?.let {
            emailService.sendRecoverPass(user)?.also {
                return saveRequestChangePass(it)
            } ?: throw IllegalStateException()
        }
        throw UserNotFoundException
    }

    private suspend fun saveRequestChangePass(recoverRequest: RecoverBO): AcknowledgeBO {
        return AcknowledgeBO(localDataSource.saveRecoverRequest(recoverRequest))
    }

}