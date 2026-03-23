package com.vgtech.mobile.data.local

import com.vgtech.mobile.data.model.Employee
import com.vgtech.mobile.data.model.VacationRequest
import com.vgtech.mobile.data.model.VacationStatus
import com.vgtech.mobile.data.model.WorkLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object InternalDb {
    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    private val _vacationRequests = MutableStateFlow<List<VacationRequest>>(emptyList())
    val vacationRequests: StateFlow<List<VacationRequest>> = _vacationRequests.asStateFlow()

    private val _workLogs = MutableStateFlow<List<WorkLog>>(emptyList())
    val workLogs: StateFlow<List<WorkLog>> = _workLogs.asStateFlow()

    init {
        // Pre-populate with a few mock users
        val namesRH = listOf("Ana Martínez", "Carlos Ruiz", "Sofía Vargas", "Miguel Torres", "Lucía Gómez", "Jorge Silva", "Elena Navarro", "Raúl Ortiz", "Carmen Mendoza", "Luis Ramos")
        val namesSuper = listOf("Fernando Reyes", "Patricia Luna", "Roberto Castro", "Diana Vega", "Andrés Ríos", "Valeria Soto", "Héctor Peña", "Laura Campos", "Gabriel Medina", "Silvia Cruz")
        val namesCons = listOf("Javier Escobar", "Mónica Pineda", "Daniela Fuentes", "Hugo Aguilar", "Ricardo Blanco", "Teresa Salinas", "Oscar Montes", "Gloria Salas", "Esteban Moya", "Rosa Varela")
        val namesProv = listOf("Constructora Los Andes", "Ingeniería López", "Puentes y Caminos S.A.", "Edificaciones Modernas", "Cimentaciones Profundas MX", "Asfaltos del Norte", "Obra Civil Integral", "Estructuras Metálicas Titan", "Constructora El Pedregal", "Desarrollos Inmobiliarios Zeta")
        val catsProv = listOf("Construcción de Puentes", "Edificación Vertical", "Cimentación Profunda", "Pavimentación y Asfaltado", "Obra Civil General", "Estructuras Metálicas")

        val generatedUsers = mutableListOf<Employee>()
        val baseDate = System.currentTimeMillis()

        // Admin account guaranteed for login
        generatedUsers.add(
            Employee(
                uid = "admin-uid",
                nombreCompleto = "Admin RH User",
                email = "admin@vgtech.com",
                direccion = "Sede Principal",
                telefono = "555-0000",
                puesto = "RH",
                sueldo = 80000.0,
                pagoPorHora = 450.0,
                diasVacaciones = 20,
                password = "admin",
                fechaRegistro = baseDate,
                activo = true
            )
        )

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
                    pagoPorHora = 250.0 + (i * 10),
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
                    pagoPorHora = 350.0,
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
                    pagoPorHora = 400.0,
                    diasVacaciones = 15,
                    password = "cons$i",
                    fechaRegistro = baseDate - (i * 86400000L),
                    activo = true
                )
            )
        }

        _employees.value = generatedUsers

        // Mock Vacation Requests
        _vacationRequests.value = listOf(
            VacationRequest(
                employeeUid = "admin-uid",
                employeeName = "Admin RH User",
                startDate = baseDate - 172800000L,
                endDate = baseDate - 86400000L,
                daysRequested = 2,
                status = VacationStatus.APPROVED,
                observations = "Vacaciones anuales"
            )
        )
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

    // ── Vacation Logic ───────────────────────────────────────────────

    fun addVacationRequest(request: VacationRequest) {
        val currentRequests = _vacationRequests.value.toMutableList()
        currentRequests.add(request)
        _vacationRequests.value = currentRequests
    }

    fun updateVacationStatus(requestId: String, status: VacationStatus) {
        val currentRequests = _vacationRequests.value.toMutableList()
        val index = currentRequests.indexOfFirst { it.id == requestId }
        if (index != -1) {
            val oldRequest = currentRequests[index]
            val updatedRequest = oldRequest.copy(status = status)
            currentRequests[index] = updatedRequest
            _vacationRequests.value = currentRequests

            if (status == VacationStatus.APPROVED) {
                val emp = getEmployeeById(oldRequest.employeeUid)
                if (emp != null) {
                    val newDays = (emp.diasVacaciones - oldRequest.daysRequested).coerceAtLeast(0)
                    updateEmployee(emp.copy(diasVacaciones = newDays))
                }
            }
        }
    }

    // ── Salary & Work Log Logic ──────────────────────────────────────

    fun addWorkLog(log: WorkLog) {
        val currentLogs = _workLogs.value.toMutableList()
        currentLogs.add(log)
        _workLogs.value = currentLogs
    }

    fun updateEmployeeRates(uid: String, sueldo: Double, pagoPorHora: Double) {
        val emp = getEmployeeById(uid)
        if (emp != null) {
            updateEmployee(emp.copy(sueldo = sueldo, pagoPorHora = pagoPorHora))
        }
    }
}
