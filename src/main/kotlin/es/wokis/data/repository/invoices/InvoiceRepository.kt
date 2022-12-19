package es.wokis.data.repository.invoices

import es.wokis.data.bo.invoice.InvoiceBO
import es.wokis.data.datasource.invoice.InvoiceLocalDataSource

interface InvoiceRepository {
    suspend fun getInvoicesOfUser(id: String): List<InvoiceBO>
    suspend fun addInvoices(id: String, invoices: List<InvoiceBO>): Boolean
    suspend fun updateInvoices(id: String, invoices: List<InvoiceBO>): Boolean
    suspend fun deleteInvoices(id: String, invoices: List<InvoiceBO>): Boolean
}

class InvoiceRepositoryImpl(private val invoiceLocalDataSource: InvoiceLocalDataSource) : InvoiceRepository {
    override suspend fun getInvoicesOfUser(id: String): List<InvoiceBO> =
        invoiceLocalDataSource.getInvoicesOfUser(id)

    override suspend fun addInvoices(id: String, invoices: List<InvoiceBO>): Boolean =
        invoiceLocalDataSource.addInvoices(id, invoices)

    override suspend fun updateInvoices(id: String, invoices: List<InvoiceBO>): Boolean =
        invoiceLocalDataSource.updateInvoices(id, invoices)

    override suspend fun deleteInvoices(id: String, invoices: List<InvoiceBO>): Boolean =
        invoiceLocalDataSource.deleteInvoices(id, invoices)
}