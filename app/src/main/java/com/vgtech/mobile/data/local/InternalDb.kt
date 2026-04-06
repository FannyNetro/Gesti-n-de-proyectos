package com.vgtech.mobile.data.local

import com.vgtech.mobile.data.model.*
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

    private val _projectProgressReports = MutableStateFlow<List<ProjectProgress>>(emptyList())
    val projectProgressReports: StateFlow<List<ProjectProgress>> = _projectProgressReports.asStateFlow()

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    init {
        // Pre-populate with a few mock users
        val namesRH = listOf("Ana Martínez", "Carlos Ruiz", "Sofía Vargas", "Miguel Torres", "Lucía Gómez", "Jorge Silva", "Elena Navarro", "Raúl Ortiz", "Carmen Mendoza", "Luis Ramos")
        val namesSuper = listOf("Fernando Reyes", "Patricia Luna", "Roberto Castro", "Diana Vega", "Andrés Ríos", "Valeria Soto", "Héctor Peña", "Laura Campos", "Gabriel Medina", "Silvia Cruz")

        val generatedUsers = mutableListOf<Employee>()

        // Admin account
        generatedUsers.add(
            Employee(
                uid = "admin-uid",
                nombreCompleto = "Admin RH User",
                email = "admin@vgtech.com",
                puesto = "RH",
                password = "admin",
                activo = true
            )
        )

        // Proveedor account
        val provUid = "prov-uid"
        generatedUsers.add(
            Employee(
                uid = provUid,
                nombreCompleto = "Proveedor General",
                email = "proveedor@vgtech.com",
                puesto = "Proveedor",
                password = "prov",
                activo = true
            )
        )

        // 10 Empleados RH
        for (i in 0 until 10) {
            generatedUsers.add(
                Employee(
                    uid = UUID.randomUUID().toString(),
                    nombreCompleto = namesRH[i],
                    email = "rh${i+1}@vgtech.com",
                    puesto = "RH",
                    password = "pass$i",
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
                    puesto = "Supervisor",
                    password = "super$i",
                    activo = true
                )
            )
        }

        _employees.value = generatedUsers

        // Mock Projects
        _projects.value = listOf(
            Project(
                id = "proj-1",
                title = "Edificio Reforma 222",
                description = "Instalación Eléctrica",
                providerUid = provUid,
                progress = 0.45f,
                status = "En Progreso"
            ),
            Project(
                id = "proj-2",
                title = "Torre Mitikah - Nivel 15",
                description = "Acabados",
                providerUid = provUid,
                progress = 0.10f,
                status = "Recién Iniciado"
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

    fun deactivateEmployee(uid: String, motivo: String) {
        val currentList = _employees.value.toMutableList()
        val index = currentList.indexOfFirst { it.uid == uid }
        if (index != -1) {
            val oldEmployee = currentList[index]
            currentList[index] = oldEmployee.copy(activo = false, motivoInactivo = motivo)
            _employees.value = currentList
        }
    }

    fun updateEmployeePassword(uid: String, newPassword: String) {
        val currentList = _employees.value.toMutableList()
        val index = currentList.indexOfFirst { it.uid == uid }
        if (index != -1) {
            val oldEmployee = currentList[index]
            currentList[index] = oldEmployee.copy(password = newPassword)
            _employees.value = currentList
        }
    }

    fun updateEmployeeRates(uid: String, sueldo: Double, pagoPorHora: Double) {
        val currentList = _employees.value.toMutableList()
        val index = currentList.indexOfFirst { it.uid == uid }
        if (index != -1) {
            val oldEmployee = currentList[index]
            currentList[index] = oldEmployee.copy(sueldo = sueldo, pagoPorHora = pagoPorHora)
            _employees.value = currentList
        }
    }

    fun getEmployeeByEmail(email: String): Employee? {
        return _employees.value.find { it.email == email }
    }

    fun getEmployeeById(uid: String): Employee? {
        return _employees.value.find { it.uid == uid }
    }

    fun addVacationRequest(request: VacationRequest) {
        val currentList = _vacationRequests.value.toMutableList()
        currentList.add(request)
        _vacationRequests.value = currentList
    }

    fun updateVacationStatus(requestId: String, status: VacationStatus) {
        val currentRequests = _vacationRequests.value.toMutableList()
        val index = currentRequests.indexOfFirst { it.id == requestId }
        if (index != -1) {
            val oldRequest = currentRequests[index]
            val updatedRequest = oldRequest.copy(status = status)
            currentRequests[index] = updatedRequest
            _vacationRequests.value = currentRequests
        }
    }

    fun addWorkLog(workLog: WorkLog) {
        val currentList = _workLogs.value.toMutableList()
        currentList.add(workLog)
        _workLogs.value = currentList
    }

    fun addProjectProgress(report: ProjectProgress) {
        // Add the report to history
        val currentReports = _projectProgressReports.value.toMutableList()
        currentReports.add(report)
        _projectProgressReports.value = currentReports

        // Update the project's general progress and PHASE/DESCRIPTION
        val currentProjects = _projects.value.toMutableList()
        val projectIndex = currentProjects.indexOfFirst { it.title == report.projectTitle }
        if (projectIndex != -1) {
            val oldProject = currentProjects[projectIndex]
            currentProjects[projectIndex] = oldProject.copy(
                progress = report.progressPercentage / 100f,
                description = report.description, // Updated the phase to match current activity
                status = if (report.progressPercentage >= 100) "Finalizado" else "En Progreso"
            )
            _projects.value = currentProjects
        }
    }
}
