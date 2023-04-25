package es.wokis.data.repository.invoices

import es.wokis.data.bo.invoice.InvoiceBO
import es.wokis.data.bo.response.AcknowledgeBO
import es.wokis.data.datasource.invoice.InvoiceLocalDataSource

interface InvoiceRepository {
    suspend fun getInvoicesOfUser(id: String): List<InvoiceBO>
    suspend fun addInvoices(id: String, invoices: List<InvoiceBO>): AcknowledgeBO
    suspend fun updateInvoices(id: String, invoices: List<InvoiceBO>): AcknowledgeBO
    suspend fun deleteInvoices(id: String, invoicesIds: List<String>): AcknowledgeBO
}

class InvoiceRepositoryImpl(private val invoiceLocalDataSource: InvoiceLocalDataSource) : InvoiceRepository {
    override suspend fun getInvoicesOfUser(id: String): List<InvoiceBO> =
        invoiceLocalDataSource.getInvoicesOfUser(id)

    override suspend fun addInvoices(id: String, invoices: List<InvoiceBO>): AcknowledgeBO = AcknowledgeBO(
        invoiceLocalDataSource.addInvoices(id, invoices)
    )

    override suspend fun updateInvoices(id: String, invoices: List<InvoiceBO>): AcknowledgeBO = AcknowledgeBO(
        invoiceLocalDataSource.updateInvoices(id, invoices)
    )

    override suspend fun deleteInvoices(id: String, invoicesIds: List<String>): AcknowledgeBO = AcknowledgeBO(
        invoiceLocalDataSource.deleteInvoices(id, invoicesIds)
    )


}