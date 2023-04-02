package es.wokis.utils

import java.io.File

fun String.normalizeUrl() = File(this).normalize().path.replace("\\", "/")