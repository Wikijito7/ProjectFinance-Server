package es.wokis.data.mapper.invoice

import es.wokis.data.bo.invoice.CategoryBO
import es.wokis.data.bo.invoice.InvoiceBO
import es.wokis.data.bo.invoice.InvoiceType
import es.wokis.data.bo.invoice.ReactionBO
import es.wokis.data.dto.invoice.CategoryDTO
import es.wokis.data.dto.invoice.InvoiceDTO
import es.wokis.data.dto.invoice.ReactionDTO
import java.util.*

fun InvoiceDTO.toBO() = InvoiceBO(
    id = id,
    idApp = idApp,
    title = title,
    description = description,
    quantity = quantity,
    date = Date(date),
    type = InvoiceType.valueOf(type.orEmpty()),
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

fun ReactionDTO.toBO() = ReactionBO(
    id = id,
    unicode = unicode
)