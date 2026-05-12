package com.vgtech.backend.plugins

import com.vgtech.backend.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("🚀 VG Tech Backend activo — PostgreSQL conectado")
        }
        authRoutes()
        usuariosRoutes()
        proyectosRoutes()
        vacacionesRoutes()
        registroHorasRoutes()
        progresoRoutes()
        invitacionesRoutes()
        cotizacionesRoutes()
        evaluacionesRoutes()
        fasesPagoRoutes()
        transaccionesRoutes()
        mensajesRoutes()
        cancelacionesRoutes()
    }
}
