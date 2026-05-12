package com.vgtech.backend.routes

import com.vgtech.backend.models.RegistroHoras
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
data class RegistroHorasDto(
    val id: String,
    val empleadoUid: String,
    val fecha: String,
    val horasTrabajadas: Double,
    val horasExtra: Double,
    val tarifaExtra: Double,
    val tarifaHora: Double,
    val pagoTotal: Double,
    val observaciones: String
)

@Serializable
data class CreateRegistroHorasRequest(
    val empleadoUid: String,
    val fecha: String,       // "YYYY-MM-DD"
    val horasTrabajadas: Double = 0.0,
    val horasExtra: Double = 0.0,
    val tarifaExtra: Double = 2.0,
    val tarifaHora: Double = 0.0,
    val observaciones: String = ""
)

fun Route.registroHorasRoutes() {
    route("/registro-horas") {

        // GET /registro-horas?empleadoUid=xxx
        get {
            val empFilter = call.request.queryParameters["empleadoUid"]
            val list = transaction {
                val q = if (empFilter != null)
                    RegistroHoras.select { RegistroHoras.empleadoUid eq UUID.fromString(empFilter) }
                else
                    RegistroHoras.selectAll()
                q.orderBy(RegistroHoras.fecha, SortOrder.DESC).map { row ->
                    RegistroHorasDto(
                        id              = row[RegistroHoras.id].toString(),
                        empleadoUid     = row[RegistroHoras.empleadoUid].toString(),
                        fecha           = row[RegistroHoras.fecha].toString(),
                        horasTrabajadas = row[RegistroHoras.horasTrabajadas].toDouble(),
                        horasExtra      = row[RegistroHoras.horasExtra].toDouble(),
                        tarifaExtra     = row[RegistroHoras.tarifaExtra].toDouble(),
                        tarifaHora      = row[RegistroHoras.tarifaHora].toDouble(),
                        pagoTotal       = row[RegistroHoras.pagoTotal].toDouble(),
                        observaciones   = row[RegistroHoras.observaciones]
                    )
                }
            }
            call.respond(HttpStatusCode.OK, list)
        }

        // POST /registro-horas
        post {
            val req = call.receive<CreateRegistroHorasRequest>()
            val totalCalculado = (req.horasTrabajadas * req.tarifaHora) + (req.horasExtra * req.tarifaHora * req.tarifaExtra)
            val now = Instant.now()
            val newId = UUID.randomUUID()
            transaction {
                RegistroHoras.insert {
                    it[id]              = newId
                    it[empleadoUid]     = UUID.fromString(req.empleadoUid)
                    it[fecha]           = java.time.LocalDate.parse(req.fecha)
                    it[horasTrabajadas] = BigDecimal.valueOf(req.horasTrabajadas)
                    it[horasExtra]      = BigDecimal.valueOf(req.horasExtra)
                    it[tarifaExtra]     = BigDecimal.valueOf(req.tarifaExtra)
                    it[tarifaHora]      = BigDecimal.valueOf(req.tarifaHora)
                    it[pagoTotal]       = BigDecimal.valueOf(totalCalculado)
                    it[observaciones]   = req.observaciones
                    it[createdAt]       = now
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
        }
    }
}
