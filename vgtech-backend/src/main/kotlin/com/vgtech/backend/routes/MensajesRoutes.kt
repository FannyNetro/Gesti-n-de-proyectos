package com.vgtech.backend.routes

import com.vgtech.backend.models.MensajesChat
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
data class MensajeChatDto(
    val id: String,
    val remitenteUid: String,
    val destinatarioUid: String,
    val proyectoId: String?,
    val mensaje: String,
    val timestamp: Long,
    val leido: Boolean
)

@Serializable
data class CreateMensajeChatRequest(
    val remitenteUid: String,
    val destinatarioUid: String,
    val proyectoId: String? = null,
    val mensaje: String
)

fun Route.mensajesRoutes() {
    route("/mensajes") {
        get {
            val proyFilter = call.request.queryParameters["proyectoId"]
            val remFilter  = call.request.queryParameters["remitenteUid"]
            val list = transaction {
                var q = MensajesChat.selectAll()
                proyFilter?.let { q = MensajesChat.select { MensajesChat.proyectoId eq UUID.fromString(it) } }
                remFilter?.let  { q = MensajesChat.select { MensajesChat.remitenteUid eq UUID.fromString(it) } }
                q.orderBy(MensajesChat.timestamp, SortOrder.ASC).map { row ->
                    MensajeChatDto(
                        id              = row[MensajesChat.id].toString(),
                        remitenteUid    = row[MensajesChat.remitenteUid].toString(),
                        destinatarioUid = row[MensajesChat.destinatarioUid].toString(),
                        proyectoId      = row[MensajesChat.proyectoId]?.toString(),
                        mensaje         = row[MensajesChat.mensaje],
                        timestamp       = row[MensajesChat.timestamp].toEpochMilli(),
                        leido           = row[MensajesChat.leido]
                    )
                }
            }
            call.respond(HttpStatusCode.OK, list)
        }

        post {
            val req = call.receive<CreateMensajeChatRequest>()
            val now = Instant.now()
            val newId = UUID.randomUUID()
            transaction {
                MensajesChat.insert {
                    it[id]              = newId
                    it[remitenteUid]    = UUID.fromString(req.remitenteUid)
                    it[destinatarioUid] = UUID.fromString(req.destinatarioUid)
                    it[proyectoId]      = req.proyectoId?.let { u -> UUID.fromString(u) }
                    it[mensaje]         = req.mensaje
                    it[timestamp]       = now
                    it[createdAt]       = now
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to newId.toString()))
        }
    }
}
