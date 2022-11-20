package es.wokis.plugins

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

private val configFile = File("config/", "app.conf")
lateinit var config: Config

fun Application.initConfig() {
    // si no existe la config interna, tira una excepción
    val internalConf = this::class.java.getResourceAsStream("/app.conf") ?: throw IllegalAccessException()

    if (!configFile.exists()) {
        configFile.mkdirs()
        Files.copy(internalConf, Paths.get(configFile.path), StandardCopyOption.REPLACE_EXISTING)
    }

    config = ConfigFactory.parseFile(File("config/app.conf"))
}
