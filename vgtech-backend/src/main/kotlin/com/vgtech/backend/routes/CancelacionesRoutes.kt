package com.vgtech.backend.routes

import com.vgtech.backend.models.SolicitudesCancelacion
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

@Serializable
data class CancelacionDto(
    val id: String,
    val proyectoId: String,
    val consultorUid: String,
    val motivo: String,
    val detalles: String,
    val fecha: Long,
    val estado: String
)

@Serializable
data class CreateCancelacionRequest(
    val proyectoId: String,
    val consultorUid: String,
    val motivo: String,
    val detalles: String = ""
)

fun Route.cancelacionesRoutes() {
    route("/cancelaciones") {
        get {
            val list = transaction {
                SolicitudesCancelacion.selectAll().map { row ->
                    CancelacionDto(
                        id           = row[SolicitudesCancelacion.id].toString(),
                        proyectoId   = row[SolicitudesCancelacion.proyectoId].toString(),
                        consultorUid = row[SolicitudesCancelacion.consultorUid].toString(),
                        motivo       = row[SolicitudesCancelacion.motivo],
                        detalles     = row[SolicitudesCancelacion.detalles],
                        fecha        = row[SolicitudesCancelacion.fecha].toEpochMilli(),
                        estado       = row[SolicitudesCancelacion.estado]
                    )
                }
            }
            call.respond(HttpStatusCode.OK, list)
        }

        post {
            val req = call.receive<CreateCancelacionRequest>()
            val now = Instant.now()
            val newId = UUID.randomUUID()
            transaction {
                SolicitudesCancelacion.insert {
                    it[id]           = newId
                    it[proyectoId]   = UUID.fromString(req.proyectoId)
                    it[consultorUid] = UUID.fromString(req.consultorUid)
                    it[motivo]       = req.motivo
                    it[detalles]     = req.detalles
                    it[fecha]        = now
                    it[createdAt]    = now
                    it[updatedAt]    = now
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
        }

        patch("/{id}/estado") {
            val id  = UUID.fromString(call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest))
            val req = call.receive<Map<String, String>>()
            transaction {
                SolicitudesCancelacion.update({ SolicitudesCancelacion.id eq id }) {
                    it[estado]    = req["estado"] ?: "Pendiente"
                    it[updatedAt] = Instant.now()
                }
            }
            call.respond(HttpStatusCode.OK, mapOf("message" to "Estado actualizado"))
        }
    }
}
