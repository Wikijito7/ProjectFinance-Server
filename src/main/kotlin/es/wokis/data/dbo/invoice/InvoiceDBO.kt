package es.wokis.data.dbo.invoice

import es.wokis.data.constants.ServerConstants
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

data class InvoiceDBO(
    @BsonId
    val id: Id<InvoiceDBO>? = null,
    val idApp: Long = 0L,
    val title: String = ServerConstants.EMPTY_TEXT,
    val description: String = ServerConstants.EMPTY_TEXT,
    val quantity: Int = 0,
    val date: Long = 0L,
    val type: String? = null,
    val userId: String? = null,
    val category: CategoryDBO? = null,
    val reactions: List<ReactionDBO> = emptyList()
)

data class CategoryDBO(
    val id: Long,
    val title: String,
    val color: String,
)

data class ReactionDBO(
    val id: Long,
    val unicode: String
)