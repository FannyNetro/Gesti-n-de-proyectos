package com.vgtech.backend.routes

import com.vgtech.backend.models.EvaluacionesDesempeno
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
data class EvaluacionDto(
    val id: String,
    val evaluadoUid: String,
    val rolEvaluado: String,
    val proyectoId: String?,
    val calificacionCalidad: Double,
    val calificacionPuntualidad: Double,
    val calificacionComunicacion: Double,
    val calificacionGeneral: Double,
    val comentarios: String,
    val fecha: Long
)

@Serializable
data class CreateEvaluacionRequest(
    val evaluadoUid: String,
    val rolEvaluado: String,
    val proyectoId: String? = null,
    val calificacionCalidad: Double,
    val calificacionPuntualidad: Double,
    val calificacionComunicacion: Double,
    val comentarios: String = ""
)

fun Route.evaluacionesRoutes() {
    route("/evaluaciones") {
        get {
            val evalFilter = call.request.queryParameters["evaluadoUid"]
            val list = transaction {
                val q = if (evalFilter != null)
                    EvaluacionesDesempeno.select { EvaluacionesDesempeno.evaluadoUid eq UUID.fromString(evalFilter) }
                else
                    EvaluacionesDesempeno.selectAll()
                q.map { row ->
                    EvaluacionDto(
                        id                      = row[EvaluacionesDesempeno.id].toString(),
                        evaluadoUid             = row[EvaluacionesDesempeno.evaluadoUid].toString(),
                        rolEvaluado             = row[EvaluacionesDesempeno.rolEvaluado],
                        proyectoId              = row[EvaluacionesDesempeno.proyectoId]?.toString(),
                        calificacionCalidad     = row[EvaluacionesDesempeno.calificacionCalidad].toDouble(),
                        calificacionPuntualidad = row[EvaluacionesDesempeno.calificacionPuntualidad].toDouble(),
                        calificacionComunicacion= row[EvaluacionesDesempeno.calificacionComunicacion].toDouble(),
                        calificacionGeneral     = row[EvaluacionesDesempeno.calificacionGeneral].toDouble(),
                        comentarios             = row[EvaluacionesDesempeno.comentarios],
                        fecha                   = row[EvaluacionesDesempeno.fecha].toEpochMilli()
                    )
                }
            }
            call.respond(HttpStatusCode.OK, list)
        }

        post {
            val req = call.receive<CreateEvaluacionRequest>()
            val general = (req.calificacionCalidad + req.calificacionPuntualidad + req.calificacionComunicacion) / 3.0
            val now = Instant.now()
            val newId = UUID.randomUUID()
            transaction {
                EvaluacionesDesempeno.insert {
                    it[id]                       = newId
                    it[evaluadoUid]              = UUID.fromString(req.evaluadoUid)
                    it[rolEvaluado]              = req.rolEvaluado
                    it[proyectoId]               = req.proyectoId?.let { u -> UUID.fromString(u) }
                    it[calificacionCalidad]      = BigDecimal.valueOf(req.calificacionCalidad)
                    it[calificacionPuntualidad]  = BigDecimal.valueOf(req.calificacionPuntualidad)
                    it[calificacionComunicacion] = BigDecimal.valueOf(req.calificacionComunicacion)
                    it[calificacionGeneral]      = BigDecimal.valueOf(general)
                    it[comentarios]              = req.comentarios
                    it[fecha]                    = now
                    it[createdAt]                = now
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
        }
    }
}
