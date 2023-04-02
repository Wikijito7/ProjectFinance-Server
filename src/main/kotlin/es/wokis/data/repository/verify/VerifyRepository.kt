package es.wokis.data.repository.verify

import es.wokis.data.bo.verification.VerificationBO
import es.wokis.data.datasource.verify.VerifyLocalDataSource

interface VerifyRepository {
    suspend fun addVerification(verification: VerificationBO)
}

class VerifyRepositoryImpl(private val localDataSource: VerifyLocalDataSource) : VerifyRepository {

    override suspend fun addVerification(verification: VerificationBO) {
        localDataSource.addVerification(verification)
    }

}