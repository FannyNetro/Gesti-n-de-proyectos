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
                tipoTrabajo = listOf("Construcción de Puentes"),
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
            ),
            Employee(
                uid = UUID.randomUUID().toString(),
                nombreCompleto = "Proveedor Limpieza",
                email = "limpieza@vgtech.com",
                direccion = "Av. Siempre Viva 1",
                telefono = "555-8899",
                puesto = "Proveedor",
                tipoTrabajo = listOf("Pavimentación y Asfaltado"),
                sueldo = 0.0,
                diasVacaciones = 0,
                password = "limp",
                fechaRegistro = System.currentTimeMillis(),
                activo = true
            ),
            Employee(
                uid = UUID.randomUUID().toString(),
                nombreCompleto = "Proveedor Seguridad",
                email = "seguridad@vgtech.com",
                direccion = "Calle Segura 4",
                telefono = "555-7777",
                puesto = "Proveedor",
                tipoTrabajo = listOf("Cimentación Profunda", "Obra Civil General"),
                sueldo = 0.0,
                diasVacaciones = 0,
                password = "segu",
                fechaRegistro = System.currentTimeMillis(),
                activo = true
            ),
            Employee(
                uid = UUID.randomUUID().toString(),
                nombreCompleto = "Consultor Externo 2",
                email = "consultor2@vgtech.com",
                direccion = "Oficina Central 90",
                telefono = "555-9090",
                puesto = "Consultor",
                sueldo = 35000.0,
                diasVacaciones = 5,
                password = "cons",
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

    fun deactivateEmployee(uid: String, motivo: String) {
        val emp = getEmployeeById(uid)
        if (emp != null) {
            updateEmployee(emp.copy(activo = false, motivoInactivo = motivo))
        }
    }

    fun updateEmployeePassword(uid: String, newPassword: String) {
        val emp = getEmployeeById(uid)
        if (emp != null) {
            updateEmployee(emp.copy(password = newPassword))
        }
    }
}
