package com.vgtech.backend

import com.vgtech.backend.plugins.configureRouting
import com.vgtech.backend.plugins.configureSerialization
import com.vgtech.backend.plugins.configureCORS
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    // Inicializar base de datos
    DatabaseFactory.init()

    // Plugins
    install(CallLogging) {
        level = Level.INFO
    }
    configureSerialization()
    configureCORS()
    configureRouting()
}
