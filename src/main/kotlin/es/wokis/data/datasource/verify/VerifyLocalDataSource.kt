package es.wokis.data.datasource.verify

import com.mongodb.client.MongoCollection
import es.wokis.data.bo.verification.VerificationBO
import es.wokis.data.dbo.user.UserDBO
import es.wokis.data.dbo.verification.VerificationDBO

interface VerifyLocalDataSource {
    suspend fun addVerification(verification: VerificationBO)
}

class VerifyLocalDataSourceImpl(private val userCollection: MongoCollection<VerificationDBO>) : VerifyLocalDataSource {

    override suspend fun addVerification(verification: VerificationBO) {
        TODO("Not yet implemented")
    }

}