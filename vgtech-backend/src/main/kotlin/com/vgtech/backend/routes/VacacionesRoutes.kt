package com.vgtech.backend.routes

import com.vgtech.backend.models.SolicitudesVacaciones
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Serializable
data class VacacionDto(
    val id: String,
    val empleadoUid: String,
    val fechaInicio: String,
    val fechaFin: String,
    val diasSolicitados: Double,
    val horasSolicitadas: Double,
    val tipo: String,
    val estado: String,
    val fechaSolicitud: Long,
    val observaciones: String
)

@Serializable
data class CreateVacacionRequest(
    val empleadoUid: String,
    val fechaInicio: String,      // "YYYY-MM-DD"
    val fechaFin: String,
    val diasSolicitados: Double = 0.0,
    val horasSolicitadas: Double = 0.0,
    val tipo: String = "VACACIONES",
    val observaciones: String = ""
)

@Serializable
data class UpdateVacacionEstadoRequest(val estado: String)

fun Route.vacacionesRoutes() {
    route("/vacaciones") {

        // GET /vacaciones?empleadoUid=xxx
        get {
            val empFilter = call.request.queryParameters["empleadoUid"]
            val list = transaction {
                val q = if (empFilter != null)
                    SolicitudesVacaciones.select { SolicitudesVacaciones.empleadoUid eq UUID.fromString(empFilter) }
                else
                    SolicitudesVacaciones.selectAll()
                q.map { row ->
                    VacacionDto(
                        id               = row[SolicitudesVacaciones.id].toString(),
                        empleadoUid      = row[SolicitudesVacaciones.empleadoUid].toString(),
                        fechaInicio      = row[SolicitudesVacaciones.fechaInicio],
                        fechaFin         = row[SolicitudesVacaciones.fechaFin],
                        diasSolicitados  = row[SolicitudesVacaciones.diasSolicitados].toDouble(),
                        horasSolicitadas = row[SolicitudesVacaciones.horasSolicitadas].toDouble(),
                        tipo             = row[SolicitudesVacaciones.tipo],
                        estado           = row[SolicitudesVacaciones.estado],
                        fechaSolicitud   = row[SolicitudesVacaciones.fechaSolicitud].toEpochMilli(),
                        observaciones    = row[SolicitudesVacaciones.observaciones]
                    )
                }
            }
            call.respond(HttpStatusCode.OK, list)
        }

        // POST /vacaciones
        post {
            val req = call.receive<CreateVacacionRequest>()
            val now = Instant.now()
            val newId = UUID.randomUUID()
            transaction {
                SolicitudesVacaciones.insert {
                    it[id]               = newId
                    it[empleadoUid]      = UUID.fromString(req.empleadoUid)
                    it[fechaInicio]      = req.fechaInicio
                    it[fechaFin]         = req.fechaFin
                    it[diasSolicitados]  = BigDecimal.valueOf(req.diasSolicitados)
                    it[horasSolicitadas] = BigDecimal.valueOf(req.horasSolicitadas)
                    it[tipo]             = req.tipo
                    it[observaciones]    = req.observaciones
                    it[fechaSolicitud]   = now
                    it[createdAt]        = now
                    it[updatedAt]        = now
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
        }

        // PATCH /vacaciones/{id}/estado
        patch("/{id}/estado") {
            val id  = UUID.fromString(call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest))
            val req = call.receive<UpdateVacacionEstadoRequest>()
            transaction {
                SolicitudesVacaciones.update({ SolicitudesVacaciones.id eq id }) {
                    it[estado]    = req.estado
                    it[updatedAt] = Instant.now()
                }
            }
            call.respond(HttpStatusCode.OK, mapOf("message" to "Estado actualizado"))
        }
    }
}
