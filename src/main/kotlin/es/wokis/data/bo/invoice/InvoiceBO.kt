package es.wokis.data.bo.invoice

import es.wokis.data.constants.ServerConstants.EMPTY_TEXT
import java.util.*

data class InvoiceBO(
    val id: String? = null,
    val idApp: Long = 0L,
    val title: String = EMPTY_TEXT,
    val description: String = EMPTY_TEXT,
    val quantity: Int = 0,
    val date: Date = Date(),
    val type: InvoiceType,
    val userId: String,
    val category: CategoryBO? = null,
    val reactions: List<ReactionBO> = emptyList()
)

data class CategoryBO(
    val id: Long,
    val title: String,
    val color: String,
)

data class ReactionBO(
    val id: Long,
    val unicode: String
)

enum class InvoiceType(val key: String) {
    DEPOSIT("DEPOSIT"),
    EXPENSE("EXPENSE");

    companion object {
        fun getFromKey(key: String) = values().find { it.key == key } ?: DEPOSIT
    }
}
