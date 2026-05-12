package com.vgtech.backend.routes

import com.vgtech.backend.models.Usuarios
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(
    val uid: String,
    val nombreCompleto: String,
    val email: String,
    val puesto: String,
    val activo: Boolean
)

fun Route.authRoutes() {
    route("/auth") {

        // POST /auth/login
        post("/login") {
            val req = call.receive<LoginRequest>()
            val normalizedEmail = req.email.lowercase().trim()
            val user = transaction {
                Usuarios.select { Usuarios.email eq normalizedEmail }.firstOrNull()
            }

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Usuario no encontrado"))
                return@post
            }

            if (user[Usuarios.passwordHash] != req.password) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Contraseña incorrecta"))
                return@post
            }

            if (!user[Usuarios.activo]) {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Usuario inactivo"))
                return@post
            }

            call.respond(
                HttpStatusCode.OK,
                LoginResponse(
                    uid            = user[Usuarios.uid].toString(),
                    nombreCompleto = user[Usuarios.nombreCompleto],
                    email          = user[Usuarios.email],
                    puesto         = user[Usuarios.puesto],
                    activo         = user[Usuarios.activo]
                )
            )
        }
    }
}
