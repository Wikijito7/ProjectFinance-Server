package es.wokis.di

import com.mongodb.client.MongoCollection
import es.wokis.data.database.AppDataBase
import es.wokis.data.datasource.invoice.InvoiceLocalDataSource
import es.wokis.data.datasource.invoice.InvoiceLocalDataSourceImpl
import es.wokis.data.datasource.recover.RecoverLocalDataSource
import es.wokis.data.datasource.recover.RecoverLocalDataSourceImpl
import es.wokis.data.datasource.user.UserLocalDataSource
import es.wokis.data.datasource.user.UserLocalDataSourceImpl
import es.wokis.data.datasource.verify.VerifyLocalDataSource
import es.wokis.data.datasource.verify.VerifyLocalDataSourceImpl
import es.wokis.data.dbo.invoice.InvoiceDBO
import es.wokis.data.dbo.recover.RecoverDBO
import es.wokis.data.dbo.user.UserDBO
import es.wokis.data.dbo.verification.VerificationDBO
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataSourceModule = module {
    single { AppDataBase() }
    single(named("usersCollection")) { getUsersCollection(get()) as MongoCollection<UserDBO> }
    single(named("invoicesCollection")) { getInvoicesCollection(get()) as MongoCollection<InvoiceDBO> }
    single(named("verificationCollection")) { getVerificationCollection(get()) as MongoCollection<VerificationDBO> }
    single(named("recoverCollection")) { getRecoverCollection(get()) as MongoCollection<RecoverDBO> }
    single { UserLocalDataSourceImpl(get(named("usersCollection"))) as UserLocalDataSource }
    single { InvoiceLocalDataSourceImpl(get(named("invoicesCollection"))) as InvoiceLocalDataSource }
    single { VerifyLocalDataSourceImpl(get(named("verificationCollection"))) as VerifyLocalDataSource }
    single { RecoverLocalDataSourceImpl(get(named("recoverCollection"))) as RecoverLocalDataSource }
}

private fun getUsersCollection(database: AppDataBase) = database.usersCollection

private fun getInvoicesCollection(database: AppDataBase) = database.invoicesCollection

private fun getVerificationCollection(database: AppDataBase) = database.verificationCollection

private fun getRecoverCollection(database: AppDataBase) = database.recoverCollection