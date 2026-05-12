package com.vgtech.backend.routes

import com.vgtech.backend.models.ProgresoProyecto
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
data class ProgresoDto(
    val id: String,
    val proyectoId: String,
    val proveedorUid: String,
    val fecha: Long,
    val porcentajeAvance: Int,
    val descripcion: String,
    val tipoReporte: String,
    val aspectosPositivos: String,
    val problemas: String,
    val motivoRetraso: String?,
    val urlImagen: String?,
    val evaluado: Boolean,
    val evaluacionConsultor: String,
    val comentariosConsultor: String,
    val calificacionEvaluacion: Double,
    val fueModificado: Boolean,
    val progresoOriginal: Int,
    val motivoModificacion: String
)

@Serializable
data class CreateProgresoRequest(
    val proyectoId: String,
    val proveedorUid: String,
    val porcentajeAvance: Int = 0,
    val descripcion: String = "",
    val tipoReporte: String = "Diario",
    val aspectosPositivos: String = "",
    val problemas: String = "",
    val motivoRetraso: String? = null,
    val urlImagen: String? = null
)

@Serializable
data class EvaluarProgresoRequest(
    val evaluacionConsultor: String,
    val comentariosConsultor: String = "",
    val calificacionEvaluacion: Double = 0.0,
    val urlImagenEvaluacion: String? = null,
    val progresoModificado: Int? = null,
    val motivoModificacion: String = ""
)

fun Route.progresoRoutes() {
    route("/progreso") {

        // GET /progreso?proyectoId=xxx
        get {
            val proyFilter = call.request.queryParameters["proyectoId"]
            val provFilter = call.request.queryParameters["proveedorUid"]
            val list = transaction {
                var q = ProgresoProyecto.selectAll()
                proyFilter?.let { q = ProgresoProyecto.select { ProgresoProyecto.proyectoId eq UUID.fromString(it) } }
                provFilter?.let { q = ProgresoProyecto.select { ProgresoProyecto.proveedorUid eq UUID.fromString(it) } }
                q.orderBy(ProgresoProyecto.fecha, SortOrder.DESC).map { row ->
                    ProgresoDto(
                        id                   = row[ProgresoProyecto.id].toString(),
                        proyectoId           = row[ProgresoProyecto.proyectoId].toString(),
                        proveedorUid         = row[ProgresoProyecto.proveedorUid].toString(),
                        fecha                = row[ProgresoProyecto.fecha].toEpochMilli(),
                        porcentajeAvance     = row[ProgresoProyecto.porcentajeAvance].toInt(),
                        descripcion          = row[ProgresoProyecto.descripcion],
                        tipoReporte          = row[ProgresoProyecto.tipoReporte],
                        aspectosPositivos    = row[ProgresoProyecto.aspectosPositivos],
                        problemas            = row[ProgresoProyecto.problemas],
                        motivoRetraso        = row[ProgresoProyecto.motivoRetraso],
                        urlImagen            = row[ProgresoProyecto.urlImagen],
                        evaluado             = row[ProgresoProyecto.evaluado],
                        evaluacionConsultor  = row[ProgresoProyecto.evaluacionConsultor],
                        comentariosConsultor = row[ProgresoProyecto.comentariosConsultor],
                        calificacionEvaluacion = row[ProgresoProyecto.calificacionEvaluacion].toDouble(),
                        fueModificado        = row[ProgresoProyecto.fueModificado],
                        progresoOriginal     = row[ProgresoProyecto.progresoOriginal].toInt(),
                        motivoModificacion   = row[ProgresoProyecto.motivoModificacion]
                    )
                }
            }
            call.respond(HttpStatusCode.OK, list)
        }

        // POST /progreso
        post {
            val req = call.receive<CreateProgresoRequest>()
            val now = Instant.now()
            val newId = UUID.randomUUID()
            transaction {
                ProgresoProyecto.insert {
                    it[id]               = newId
                    it[proyectoId]       = UUID.fromString(req.proyectoId)
                    it[proveedorUid]     = UUID.fromString(req.proveedorUid)
                    it[fecha]            = now
                    it[porcentajeAvance] = req.porcentajeAvance.toShort()
                    it[descripcion]      = req.descripcion
                    it[tipoReporte]      = req.tipoReporte
                    it[aspectosPositivos]= req.aspectosPositivos
                    it[problemas]        = req.problemas
                    it[motivoRetraso]    = req.motivoRetraso
                    it[urlImagen]        = req.urlImagen
                    it[createdAt]        = now
                    it[updatedAt]        = now
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
        }

        // PUT /progreso/{id}/evaluar
        put("/{id}/evaluar") {
            val id  = UUID.fromString(call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest))
            val req = call.receive<EvaluarProgresoRequest>()
            val now = Instant.now()
            transaction {
                val current = ProgresoProyecto.select { ProgresoProyecto.id eq id }.firstOrNull()
                val originalProgress = current?.get(ProgresoProyecto.porcentajeAvance)?.toInt() ?: 0
                val isModified = req.progresoModificado != null && req.progresoModificado != originalProgress
                ProgresoProyecto.update({ ProgresoProyecto.id eq id }) {
                    it[evaluado]              = true
                    it[evaluacionConsultor]   = req.evaluacionConsultor
                    it[comentariosConsultor]  = req.comentariosConsultor
                    it[calificacionEvaluacion]= BigDecimal.valueOf(req.calificacionEvaluacion)
                    it[urlImagenEvaluacion]   = req.urlImagenEvaluacion
                    if (isModified) {
                        it[fueModificado]     = true
                        it[progresoOriginal]  = originalProgress.toShort()
                        it[porcentajeAvance]  = req.progresoModificado!!.toShort()
                        it[motivoModificacion]= req.motivoModificacion
                    }
                    it[updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.OK, mapOf("message" to "Progreso evaluado"))
        }
    }
}
