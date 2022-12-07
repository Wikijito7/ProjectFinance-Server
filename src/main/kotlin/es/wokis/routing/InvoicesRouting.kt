package es.wokis.routing

import es.wokis.data.dto.invoice.InvoiceDTO
import es.wokis.data.mapper.invoice.toBO
import es.wokis.data.repository.invoices.InvoiceRepository
import es.wokis.utils.user
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.setUpInvoicesRouting() {
    val invoiceRepository by inject<InvoiceRepository>()

    authenticate {
        get {
            val user = call.user
            user?.id?.let {
                val invoices = invoiceRepository.getInvoicesOfUser(it)
                call.respond(HttpStatusCode.OK, invoices)
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }

        post {
            val user = call.user
            val invoices = call.receive<List<InvoiceDTO>>()
            user?.id?.let {
                val userInvoices = invoiceRepository.addInvoices(it, invoices.toBO())
                call.respond(HttpStatusCode.OK, userInvoices)
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }

        put {
            val user = call.user
            val invoices = call.receive<List<InvoiceDTO>>()
            user?.id?.let {
                val userInvoices = invoiceRepository.updateInvoices(it, invoices.toBO())
                call.respond(HttpStatusCode.OK, userInvoices)
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }

        delete {
            val user = call.user
            val invoices = call.receive<List<InvoiceDTO>>()
            user?.id?.let {
                val userInvoices = invoiceRepository.deleteInvoices(it, invoices.toBO())
                call.respond(HttpStatusCode.OK, userInvoices)
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }
    }

}