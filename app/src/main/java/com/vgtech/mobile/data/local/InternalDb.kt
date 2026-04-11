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

    private val _cancellationRequests = MutableStateFlow<List<ProjectCancellationRequest>>(emptyList())
    val cancellationRequests: StateFlow<List<ProjectCancellationRequest>> = _cancellationRequests.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    init {
        val currentConsultantUid = "consultor-uid"
        val provUid = "prov-uid"

        val generatedUsers = mutableListOf<Employee>()
        generatedUsers.add(Employee(uid = "admin-uid", nombreCompleto = "Admin RH User", email = "admin@vgtech.com", puesto = "RH", password = "admin", activo = true))
        generatedUsers.add(Employee(uid = provUid, nombreCompleto = "Proveedor General", email = "proveedor@vgtech.com", puesto = "Proveedor", password = "prov", activo = true))
        generatedUsers.add(Employee(uid = currentConsultantUid, nombreCompleto = "Consultor Externo", email = "consultor@vgtech.com", puesto = "Consultor", password = "cons", activo = true))

        _employees.value = generatedUsers

        _projects.value = listOf(
            Project(
                id = "proj-101",
                title = "Hospital General - Fase 1",
                description = "Instalaciones iniciales y planos.",
                consultantUid = currentConsultantUid,
                providerUid = provUid,
                providerName = "Proveedor General",
                progress = 1.0f,
                status = "Finalizado",
                comments = "Excelente coordinación en la primera etapa. El proveedor cumplió con los estándares de seguridad.",
                hasDelays = false,
                providerRating = 4.8f,
                consultantRating = 5.0f,
                evaluationResult = "Aprobado con Distinción"
            ),
            Project(
                id = "proj-102",
                title = "Centro Logístico Norte",
                description = "Auditoría de cimentación.",
                consultantUid = currentConsultantUid,
                providerUid = provUid,
                providerName = "Proveedor General",
                progress = 1.0f,
                status = "Finalizado",
                comments = "Hubo problemas con el suministro de concreto, pero se resolvió.",
                hasDelays = true,
                delayReason = "Escasez de materiales de construcción en la zona.",
                providerRating = 3.5f,
                consultantRating = 4.2f,
                evaluationResult = "Satisfactorio"
            ),
            Project(
                id = "proj-1",
                title = "Hospital General - Fase 3",
                description = "Revisión de instalaciones de oxígeno.",
                consultantUid = currentConsultantUid,
                providerUid = provUid,
                providerName = "Proveedor General",
                progress = 0.85f,
                status = "En Progreso"
            ),
            Project(
                id = "proj-2",
                title = "Residencial Las Lomas",
                description = "Estructura principal.",
                consultantUid = currentConsultantUid,
                providerUid = provUid,
                providerName = "Proveedor General",
                progress = 0.50f,
                status = "En Progreso"
            )
        )
        
        // Mock some progress reports to evaluate
        _projectProgressReports.value = listOf(
            ProjectProgress(
                id = "report-1",
                projectId = "proj-1",
                projectTitle = "Hospital General - Fase 3",
                providerUid = provUid,
                providerName = "Proveedor General",
                progressPercentage = 85,
                description = "Instalación de tuberías de cobre completada al 90%.",
                reportType = "Diario",
                highlights = "Material de alta calidad.",
                issues = "Poca ventilación en el área."
            ),
            ProjectProgress(
                id = "report-2",
                projectId = "proj-2",
                projectTitle = "Residencial Las Lomas",
                providerUid = provUid,
                providerName = "Proveedor General",
                progressPercentage = 50,
                description = "Colado de losa de entrepiso.",
                reportType = "Semanal",
                highlights = "Buen tiempo de ejecución.",
                issues = ""
            )
        )
    }

    fun addEmployee(employee: Employee) {
        val currentList = _employees.value.toMutableList()
        currentList.add(employee)
        _employees.value = currentList
    }

    fun getEmployeeByEmail(email: String): Employee? {
        return _employees.value.find { it.email == email }
    }

    fun getEmployeeById(uid: String): Employee? {
        return _employees.value.find { it.uid == uid }
    }

    fun addProjectCancellationRequest(request: ProjectCancellationRequest) {
        val currentList = _cancellationRequests.value.toMutableList()
        currentList.add(request)
        _cancellationRequests.value = currentList
    }

    fun updateEmployee(employee: Employee) {
        val currentList = _employees.value.toMutableList()
        val index = currentList.indexOfFirst { it.uid == employee.uid }
        if (index != -1) {
            currentList[index] = employee
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

    fun deactivateEmployee(uid: String, motivo: String) {
        val currentList = _employees.value.toMutableList()
        val index = currentList.indexOfFirst { it.uid == uid }
        if (index != -1) {
            val oldEmployee = currentList[index]
            currentList[index] = oldEmployee.copy(activo = false, motivoInactivo = motivo)
            _employees.value = currentList
        }
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
        val currentReports = _projectProgressReports.value.toMutableList()
        currentReports.add(report)
        _projectProgressReports.value = currentReports
    }

    fun evaluateProgress(
        reportId: String, 
        evaluation: String, 
        comments: String, 
        rating: Float, 
        imageUrl: String?,
        modifiedProgress: Int? = null,
        modificationReason: String = ""
    ) {
        val currentReports = _projectProgressReports.value.toMutableList()
        val index = currentReports.indexOfFirst { it.id == reportId }
        if (index != -1) {
            val oldReport = currentReports[index]
            
            // 1. Update the report with evaluation and possible correction
            val updatedReport = oldReport.copy(
                evaluated = true,
                consultantEvaluation = evaluation,
                consultantComments = comments,
                evaluationRating = rating,
                evaluationImageUrl = imageUrl,
                wasModified = modifiedProgress != null,
                originalProgress = if (modifiedProgress != null) oldReport.progressPercentage else oldReport.originalProgress,
                progressPercentage = modifiedProgress ?: oldReport.progressPercentage,
                modificationReason = modificationReason
            )
            currentReports[index] = updatedReport
            _projectProgressReports.value = currentReports

            // 2. Sync with main project progress if changed
            if (modifiedProgress != null) {
                updateProjectProgress(oldReport.projectId, modifiedProgress / 100f)
            } else {
                updateProjectProgress(oldReport.projectId, oldReport.progressPercentage / 100f)
            }
        }
    }

    fun updateProjectProgress(projectId: String, newProgress: Float) {
        val currentProjects = _projects.value.toMutableList()
        val index = currentProjects.indexOfFirst { it.id == projectId }
        if (index != -1) {
            val oldProject = currentProjects[index]
            currentProjects[index] = oldProject.copy(progress = newProgress)
            _projects.value = currentProjects
        }
    }

    fun toggleProjectMark(projectId: String) {
        val currentProjects = _projects.value.toMutableList()
        val index = currentProjects.indexOfFirst { it.id == projectId }
        if (index != -1) {
            val oldProject = currentProjects[index]
            currentProjects[index] = oldProject.copy(isMarked = !oldProject.isMarked)
            _projects.value = currentProjects
        }
    }

    fun addChatMessage(message: ChatMessage) {
        val currentList = _chatMessages.value.toMutableList()
        currentList.add(message)
        _chatMessages.value = currentList
    }
}
