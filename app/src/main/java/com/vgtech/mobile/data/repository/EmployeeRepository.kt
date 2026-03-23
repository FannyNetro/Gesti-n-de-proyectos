package com.vgtech.mobile.data.repository

import com.vgtech.mobile.data.local.InternalDb
import com.vgtech.mobile.data.model.Employee
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * EmployeeRepository — Internal memory CRUD for the `employees` collection.
 */
class EmployeeRepository {

    // ── Real-time employee list ──────────────────────────────────────

    /**
     * Returns a Flow that emits the full list of active employees
     * every time the internal database changes.
     */
    fun getEmployees(): Flow<List<Employee>> {
        return InternalDb.employees.map { list ->
            list.sortedByDescending { it.fechaRegistro }
        }
    }

    // ── Get single employee ──────────────────────────────────────────

    suspend fun getEmployee(uid: String): Employee? {
        return InternalDb.getEmployeeById(uid)
    }

    // ── Update employee ──────────────────────────────────────────────

    suspend fun updateEmployee(employee: Employee) {
        InternalDb.updateEmployee(employee)
    }

    // ── Deactivate Employee ──────────────────────────────────────────
    suspend fun deactivateEmployee(uid: String, motivo: String) {
        // En mem (Mock):
        InternalDb.deactivateEmployee(uid, motivo)
        // En Firestore (Futuro):
        // employeeCollection.document(uid).update(mapOf("activo" to false, "motivoInactivo" to motivo)).await()
    }

    // ── Reset Employee Password ──────────────────────────────────────
    suspend fun resetPassword(uid: String, newPassword: String) {
        // En mem (Mock):
        InternalDb.updateEmployeePassword(uid, newPassword)
        // En Firebase Auth / Firestore (Futuro):
        // Se requeriría usar Firebase Admin SDK o functions si se desea cambiar la contraseña de otro usuario.
    }

    // ── Get employee count ───────────────────────────────────────────

    suspend fun getActiveCount(): Int {
        return InternalDb.employees.value.count { it.activo }
    }
}
