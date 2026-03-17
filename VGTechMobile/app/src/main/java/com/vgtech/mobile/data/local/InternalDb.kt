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
        val namesRH = listOf("Ana Martínez", "Carlos Ruiz", "Sofía Vargas", "Miguel Torres", "Lucía Gómez", "Jorge Silva", "Elena Navarro", "Raúl Ortiz", "Carmen Mendoza", "Luis Ramos")
        val namesSuper = listOf("Fernando Reyes", "Patricia Luna", "Roberto Castro", "Diana Vega", "Andrés Ríos", "Valeria Soto", "Héctor Peña", "Laura Campos", "Gabriel Medina", "Silvia Cruz")
        val namesCons = listOf("Javier Escobar", "Mónica Pineda", "Daniela Fuentes", "Hugo Aguilar", "Ricardo Blanco", "Teresa Salinas", "Oscar Montes", "Gloria Salas", "Esteban Moya", "Rosa Varela")
        val namesProv = listOf("Constructora Los Andes", "Ingeniería López", "Puentes y Caminos S.A.", "Edificaciones Modernas", "Cimentaciones Profundas MX", "Asfaltos del Norte", "Obra Civil Integral", "Estructuras Metálicas Titan", "Constructora El Pedregal", "Desarrollos Inmobiliarios Zeta")
        val catsProv = listOf("Construcción de Puentes", "Edificación Vertical", "Cimentación Profunda", "Pavimentación y Asfaltado", "Obra Civil General", "Estructuras Metálicas")

        val generatedUsers = mutableListOf<Employee>()
        val baseDate = System.currentTimeMillis()

        // 10 Empleados RH/Admin
        for (i in 0 until 10) {
            generatedUsers.add(
                Employee(
                    uid = UUID.randomUUID().toString(),
                    nombreCompleto = namesRH[i],
                    email = "rh${i+1}@vgtech.com",
                    direccion = "Dirección RH ${i+1}",
                    telefono = "555-10${i.toString().padStart(2, '0')}",
                    puesto = if (i % 2 == 0) "RH" else "Administrativo",
                    sueldo = 45000.0 + (i * 1000),
                    diasVacaciones = 10 + i,
                    password = "pass$i",
                    fechaRegistro = baseDate - (i * 86400000L),
                    activo = true
                )
            )
        }

        // 10 Supervisores
        for (i in 0 until 10) {
            generatedUsers.add(
                Employee(
                    uid = UUID.randomUUID().toString(),
                    nombreCompleto = namesSuper[i],
                    email = "super${i+1}@vgtech.com",
                    direccion = "Dirección Super ${i+1}",
                    telefono = "555-20${i.toString().padStart(2, '0')}",
                    puesto = "Supervisor",
                    sueldo = 55000.0 + (i * 1500),
                    diasVacaciones = 12,
                    password = "super$i",
                    fechaRegistro = baseDate - (i * 86400000L),
                    activo = true
                )
            )
        }

        // 10 Consultores
        for (i in 0 until 10) {
            generatedUsers.add(
                Employee(
                    uid = UUID.randomUUID().toString(),
                    nombreCompleto = namesCons[i],
                    email = "consul${i+1}@vgtech.com",
                    direccion = "Dirección Consultor ${i+1}",
                    telefono = "555-30${i.toString().padStart(2, '0')}",
                    puesto = "Consultor",
                    sueldo = 65000.0 + (i * 2000),
                    diasVacaciones = 15,
                    password = "cons$i",
                    fechaRegistro = baseDate - (i * 86400000L),
                    activo = true
                )
            )
        }

        // 10 Proveedores (Constructoras)
        for (i in 0 until 10) {
            val assignedCats = listOf(catsProv[i % catsProv.size], catsProv[(i + 1) % catsProv.size]).distinct()
            generatedUsers.add(
                Employee(
                    uid = UUID.randomUUID().toString(),
                    nombreCompleto = namesProv[i],
                    email = "prov${i+1}@constructora.com",
                    direccion = "Oficinas Centrales Prov ${i+1}",
                    telefono = "555-40${i.toString().padStart(2, '0')}",
                    puesto = "Proveedor",
                    tipoTrabajo = assignedCats,
                    sueldo = 0.0,
                    diasVacaciones = 0,
                    password = "prov$i",
                    fechaRegistro = baseDate - (i * 86400000L),
                    activo = true
                )
            )
        }

        _employees.value = generatedUsers
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
