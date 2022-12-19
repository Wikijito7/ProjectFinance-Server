package es.wokis.di

import com.mongodb.client.MongoCollection
import es.wokis.data.database.AppDataBase
import es.wokis.data.datasource.invoice.InvoiceLocalDataSource
import es.wokis.data.datasource.invoice.InvoiceLocalDataSourceImpl
import es.wokis.data.datasource.user.UserLocalDataSource
import es.wokis.data.datasource.user.UserLocalDataSourceImpl
import es.wokis.data.dbo.invoice.InvoiceDBO
import es.wokis.data.dbo.user.UserDBO
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataSourceModule = module {
    single { AppDataBase() }
    single(named("usersCollection")) { getUsersCollection(get()) as MongoCollection<UserDBO> }
    single(named("invoicesCollection")) { getInvoicesCollection(get()) as MongoCollection<InvoiceDBO> }
    single { UserLocalDataSourceImpl(get(named("usersCollection"))) as UserLocalDataSource }
    single { InvoiceLocalDataSourceImpl(get(named("invoicesCollection"))) as InvoiceLocalDataSource }
}

private fun getUsersCollection(database: AppDataBase) = database.usersCollection

private fun getInvoicesCollection(database: AppDataBase) = database.invoicesCollection