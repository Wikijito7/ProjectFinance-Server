package es.wokis.data.repository.invoices

import es.wokis.data.bo.invoice.InvoiceBO
import es.wokis.data.datasource.user.UserLocalDataSource

interface InvoiceRepository {
    suspend fun getInvoicesOfUser(id: String): List<InvoiceBO>
    suspend fun addInvoices(id: String, invoices: List<InvoiceBO>): Boolean
    suspend fun updateInvoices(id: String, invoices: List<InvoiceBO>): Boolean
    suspend fun deleteInvoices(id: String, invoices: List<InvoiceBO>): Boolean
}

class InvoiceRepositoryImpl(private val userLocalDataSource: UserLocalDataSource) : InvoiceRepository {
    override suspend fun getInvoicesOfUser(id: String): List<InvoiceBO> {
        TODO("Not yet implemented")
    }

    override suspend fun addInvoices(id: String, invoices: List<InvoiceBO>): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateInvoices(id: String, invoices: List<InvoiceBO>): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteInvoices(id: String, invoices: List<InvoiceBO>): Boolean {
        TODO("Not yet implemented")
    }
}