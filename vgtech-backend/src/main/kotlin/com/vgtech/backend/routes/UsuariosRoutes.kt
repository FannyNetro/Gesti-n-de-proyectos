package com.vgtech.backend.routes

import com.vgtech.backend.models.Usuarios
import com.vgtech.backend.models.UsuarioCategoriasWork
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
data class UsuarioDto(
    val uid: String,
    val nombreCompleto: String,
    val email: String,
    val direccion: String,
    val telefono: String,
    val puesto: String,
    val sueldo: Double,
    val pagoPorHora: Double,
    val diasVacaciones: Double,
    val activo: Boolean,
    val motivoInactivo: String,
    val fotoBase64: String? = null,
    val tipoTrabajo: List<String> = emptyList()
)

@Serializable
data class CreateUsuarioRequest(
    val nombreCompleto: String,
    val email: String,
    val password: String,
    val direccion: String = "",
    val telefono: String = "",
    val puesto: String,
    val sueldo: Double = 0.0,
    val pagoPorHora: Double = 0.0,
    val diasVacaciones: Double = 0.0,
    val fotoBase64: String? = null,
    val tipoTrabajo: List<String> = emptyList()
)

@Serializable
data class UpdateUsuarioRequest(
    val nombreCompleto: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    val puesto: String? = null,
    val sueldo: Double? = null,
    val pagoPorHora: Double? = null,
    val diasVacaciones: Double? = null,
    val activo: Boolean? = null,
    val motivoInactivo: String? = null,
    val password: String? = null,
    val fotoBase64: String? = null
)

fun Route.usuariosRoutes() {
    route("/usuarios") {

        // GET /usuarios — lista todos los usuarios
        get {
            val puestoFilter = call.request.queryParameters["puesto"]?.uppercase()
            val usuarios = transaction {
                val query = if (puestoFilter != null)
                    Usuarios.select { Usuarios.puesto eq puestoFilter }
                else
                    Usuarios.selectAll()

                query.map { row ->
                    val uid = row[Usuarios.uid]
                    val categorias = UsuarioCategoriasWork
                        .select { UsuarioCategoriasWork.usuarioUid eq uid }
                        .map { it[UsuarioCategoriasWork.categoria] }
                    UsuarioDto(
                        uid            = uid.toString(),
                        nombreCompleto = row[Usuarios.nombreCompleto],
                        email          = row[Usuarios.email],
                        direccion      = row[Usuarios.direccion],
                        telefono       = row[Usuarios.telefono],
                        puesto         = formatPuesto(row[Usuarios.puesto]),
                        sueldo         = row[Usuarios.sueldo].toDouble(),
                        pagoPorHora    = row[Usuarios.pagoPorHora].toDouble(),
                        diasVacaciones = row[Usuarios.diasVacaciones].toDouble(),
                        activo         = row[Usuarios.activo],
                        motivoInactivo = row[Usuarios.motivoInactivo],
                        fotoBase64     = row[Usuarios.fotoBase64],
                        tipoTrabajo    = categorias
                    )
                }
            }
            call.respond(HttpStatusCode.OK, usuarios)
        }

        // GET /usuarios/{uid}
        get("/{uid}") {
            val uid = UUID.fromString(call.parameters["uid"] ?: return@get call.respond(HttpStatusCode.BadRequest))
            val usuario = transaction {
                Usuarios.select { Usuarios.uid eq uid }.firstOrNull()?.let { row ->
                    val categorias = UsuarioCategoriasWork
                        .select { UsuarioCategoriasWork.usuarioUid eq uid }
                        .map { it[UsuarioCategoriasWork.categoria] }
                    UsuarioDto(
                        uid            = row[Usuarios.uid].toString(),
                        nombreCompleto = row[Usuarios.nombreCompleto],
                        email          = row[Usuarios.email],
                        direccion      = row[Usuarios.direccion],
                        telefono       = row[Usuarios.telefono],
                        puesto         = formatPuesto(row[Usuarios.puesto]),
                        sueldo         = row[Usuarios.sueldo].toDouble(),
                        pagoPorHora    = row[Usuarios.pagoPorHora].toDouble(),
                        diasVacaciones = row[Usuarios.diasVacaciones].toDouble(),
                        activo         = row[Usuarios.activo],
                        motivoInactivo = row[Usuarios.motivoInactivo],
                        fotoBase64     = row[Usuarios.fotoBase64],
                        tipoTrabajo    = categorias
                    )
                }
            }
            if (usuario == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.OK, usuario)
        }

        // POST /usuarios — crear nuevo usuario
        post {
            val req = call.receive<CreateUsuarioRequest>()
            val newUid = UUID.randomUUID()
            val now = Instant.now()
            transaction {
                Usuarios.insert {
                    it[uid]            = newUid
                    it[nombreCompleto] = req.nombreCompleto
                    it[email]          = req.email
                    it[passwordHash]   = req.password
                    it[direccion]      = req.direccion
                    it[telefono]       = req.telefono
                    it[puesto]         = req.puesto.uppercase()
                    it[sueldo]         = BigDecimal.valueOf(req.sueldo)
                    it[pagoPorHora]    = BigDecimal.valueOf(req.pagoPorHora)
                    it[diasVacaciones] = BigDecimal.valueOf(req.diasVacaciones)
                    it[fotoBase64]     = req.fotoBase64
                    it[fechaRegistro]  = now
                    it[createdAt]      = now
                    it[updatedAt]      = now
                }
                req.tipoTrabajo.forEach { cat ->
                    UsuarioCategoriasWork.insert {
                        it[usuarioUid] = newUid
                        it[categoria]  = cat
                    }
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("uid" to newUid.toString()))
        }

        // PUT /usuarios/{uid} — actualizar usuario
        put("/{uid}") {
            val uid = UUID.fromString(call.parameters["uid"] ?: return@put call.respond(HttpStatusCode.BadRequest))
            val req = call.receive<UpdateUsuarioRequest>()
            val now = Instant.now()
            transaction {
                Usuarios.update({ Usuarios.uid eq uid }) {
                    req.nombreCompleto?.let { v -> it[nombreCompleto] = v }
                    req.direccion?.let { v -> it[direccion] = v }
                    req.telefono?.let { v -> it[telefono] = v }
                    req.puesto?.let { v -> it[puesto] = v.uppercase() }
                    req.sueldo?.let { v -> it[sueldo] = BigDecimal.valueOf(v) }
                    req.pagoPorHora?.let { v -> it[pagoPorHora] = BigDecimal.valueOf(v) }
                    req.diasVacaciones?.let { v -> it[diasVacaciones] = BigDecimal.valueOf(v) }
                    req.activo?.let { v -> it[activo] = v }
                    req.motivoInactivo?.let { v -> it[motivoInactivo] = v }
                    req.password?.let { v -> it[passwordHash] = v }
                    req.fotoBase64?.let { v -> it[fotoBase64] = v }
                    it[updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.OK, mapOf("message" to "Usuario actualizado"))
        }
    }
}

fun formatPuesto(puesto: String): String {
    val p = puesto.uppercase()
    return if (p == "RH") "RH" else p.lowercase().replaceFirstChar { it.uppercase() }
}
