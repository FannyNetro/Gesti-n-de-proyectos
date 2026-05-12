package com.vgtech.backend.routes

import com.vgtech.backend.models.Proyectos
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
data class ProyectoDto(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val proveedorUid: String?,
    val supervisorUid: String?,
    val consultorUid: String?,
    val clienteUid: String?,
    val progreso: Double,
    val estado: String,
    val fechaInicio: Long,
    val fechaFin: Long?,
    val comentarios: String,
    val tieneRetrasos: Boolean,
    val motivoRetraso: String,
    val responsableRetraso: String,
    val calificacionProveedor: Double,
    val calificacionConsultor: Double,
    val resultadoEvaluacion: String
)

@Serializable
data class CreateProyectoRequest(
    val titulo: String,
    val descripcion: String = "",
    val proveedorUid: String? = null,
    val supervisorUid: String? = null,
    val consultorUid: String? = null,
    val clienteUid: String? = null
)

@Serializable
data class UpdateProyectoRequest(
    val titulo: String? = null,
    val descripcion: String? = null,
    val proveedorUid: String? = null,
    val supervisorUid: String? = null,
    val consultorUid: String? = null,
    val progreso: Double? = null,
    val estado: String? = null,
    val fechaFin: Long? = null,
    val comentarios: String? = null,
    val tieneRetrasos: Boolean? = null,
    val motivoRetraso: String? = null,
    val responsableRetraso: String? = null,
    val calificacionProveedor: Double? = null,
    val calificacionConsultor: Double? = null,
    val resultadoEvaluacion: String? = null
)

private fun ResultRow.toDto() = ProyectoDto(
    id                   = this[Proyectos.id].toString(),
    titulo               = this[Proyectos.titulo],
    descripcion          = this[Proyectos.descripcion],
    proveedorUid         = this[Proyectos.proveedorUid]?.toString(),
    supervisorUid        = this[Proyectos.supervisorUid]?.toString(),
    consultorUid         = this[Proyectos.consultorUid]?.toString(),
    clienteUid           = this[Proyectos.clienteUid]?.toString(),
    progreso             = this[Proyectos.progreso].toDouble(),
    estado               = this[Proyectos.estado],
    fechaInicio          = this[Proyectos.fechaInicio].toEpochMilli(),
    fechaFin             = this[Proyectos.fechaFin]?.toEpochMilli(),
    comentarios          = this[Proyectos.comentarios],
    tieneRetrasos        = this[Proyectos.tieneRetrasos],
    motivoRetraso        = this[Proyectos.motivoRetraso],
    responsableRetraso   = this[Proyectos.responsableRetraso],
    calificacionProveedor= this[Proyectos.calificacionProveedor].toDouble(),
    calificacionConsultor= this[Proyectos.calificacionConsultor].toDouble(),
    resultadoEvaluacion  = this[Proyectos.resultadoEvaluacion]
)

fun Route.proyectosRoutes() {
    route("/proyectos") {

        // GET /proyectos
        get {
            val supervisorFilter = call.request.queryParameters["supervisorUid"]
            val consultorFilter  = call.request.queryParameters["consultorUid"]
            val proveedorFilter  = call.request.queryParameters["proveedorUid"]
            val list = transaction {
                var q = Proyectos.selectAll()
                supervisorFilter?.let { q = Proyectos.select { Proyectos.supervisorUid eq UUID.fromString(it) } }
                consultorFilter?.let  { q = Proyectos.select { Proyectos.consultorUid  eq UUID.fromString(it) } }
                proveedorFilter?.let  { q = Proyectos.select { Proyectos.proveedorUid  eq UUID.fromString(it) } }
                q.map { it.toDto() }
            }
            call.respond(HttpStatusCode.OK, list)
        }

        // GET /proyectos/{id}
        get("/{id}") {
            val id = UUID.fromString(call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest))
            val p = transaction { Proyectos.select { Proyectos.id eq id }.firstOrNull()?.toDto() }
            if (p == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.OK, p)
        }

        // POST /proyectos
        post {
            val req = call.receive<CreateProyectoRequest>()
            val now = Instant.now()
            val newId = UUID.randomUUID()
            transaction {
                Proyectos.insert {
                    it[id]          = newId
                    it[titulo]      = req.titulo
                    it[descripcion] = req.descripcion
                    it[proveedorUid]  = req.proveedorUid?.let { u -> UUID.fromString(u) }
                    it[supervisorUid] = req.supervisorUid?.let { u -> UUID.fromString(u) }
                    it[consultorUid]  = req.consultorUid?.let { u -> UUID.fromString(u) }
                    it[clienteUid]    = req.clienteUid?.let { u -> UUID.fromString(u) }
                    it[fechaInicio] = now
                    it[createdAt]   = now
                    it[updatedAt]   = now
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
        }

        // PUT /proyectos/{id}
        put("/{id}") {
            val id = UUID.fromString(call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest))
            val req = call.receive<UpdateProyectoRequest>()
            val now = Instant.now()
            transaction {
                Proyectos.update({ Proyectos.id eq id }) {
                    req.titulo?.let { v -> it[titulo] = v }
                    req.descripcion?.let { v -> it[descripcion] = v }
                    req.proveedorUid?.let { v -> it[proveedorUid] = UUID.fromString(v) }
                    req.supervisorUid?.let { v -> it[supervisorUid] = UUID.fromString(v) }
                    req.consultorUid?.let { v -> it[consultorUid] = UUID.fromString(v) }
                    req.progreso?.let { v -> it[progreso] = BigDecimal.valueOf(v) }
                    req.estado?.let { v -> it[estado] = v }
                    req.fechaFin?.let { v -> it[fechaFin] = Instant.ofEpochMilli(v) }
                    req.comentarios?.let { v -> it[comentarios] = v }
                    req.tieneRetrasos?.let { v -> it[tieneRetrasos] = v }
                    req.motivoRetraso?.let { v -> it[motivoRetraso] = v }
                    req.responsableRetraso?.let { v -> it[responsableRetraso] = v }
                    req.calificacionProveedor?.let { v -> it[calificacionProveedor] = BigDecimal.valueOf(v) }
                    req.calificacionConsultor?.let { v -> it[calificacionConsultor] = BigDecimal.valueOf(v) }
                    req.resultadoEvaluacion?.let { v -> it[resultadoEvaluacion] = v }
                    it[updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.OK, mapOf("message" to "Proyecto actualizado"))
        }
    }
}
