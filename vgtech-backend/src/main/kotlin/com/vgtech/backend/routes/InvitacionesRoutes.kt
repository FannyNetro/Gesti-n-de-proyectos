package com.vgtech.backend.routes

import com.vgtech.backend.models.InvitacionesProveedor
import com.vgtech.backend.models.Cotizaciones
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
data class InvitacionDto(
    val id: String,
    val proyectoId: String,
    val proveedorUid: String,
    val supervisorUid: String,
    val mensaje: String,
    val fecha: Long,
    val estado: String
)

@Serializable
data class CreateInvitacionRequest(
    val proyectoId: String,
    val proveedorUid: String,
    val supervisorUid: String,
    val mensaje: String = ""
)

@Serializable
data class CotizacionDto(
    val id: String,
    val invitacionId: String,
    val proyectoId: String,
    val proveedorUid: String,
    val monto: Double,
    val diasEstimados: Int,
    val descripcion: String,
    val fecha: Long,
    val enviadaACliente: Boolean,
    val estadoCliente: String,
    val confirmadoSupervisor: Boolean
)

@Serializable
data class CreateCotizacionRequest(
    val invitacionId: String,
    val proyectoId: String,
    val proveedorUid: String,
    val monto: Double,
    val diasEstimados: Int = 0,
    val descripcion: String = ""
)

fun Route.invitacionesRoutes() {
    route("/invitaciones") {
        get {
            val provFilter = call.request.queryParameters["proveedorUid"]
            val supFilter  = call.request.queryParameters["supervisorUid"]
            val list = transaction {
                var q = InvitacionesProveedor.selectAll()
                provFilter?.let { q = InvitacionesProveedor.select { InvitacionesProveedor.proveedorUid eq UUID.fromString(it) } }
                supFilter?.let  { q = InvitacionesProveedor.select { InvitacionesProveedor.supervisorUid eq UUID.fromString(it) } }
                q.map { row ->
                    InvitacionDto(
                        id           = row[InvitacionesProveedor.id].toString(),
                        proyectoId   = row[InvitacionesProveedor.proyectoId].toString(),
                        proveedorUid = row[InvitacionesProveedor.proveedorUid].toString(),
                        supervisorUid= row[InvitacionesProveedor.supervisorUid].toString(),
                        mensaje      = row[InvitacionesProveedor.mensaje],
                        fecha        = row[InvitacionesProveedor.fecha].toEpochMilli(),
                        estado       = row[InvitacionesProveedor.estado]
                    )
                }
            }
            call.respond(HttpStatusCode.OK, list)
        }

        post {
            val req = call.receive<CreateInvitacionRequest>()
            val now = Instant.now()
            val newId = UUID.randomUUID()
            transaction {
                InvitacionesProveedor.insert {
                    it[id]           = newId
                    it[proyectoId]   = UUID.fromString(req.proyectoId)
                    it[proveedorUid] = UUID.fromString(req.proveedorUid)
                    it[supervisorUid]= UUID.fromString(req.supervisorUid)
                    it[mensaje]      = req.mensaje
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
                InvitacionesProveedor.update({ InvitacionesProveedor.id eq id }) {
                    it[estado]    = req["estado"] ?: "Enviada"
                    it[updatedAt] = Instant.now()
                }
            }
            call.respond(HttpStatusCode.OK, mapOf("message" to "Estado actualizado"))
        }
    }
}

fun Route.cotizacionesRoutes() {
    route("/cotizaciones") {
        get {
            val provFilter  = call.request.queryParameters["proveedorUid"]
            val proyFilter  = call.request.queryParameters["proyectoId"]
            val list = transaction {
                var q = Cotizaciones.selectAll()
                provFilter?.let { q = Cotizaciones.select { Cotizaciones.proveedorUid eq UUID.fromString(it) } }
                proyFilter?.let { q = Cotizaciones.select { Cotizaciones.proyectoId   eq UUID.fromString(it) } }
                q.map { row ->
                    CotizacionDto(
                        id                   = row[Cotizaciones.id].toString(),
                        invitacionId         = row[Cotizaciones.invitacionId].toString(),
                        proyectoId           = row[Cotizaciones.proyectoId].toString(),
                        proveedorUid         = row[Cotizaciones.proveedorUid].toString(),
                        monto                = row[Cotizaciones.monto].toDouble(),
                        diasEstimados        = row[Cotizaciones.diasEstimados].toInt(),
                        descripcion          = row[Cotizaciones.descripcion],
                        fecha                = row[Cotizaciones.fecha].toEpochMilli(),
                        enviadaACliente      = row[Cotizaciones.enviadaACliente],
                        estadoCliente        = row[Cotizaciones.estadoCliente],
                        confirmadoSupervisor = row[Cotizaciones.confirmadoSupervisor]
                    )
                }
            }
            call.respond(HttpStatusCode.OK, list)
        }

        post {
            val req = call.receive<CreateCotizacionRequest>()
            val now = Instant.now()
            val newId = UUID.randomUUID()
            transaction {
                Cotizaciones.insert {
                    it[id]           = newId
                    it[invitacionId] = UUID.fromString(req.invitacionId)
                    it[proyectoId]   = UUID.fromString(req.proyectoId)
                    it[proveedorUid] = UUID.fromString(req.proveedorUid)
                    it[monto]        = BigDecimal.valueOf(req.monto)
                    it[diasEstimados]= req.diasEstimados.toShort()
                    it[descripcion]  = req.descripcion
                    it[fecha]        = now
                    it[createdAt]    = now
                    it[updatedAt]    = now
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
        }

        patch("/{id}/cliente-estado") {
            val id  = UUID.fromString(call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest))
            val req = call.receive<Map<String, String>>()
            transaction {
                Cotizaciones.update({ Cotizaciones.id eq id }) {
                    it[estadoCliente] = req["estadoCliente"] ?: "Pendiente"
                    it[updatedAt]     = Instant.now()
                }
            }
            call.respond(HttpStatusCode.OK, mapOf("message" to "Estado cliente actualizado"))
        }

        patch("/{id}/confirmar-supervisor") {
            val id = UUID.fromString(call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest))
            transaction {
                Cotizaciones.update({ Cotizaciones.id eq id }) {
                    it[confirmadoSupervisor] = true
                    it[updatedAt]            = Instant.now()
                }
            }
            call.respond(HttpStatusCode.OK, mapOf("message" to "Supervisor confirmó la cotización"))
        }
    }
}
