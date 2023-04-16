package es.wokis.di

import es.wokis.data.repository.invoices.InvoiceRepository
import es.wokis.data.repository.invoices.InvoiceRepositoryImpl
import es.wokis.data.repository.user.UserRepository
import es.wokis.data.repository.user.UserRepositoryImpl
import es.wokis.data.repository.verify.VerifyRepository
import es.wokis.data.repository.verify.VerifyRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single { UserRepositoryImpl(get()) as UserRepository }
    single { InvoiceRepositoryImpl(get()) as InvoiceRepository }
    single { VerifyRepositoryImpl(get()) as VerifyRepository }
}