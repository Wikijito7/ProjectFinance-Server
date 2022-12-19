package es.wokis.data.mapper.invoice

import es.wokis.data.bo.invoice.CategoryBO
import es.wokis.data.bo.invoice.InvoiceBO
import es.wokis.data.bo.invoice.InvoiceType
import es.wokis.data.bo.invoice.ReactionBO
import es.wokis.data.dbo.invoice.CategoryDBO
import es.wokis.data.dbo.invoice.InvoiceDBO
import es.wokis.data.dbo.invoice.ReactionDBO
import es.wokis.data.dto.invoice.CategoryDTO
import es.wokis.data.dto.invoice.InvoiceDTO
import es.wokis.data.dto.invoice.ReactionDTO
import org.bson.types.ObjectId
import org.litote.kmongo.id.toId
import java.util.*

fun InvoiceDTO.toBO() = InvoiceBO(
    id = id,
    idApp = idApp,
    title = title,
    description = description,
    quantity = quantity,
    date = Date(date),
    type = InvoiceType.getFromKey(type.orEmpty()),
    userId = userId.orEmpty(),
    category = category?.toBO(),
    reactions = reactions.toBO()
)

fun CategoryDTO.toBO() = CategoryBO(
    id = id,
    title = title,
    color = color
)

fun List<ReactionDTO>?.toBO() = this?.map { it.toBO() }.orEmpty()

@JvmName("invoiceDTOToBO")
fun List<InvoiceDTO>.toBO() = this.map { it.toBO() }

fun ReactionDTO.toBO() = ReactionBO(
    id = id,
    unicode = unicode
)

fun InvoiceDBO.toBO() = InvoiceBO(
    id = id.toString(),
    idApp = idApp,
    title = title,
    description = description,
    quantity = quantity,
    date = Date(date),
    type = InvoiceType.getFromKey(type.orEmpty()),
    userId = userId.orEmpty(),
    category = category?.toBO(),
    reactions = reactions.toBO()
)

fun CategoryDBO.toBO() = CategoryBO(
    id = id,
    title = title,
    color = color
)

@JvmName("reactionDBOToBO")
fun List<ReactionDBO>?.toBO() = this?.map { it.toBO() }.orEmpty()

fun ReactionDBO.toBO() = ReactionBO(
    id = id,
    unicode = unicode
)

fun InvoiceBO.toDBO() = InvoiceDBO(
    id = id?.let { ObjectId(it).toId() },
    idApp = idApp,
    title = title,
    description = description,
    quantity = quantity,
    date = date.time,
    type = type.key,
    userId = userId,
    category = category?.toDBO(),
    reactions = reactions.toDBO()
)

fun CategoryBO.toDBO() = CategoryDBO(
    id = id,
    title = title,
    color = color
)

@JvmName("reactionBOToDBO")
fun List<ReactionBO>?.toDBO() = this?.map { it.toDBO() }.orEmpty()

@JvmName("invoicesBOToDBO")
fun List<InvoiceBO>?.toDBO(): List<InvoiceDBO> = this?.map { it.toDBO() }.orEmpty()

fun ReactionBO.toDBO() = ReactionDBO(
    id = id,
    unicode = unicode
)

fun List<InvoiceBO>.toDTO(): List<InvoiceDTO> = this.map { it.toDTO() }

fun InvoiceBO.toDTO() = InvoiceDTO(
    id = id.toString(),
    idApp = idApp,
    title = title,
    description = description,
    quantity = quantity,
    date = date.time,
    type = type.key,
    userId = userId,
    category = category?.toDTO(),
    reactions = reactions.toDTO()
)

fun CategoryBO.toDTO() = CategoryDTO(
    id = id,
    title = title,
    color = color
)

@JvmName("reactionBOToDTO")
fun List<ReactionBO>.toDTO(): List<ReactionDTO> = this.map { it.toDTO() }

fun ReactionBO.toDTO() = ReactionDTO(
    id = id,
    unicode = unicode
)