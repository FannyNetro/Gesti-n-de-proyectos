package com.vgtech.backend.routes

import com.vgtech.backend.models.FasesProyecto
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
data class FaseProyectoDto(
    val id: String,
    val proyectoId: String,
    val nombre: String,
    val descripcion: String,
    val fechaInicio: Long,
    val fechaEntrega: Long,
    val estado: String,
    val porcentaje: Int,
    val fotoBase64: String?,
    val observaciones: String
)

@Serializable
data class CreateFaseProyectoRequest(
    val proyectoId: String,
    val nombre: String,
    val descripcion: String = "",
    val fechaInicio: Long,
    val fechaEntrega: Long,
    val estado: String = "Pendiente"
)

@Serializable
data class UpdateFaseProyectoRequest(
    val nombre: String? = null,
    val descripcion: String? = null,
    val estado: String? = null,
    val porcentaje: Int? = null,
    val fotoBase64: String? = null,
    val observaciones: String? = null,
    val fechaEntrega: Long? = null
)

fun Route.fasesProyectoRoutes() {
    route("/fases-proyecto") {

        // GET /fases-proyecto?proyectoId=xxx
        get {
            val proyFilter = call.request.queryParameters["proyectoId"]
            val list = transaction {
                val q = if (proyFilter != null)
                    FasesProyecto.select { FasesProyecto.proyectoId eq UUID.fromString(proyFilter) }
                else
                    FasesProyecto.selectAll()
                q.orderBy(FasesProyecto.fechaInicio, SortOrder.ASC).map { row ->
                    FaseProyectoDto(
                        id           = row[FasesProyecto.id].toString(),
                        proyectoId   = row[FasesProyecto.proyectoId].toString(),
                        nombre       = row[FasesProyecto.nombre],
                        descripcion  = row[FasesProyecto.descripcion],
                        fechaInicio  = row[FasesProyecto.fechaInicio].toEpochMilli(),
                        fechaEntrega = row[FasesProyecto.fechaEntrega].toEpochMilli(),
                        estado       = row[FasesProyecto.estado],
                        porcentaje   = row[FasesProyecto.porcentaje].toInt(),
                        fotoBase64   = row[FasesProyecto.fotoBase64],
                        observaciones= row[FasesProyecto.observaciones]
                    )
                }
            }
            call.respond(HttpStatusCode.OK, list)
        }

        // POST /fases-proyecto
        post {
            try {
                val req = call.receive<CreateFaseProyectoRequest>()
                val newId = UUID.randomUUID()
                val now = Instant.now()
                transaction {
                    FasesProyecto.insert {
                        it[id]           = newId
                        it[proyectoId]   = UUID.fromString(req.proyectoId)
                        it[nombre]       = req.nombre
                        it[descripcion]  = req.descripcion
                        it[fechaInicio]  = Instant.ofEpochMilli(req.fechaInicio)
                        it[fechaEntrega] = Instant.ofEpochMilli(req.fechaEntrega)
                        it[estado]       = req.estado
                        it[createdAt]    = now
                        it[updatedAt]    = now
                    }
                }
                call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Error al crear fase")))
            }
        }

        // PUT /fases-proyecto/{id} — proveedor actualiza avance
        put("/{id}") {
            try {
                val id = UUID.fromString(call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest))
                val req = call.receive<UpdateFaseProyectoRequest>()
                val now = Instant.now()
                transaction {
                    FasesProyecto.update({ FasesProyecto.id eq id }) {
                        req.nombre?.let { v -> it[nombre] = v }
                        req.descripcion?.let { v -> it[descripcion] = v }
                        req.estado?.let { v -> it[estado] = v }
                        req.porcentaje?.let { v -> it[porcentaje] = v.toShort() }
                        req.fotoBase64?.let { v -> it[fotoBase64] = v }
                        req.observaciones?.let { v -> it[observaciones] = v }
                        req.fechaEntrega?.let { v -> it[fechaEntrega] = Instant.ofEpochMilli(v) }
                        it[updatedAt] = now
                    }
                }
                call.respond(HttpStatusCode.OK, mapOf("message" to "Fase actualizada"))
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Error al actualizar fase")))
            }
        }
    }
}
