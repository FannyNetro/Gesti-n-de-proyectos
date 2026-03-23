package com.vgtech.mobile.data.repository

import android.content.Context
import com.vgtech.mobile.data.local.InternalDb
import com.vgtech.mobile.data.model.Employee
import com.vgtech.mobile.data.model.UserRole
import java.util.UUID

/**
 * AuthRepository — handles Internal Authentication and role resolution.
 */
class AuthRepository {

    // ── Current Session ──────────────────────────────────────────────

    var currentUser: Employee? = null
        private set

    val isLoggedIn: Boolean
        get() = currentUser != null

    // ── Sign In ──────────────────────────────────────────────────────

    suspend fun signIn(email: String, password: String): UserRole {
        val user = InternalDb.getEmployeeByEmail(email)
            ?: throw Exception("Usuario no encontrado")

        if (user.password != password) {
            throw Exception("Contraseña incorrecta")
        }
        
        if (!user.activo) {
            throw Exception("El usuario está inactivo")
        }

        currentUser = user
        return getUserRole(user.uid)
    }

    // ── Role Resolution ──────────────────────────────────────────────

    suspend fun getUserRole(uid: String): UserRole {
        val user = InternalDb.getEmployeeById(uid)
            ?: throw Exception("Perfil de empleado no encontrado")
        return user.toRole()
    }

    // ── Employee Registration ────────────────────────────────────────

    suspend fun registerEmployee(
        context: Context,
        employee: Employee
    ): String {
        // Enforce uniqueness for email
        if (InternalDb.getEmployeeByEmail(employee.email) != null) {
            throw Exception("El correo electrónico ya está registrado")
        }

        val uid = UUID.randomUUID().toString()
        val employeeWithUid = employee.copy(uid = uid)
        
        InternalDb.addEmployee(employeeWithUid)
        return uid
    }

    // ── Self-Registration ────────────────────────────────────────────

    suspend fun signUp(employee: Employee): UserRole {
        // Enforce uniqueness for email
        if (InternalDb.getEmployeeByEmail(employee.email) != null) {
            throw Exception("El correo electrónico ya está registrado")
        }

        val uid = UUID.randomUUID().toString()
        val employeeWithUid = employee.copy(uid = uid)
        
        InternalDb.addEmployee(employeeWithUid)
        currentUser = employeeWithUid // Auto log-in upon sign-up

        return employeeWithUid.toRole()
    }

    // ── Sign Out ─────────────────────────────────────────────────────

    fun signOut() {
        currentUser = null
    }
}
