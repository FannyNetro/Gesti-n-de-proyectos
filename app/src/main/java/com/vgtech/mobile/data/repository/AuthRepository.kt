package com.vgtech.mobile.data.repository

import com.vgtech.mobile.data.model.Employee
import com.vgtech.mobile.data.model.UserRole
import com.vgtech.mobile.network.RetrofitClient
import com.vgtech.mobile.network.dto.LoginRequestDto
import com.vgtech.mobile.network.dto.LoginResponseDto

/**
 * AuthRepository — conectado a PostgreSQL vía API REST Ktor.
 * Reemplaza el InternalDb local por llamadas HTTP.
 */
class AuthRepository {

    // ── Sesión actual ────────────────────────────────────────────────────
    var currentUser: Employee? = null
        private set

    // ── Login ────────────────────────────────────────────────────────────

    /**
     * Llama a POST /auth/login con email y password.
     * Lanza excepción si las credenciales son incorrectas o el usuario está inactivo.
     */
    suspend fun signIn(email: String, password: String): UserRole {
        val response = RetrofitClient.api.login(LoginRequestDto(email, password))

        if (!response.isSuccessful) {
            val errorMsg = when (response.code()) {
                401  -> "Credenciales incorrectas"
                403  -> "Usuario inactivo"
                else -> "Error de servidor: ${response.code()}"
            }
            throw Exception(errorMsg)
        }

        val body = response.body() ?: throw Exception("Respuesta vacía del servidor")
        currentUser = body.toEmployee()
        return currentUser!!.toRole()
    }

    // ── Logout ───────────────────────────────────────────────────────────

    fun signOut() {
        currentUser = null
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private fun LoginResponseDto.toEmployee() = Employee(
        uid            = uid,
        nombreCompleto = nombreCompleto,
        email          = email,
        puesto         = puesto,
        activo         = activo
    )
}
