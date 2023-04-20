package es.wokis.data.enums

import es.wokis.data.bo.user.BadgeBO

enum class BadgesEnum(val id: Int, val color: String) {
    VERIFIED(1, "#01B05B"),
    DEBUG(2, "#AA00FF"),
    CADOX_DEBUG(2, "#FFAE42"),
    WOKIS_DEBUG(2, "#FFAE42"),
    TONY_DEBUG(2, "#AC1519"),
    OG(3, "#005689");
}

fun BadgesEnum.toBadge(): BadgeBO = BadgeBO(
    id = id,
    color = color
)