package es.wokis.data.datasource.invoice

import com.mongodb.client.MongoCollection
import es.wokis.data.bo.invoice.InvoiceBO
import es.wokis.data.dbo.invoice.InvoiceDBO
import es.wokis.data.mapper.invoice.toBO
import es.wokis.data.mapper.invoice.toDBO
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.eq
import org.litote.kmongo.id.toId
import org.litote.kmongo.updateOne
import java.util.regex.Pattern

interface InvoiceLocalDataSource {
    suspend fun getInvoicesOfUser(id: String): List<InvoiceBO>
    suspend fun addInvoices(id: String, invoices: List<InvoiceBO>): Boolean
    suspend fun updateInvoices(id: String, invoices: List<InvoiceBO>): Boolean
    suspend fun deleteInvoices(id: String, invoicesIds: List<String>): Boolean
}

class InvoiceLocalDataSourceImpl(private val invoiceCollection: MongoCollection<InvoiceDBO>) : InvoiceLocalDataSource {

    private val getCaseInsensitive: (element: String) -> Pattern = {
        Pattern.compile(it, Pattern.CASE_INSENSITIVE)
    }

    override suspend fun getInvoicesOfUser(id: String): List<InvoiceBO> =
        invoiceCollection.find(InvoiceDBO::userId eq id).map {
            it.toBO()
        }.toList()

    override suspend fun addInvoices(id: String, invoices: List<InvoiceBO>): Boolean = try {
        invoiceCollection.insertMany(invoices.toDBO()).wasAcknowledged()

    } catch (e: Throwable) {
        println(e.stackTraceToString())
        false
    }

    override suspend fun updateInvoices(id: String, invoices: List<InvoiceBO>): Boolean = try {
        invoices.toDBO().map {
            invoiceCollection.updateOne(InvoiceDBO::id eq it.id, it).wasAcknowledged()
        }.all { it }

    } catch (e: Throwable) {
        println(e.stackTraceToString())
        false
    }

    override suspend fun deleteInvoices(id: String, invoicesIds: List<String>): Boolean = try {
        invoicesIds.map {
            val bsonId: Id<InvoiceDBO> = ObjectId(it).toId()
            invoiceCollection.deleteOne(InvoiceDBO::id eq bsonId).wasAcknowledged()
        }.all { it }

    } catch (e: Throwable) {
        println(e.stackTraceToString())
        false
    }

}