package com.vgtech.mobile.data.repository

import com.vgtech.mobile.data.model.Employee
import com.vgtech.mobile.network.RetrofitClient
import com.vgtech.mobile.network.dto.CreateUsuarioDto
import com.vgtech.mobile.network.dto.UpdateUsuarioDto
import com.vgtech.mobile.network.dto.UsuarioDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * EmployeeRepository — conectado a PostgreSQL vía API REST Ktor.
 * getEmployees() retorna un Flow que emite la lista al ser coleccionado.
 */
class EmployeeRepository {

    // ── Lista de empleados ───────────────────────────────────────────────

    /**
     * Retorna un Flow con todos los empleados desde la API.
     * @param puesto filtro opcional: "RH", "SUPERVISOR", "CONSULTOR", etc.
     */
    fun getEmployees(puesto: String? = null): Flow<List<Employee>> = flow {
        val response = RetrofitClient.api.getUsuarios(puesto)
        if (response.isSuccessful) {
            emit(response.body()?.map { it.toEmployee() } ?: emptyList())
        } else {
            emit(emptyList())
        }
    }

    // ── Obtener empleado individual ──────────────────────────────────────

    suspend fun getEmployee(uid: String): Employee? {
        val response = RetrofitClient.api.getUsuario(uid)
        return if (response.isSuccessful) response.body()?.toEmployee() else null
    }

    // ── Crear empleado ───────────────────────────────────────────────────

    suspend fun registerEmployee(employee: Employee): String {
        val response = RetrofitClient.api.createUsuario(
            CreateUsuarioDto(
                nombreCompleto = employee.nombreCompleto,
                email          = employee.email,
                password       = employee.password,
                direccion      = employee.direccion,
                telefono       = employee.telefono,
                puesto         = employee.puesto,
                sueldo         = employee.sueldo,
                pagoPorHora    = employee.pagoPorHora,
                diasVacaciones = employee.diasVacaciones,
                fotoBase64     = employee.fotoBase64,
                tipoTrabajo    = employee.tipoTrabajo
            )
        )
        if (!response.isSuccessful) throw Exception("Error al crear empleado: ${response.code()}")
        return response.body()?.get("uid") ?: throw Exception("Respuesta vacía")
    }

    // ── Actualizar empleado ──────────────────────────────────────────────

    suspend fun updateEmployee(employee: Employee) {
        RetrofitClient.api.updateUsuario(
            employee.uid,
            UpdateUsuarioDto(
                nombreCompleto = employee.nombreCompleto,
                direccion      = employee.direccion,
                telefono       = employee.telefono,
                puesto         = employee.puesto,
                sueldo         = employee.sueldo,
                pagoPorHora    = employee.pagoPorHora,
                diasVacaciones = employee.diasVacaciones,
                activo         = employee.activo,
                motivoInactivo = employee.motivoInactivo,
                fotoBase64     = employee.fotoBase64
            )
        )
    }

    // ── Desactivar empleado ──────────────────────────────────────────────

    suspend fun deactivateEmployee(uid: String, motivo: String) {
        RetrofitClient.api.updateUsuario(
            uid,
            UpdateUsuarioDto(activo = false, motivoInactivo = motivo)
        )
    }

    // ── Resetear contraseña ──────────────────────────────────────────────

    suspend fun resetPassword(uid: String, newPassword: String) {
        RetrofitClient.api.updateUsuario(uid, UpdateUsuarioDto(password = newPassword))
    }

    // ── Mapper ──────────────────────────────────────────────────────────

    private fun UsuarioDto.toEmployee() = Employee(
        uid            = uid,
        nombreCompleto = nombreCompleto,
        email          = email,
        direccion      = direccion,
        telefono       = telefono,
        puesto         = puesto,
        sueldo         = sueldo,
        pagoPorHora    = pagoPorHora,
        diasVacaciones = diasVacaciones,
        activo         = activo,
        motivoInactivo = motivoInactivo,
        fotoBase64     = fotoBase64,
        tipoTrabajo    = tipoTrabajo
    )
}
