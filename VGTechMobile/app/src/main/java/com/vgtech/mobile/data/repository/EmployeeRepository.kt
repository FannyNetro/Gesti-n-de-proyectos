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
            list.filter { it.activo }.sortedByDescending { it.fechaRegistro }
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

    // ── Soft-delete (deactivate) ─────────────────────────────────────

    /**
     * "Despedir" — marks the employee as inactive instead of hard-deleting.
     */
    suspend fun deactivateEmployee(uid: String) {
        val emp = InternalDb.getEmployeeById(uid)
        if (emp != null) {
            InternalDb.updateEmployee(emp.copy(activo = false))
        }
    }

    // ── Get employee count ───────────────────────────────────────────

    suspend fun getActiveCount(): Int {
        return InternalDb.employees.value.count { it.activo }
    }
}
