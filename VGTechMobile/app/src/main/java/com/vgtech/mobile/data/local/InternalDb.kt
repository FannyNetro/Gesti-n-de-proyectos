package com.vgtech.mobile.data.local

import com.vgtech.mobile.data.model.Employee
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object InternalDb {
    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    init {
        // Pre-populate with a few mock users
        val mockUsers = listOf(
            Employee(
                uid = UUID.randomUUID().toString(),
                nombreCompleto = "Admin RH User",
                email = "admin@vgtech.com",
                direccion = "Calle Principal 123",
                telefono = "555-1234",
                puesto = "RH",
                sueldo = 50000.0,
                diasVacaciones = 15,
                password = "admin",
                fechaRegistro = System.currentTimeMillis(),
                activo = true
            ),
            Employee(
                uid = UUID.randomUUID().toString(),
                nombreCompleto = "Supervisor User",
                email = "super@vgtech.com",
                direccion = "Avenida Central 456",
                telefono = "555-5678",
                puesto = "Supervisor",
                sueldo = 40000.0,
                diasVacaciones = 10,
                password = "super",
                fechaRegistro = System.currentTimeMillis(),
                activo = true
            ),
            Employee(
                uid = UUID.randomUUID().toString(),
                nombreCompleto = "Consultor User",
                email = "consultor@vgtech.com",
                direccion = "Plaza Norte 789",
                telefono = "555-9012",
                puesto = "Consultor",
                sueldo = 30000.0,
                diasVacaciones = 5,
                password = "cons",
                fechaRegistro = System.currentTimeMillis(),
                activo = true
            ),
            Employee(
                uid = UUID.randomUUID().toString(),
                nombreCompleto = "Proveedor User",
                email = "proveedor@vgtech.com",
                direccion = "Av. Industrial 12",
                telefono = "555-3344",
                puesto = "Proveedor",
                sueldo = 0.0,
                diasVacaciones = 0,
                password = "prov",
                fechaRegistro = System.currentTimeMillis(),
                activo = true
            ),
            Employee(
                uid = UUID.randomUUID().toString(),
                nombreCompleto = "Cliente Empresa SA",
                email = "cliente@vgtech.com",
                direccion = "Edificio Corporativo 4",
                telefono = "555-5566",
                puesto = "Cliente",
                sueldo = 0.0,
                diasVacaciones = 0,
                password = "clie",
                fechaRegistro = System.currentTimeMillis(),
                activo = true
            )
        )
        _employees.value = mockUsers
    }

    fun addEmployee(employee: Employee) {
        val currentList = _employees.value.toMutableList()
        currentList.add(employee)
        _employees.value = currentList
    }

    fun updateEmployee(employee: Employee) {
        val currentList = _employees.value.toMutableList()
        val index = currentList.indexOfFirst { it.uid == employee.uid }
        if (index != -1) {
            currentList[index] = employee
            _employees.value = currentList
        }
    }

    fun getEmployeeByEmail(email: String): Employee? {
        return _employees.value.find { it.email == email }
    }

    fun getEmployeeById(uid: String): Employee? {
        return _employees.value.find { it.uid == uid }
    }
}
