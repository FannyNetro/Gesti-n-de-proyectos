package com.vgtech.backend.routes

import com.vgtech.backend.models.FasesPago
import com.vgtech.backend.models.TransaccionesProveedor
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
data class FasePagoDto(
    val id: String,
    val proveedorId: String,
    val proyectoId: String,
    val numeroFase: Int,
    val totalFases: Int,
    val montoAPagar: Double,
    val fechaProgramada: Long,
    val estado: String,
    val fechaPago: Long?
)

@Serializable
data class CreateFasePagoRequest(
    val proveedorId: String,
    val proyectoId: String,
    val numeroFase: Int,
    val totalFases: Int,
    val montoAPagar: Double,
    val fechaProgramada: Long
)

@Serializable
data class TransaccionDto(
    val id: String,
    val proveedorId: String,
    val proyectoId: String?,
    val faseId: String?,
    val timestamp: Long,
    val tipo: String,
    val montoBruto: Double,
    val corteEmpresa: Double,
    val corteProveedor: Double,
    val descripcion: String
)

@Serializable
data class CreateTransaccionRequest(
    val proveedorId: String,
    val proyectoId: String? = null,
    val faseId: String? = null,
    val tipo: String,
    val montoBruto: Double,
    val corteEmpresa: Double = 0.0,
    val corteProveedor: Double = 0.0,
    val descripcion: String = ""
)

fun Route.fasesPagoRoutes() {
    route("/fases-pago") {

        // GET /fases-pago?proveedorId=xxx
        get {
            val provFilter = call.request.queryParameters["proveedorId"]
            val list = transaction {
                val q = if (provFilter != null)
                    FasesPago.select { FasesPago.proveedorId eq UUID.fromString(provFilter) }
                else
                    FasesPago.selectAll()
                q.map { row ->
                    FasePagoDto(
                        id              = row[FasesPago.id].toString(),
                        proveedorId     = row[FasesPago.proveedorId].toString(),
                        proyectoId      = row[FasesPago.proyectoId].toString(),
                        numeroFase      = row[FasesPago.numeroFase].toInt(),
                        totalFases      = row[FasesPago.totalFases].toInt(),
                        montoAPagar     = row[FasesPago.montoAPagar].toDouble(),
                        fechaProgramada = row[FasesPago.fechaProgramada].toEpochMilli(),
                        estado          = row[FasesPago.estado],
                        fechaPago       = row[FasesPago.fechaPago]?.toEpochMilli()
                    )
                }
            }
            call.respond(HttpStatusCode.OK, list)
        }

        // POST /fases-pago
        post {
            val req = call.receive<CreateFasePagoRequest>()
            val now = Instant.now()
            val newId = UUID.randomUUID()
            transaction {
                FasesPago.insert {
                    it[id]              = newId
                    it[proveedorId]     = UUID.fromString(req.proveedorId)
                    it[proyectoId]      = UUID.fromString(req.proyectoId)
                    it[numeroFase]      = req.numeroFase.toShort()
                    it[totalFases]      = req.totalFases.toShort()
                    it[montoAPagar]     = BigDecimal.valueOf(req.montoAPagar)
                    it[fechaProgramada] = Instant.ofEpochMilli(req.fechaProgramada)
                    it[createdAt]       = now
                    it[updatedAt]       = now
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
        }

        // PATCH /fases-pago/{id}/pagar
        patch("/{id}/pagar") {
            val id = UUID.fromString(call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest))
            val now = Instant.now()
            transaction {
                FasesPago.update({ FasesPago.id eq id }) {
                    it[estado]    = "PAGADO"
                    it[fechaPago] = now
                    it[updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.OK, mapOf("message" to "Fase marcada como pagada"))
        }

        // DELETE /fases-pago/{id}
        delete("/{id}") {
            val faseId = UUID.fromString(call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest))
            transaction { exec("DELETE FROM fases_pago WHERE id = '$faseId'") }
            call.respond(HttpStatusCode.OK, mapOf("message" to "Fase eliminada"))
        }
    }
}

fun Route.transaccionesRoutes() {
    route("/transacciones") {

        // GET /transacciones?proveedorId=xxx
        get {
            val provFilter = call.request.queryParameters["proveedorId"]
            val list = transaction {
                val q = if (provFilter != null)
                    TransaccionesProveedor.select { TransaccionesProveedor.proveedorId eq UUID.fromString(provFilter) }
                else
                    TransaccionesProveedor.selectAll()
                q.orderBy(TransaccionesProveedor.timestamp, SortOrder.DESC).map { row ->
                    TransaccionDto(
                        id             = row[TransaccionesProveedor.id].toString(),
                        proveedorId    = row[TransaccionesProveedor.proveedorId].toString(),
                        proyectoId     = row[TransaccionesProveedor.proyectoId]?.toString(),
                        faseId         = row[TransaccionesProveedor.faseId]?.toString(),
                        timestamp      = row[TransaccionesProveedor.timestamp].toEpochMilli(),
                        tipo           = row[TransaccionesProveedor.tipo],
                        montoBruto     = row[TransaccionesProveedor.montoBruto].toDouble(),
                        corteEmpresa   = row[TransaccionesProveedor.corteEmpresa].toDouble(),
                        corteProveedor = row[TransaccionesProveedor.corteProveedor].toDouble(),
                        descripcion    = row[TransaccionesProveedor.descripcion]
                    )
                }
            }
            call.respond(HttpStatusCode.OK, list)
        }

        // POST /transacciones
        post {
            val req = call.receive<CreateTransaccionRequest>()
            val now = Instant.now()
            val newId = UUID.randomUUID()
            transaction {
                TransaccionesProveedor.insert {
                    it[id]             = newId
                    it[proveedorId]    = UUID.fromString(req.proveedorId)
                    it[proyectoId]     = req.proyectoId?.let { u -> UUID.fromString(u) }
                    it[faseId]         = req.faseId?.let { u -> UUID.fromString(u) }
                    it[timestamp]      = now
                    it[tipo]           = req.tipo
                    it[montoBruto]     = BigDecimal.valueOf(req.montoBruto)
                    it[corteEmpresa]   = BigDecimal.valueOf(req.corteEmpresa)
                    it[corteProveedor] = BigDecimal.valueOf(req.corteProveedor)
                    it[descripcion]    = req.descripcion
                    it[createdAt]      = now
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
        }
    }
}
