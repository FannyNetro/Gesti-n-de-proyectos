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

    // ── Update employee ──────────────────────────────────────────────

    fun updateEmployee(employee: Employee) {
        InternalDb.updateEmployee(employee)
    }

    // ── Deactivate Employee ──────────────────────────────────────────
    fun deactivateEmployee(uid: String, motivo: String) {
        InternalDb.deactivateEmployee(uid, motivo)
    }

    // ── Reset Employee Password ──────────────────────────────────────
    fun resetPassword(uid: String, newPassword: String) {
        InternalDb.updateEmployeePassword(uid, newPassword)
    }
}
