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

    // ── Supervisor-specific stores ────────────────────────────────
    private val _invitations = MutableStateFlow<List<ProviderInvitation>>(emptyList())
    val invitations: StateFlow<List<ProviderInvitation>> = _invitations.asStateFlow()

    private val _quotations = MutableStateFlow<List<Quotation>>(emptyList())
    val quotations: StateFlow<List<Quotation>> = _quotations.asStateFlow()

    private val _evaluations = MutableStateFlow<List<PerformanceEvaluation>>(emptyList())
    val evaluations: StateFlow<List<PerformanceEvaluation>> = _evaluations.asStateFlow()

    init {
        val currentConsultantUid = "consultor-uid"
        val provUid = "prov-uid"

        // ── Expanded seed users ──────────────────────────────────
        val generatedUsers = mutableListOf<Employee>()
        generatedUsers.add(Employee(uid = "admin-uid", nombreCompleto = "Admin RH User", email = "admin@vgtech.com", puesto = "RH", password = "admin", activo = true, sueldo = 25000.0, pagoPorHora = 156.25, diasVacaciones = 12))
        generatedUsers.add(Employee(uid = "sup-uid", nombreCompleto = "Carlos Mendoza", email = "supervisor@vgtech.com", puesto = "Supervisor", password = "super", activo = true, sueldo = 35000.0, pagoPorHora = 218.75, diasVacaciones = 14))
        generatedUsers.add(Employee(uid = provUid, nombreCompleto = "Proveedor General", email = "proveedor@vgtech.com", puesto = "Proveedor", password = "prov", activo = true, tipoTrabajo = listOf("Instalaciones", "Obra Civil"), sueldo = 0.0, pagoPorHora = 180.0, diasVacaciones = 0))
        generatedUsers.add(Employee(uid = "prov-2", nombreCompleto = "Materiales del Norte", email = "norte@vgtech.com", puesto = "Proveedor", password = "prov2", activo = true, tipoTrabajo = listOf("Materiales", "Acabados"), sueldo = 0.0, pagoPorHora = 160.0, diasVacaciones = 0))
        generatedUsers.add(Employee(uid = "prov-3", nombreCompleto = "Electro Servicios MX", email = "electro@vgtech.com", puesto = "Proveedor", password = "prov3", activo = true, tipoTrabajo = listOf("Eléctrico", "Iluminación"), sueldo = 0.0, pagoPorHora = 200.0, diasVacaciones = 0))
        generatedUsers.add(Employee(uid = currentConsultantUid, nombreCompleto = "Consultor Externo", email = "consultor@vgtech.com", puesto = "Consultor", password = "cons", activo = true, sueldo = 28000.0, pagoPorHora = 175.0, diasVacaciones = 10))
        generatedUsers.add(Employee(uid = "cons-2", nombreCompleto = "Ana García López", email = "ana@vgtech.com", puesto = "Consultor", password = "cons2", activo = true, sueldo = 30000.0, pagoPorHora = 187.50, diasVacaciones = 12))
        generatedUsers.add(Employee(uid = "cons-3", nombreCompleto = "Roberto Díaz Martín", email = "roberto@vgtech.com", puesto = "Consultor", password = "cons3", activo = true, sueldo = 26000.0, pagoPorHora = 162.50, diasVacaciones = 8))
        generatedUsers.add(Employee(uid = "cliente-uid", nombreCompleto = "Cliente Corporativo", email = "cliente@vgtech.com", puesto = "Cliente", password = "cli", activo = true))

        _employees.value = generatedUsers

        // ── Expanded seed projects ───────────────────────────────
        _projects.value = listOf(
            Project(
                id = "proj-101",
                title = "Hospital General - Fase 1",
                description = "Instalaciones iniciales y planos.",
                consultantUid = currentConsultantUid,
                providerUid = provUid,
                providerName = "Proveedor General",
                supervisorUid = "sup-uid",
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
                supervisorUid = "sup-uid",
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
                supervisorUid = "sup-uid",
                progress = 0.85f,
                status = "En Progreso"
            ),
            Project(
                id = "proj-2",
                title = "Residencial Las Lomas",
                description = "Estructura principal.",
                consultantUid = "cons-2",
                providerUid = provUid,
                providerName = "Proveedor General",
                supervisorUid = "sup-uid",
                progress = 0.50f,
                status = "En Progreso"
            ),
            Project(
                id = "proj-3",
                title = "Torre Corporativa Alfa",
                description = "Diseño y supervisión de obra.",
                consultantUid = "cons-3",
                providerUid = "prov-2",
                providerName = "Materiales del Norte",
                supervisorUid = "sup-uid",
                progress = 0.30f,
                status = "En Progreso",
                hasDelays = true,
                delayReason = "Retraso en permisos municipales."
            ),
            Project(
                id = "proj-4",
                title = "Plaza Comercial Sur",
                description = "Instalaciones eléctricas y acabados.",
                consultantUid = null,
                providerUid = null,
                providerName = "",
                supervisorUid = "sup-uid",
                progress = 0f,
                status = "Pendiente"
            ),
            Project(
                id = "proj-5",
                title = "Parque Industrial Oriente",
                description = "Cimentación y estructura metálica.",
                consultantUid = "cons-2",
                providerUid = "prov-3",
                providerName = "Electro Servicios MX",
                supervisorUid = "sup-uid",
                progress = 0.15f,
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
                issues = "Poca ventilación en el área.",
                date = System.currentTimeMillis() - 86_400_000 // 1 day ago
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
                issues = "",
                date = System.currentTimeMillis() - 172_800_000 // 2 days ago
            ),
            ProjectProgress(
                id = "report-3",
                projectId = "proj-3",
                projectTitle = "Torre Corporativa Alfa",
                providerUid = "prov-2",
                providerName = "Materiales del Norte",
                progressPercentage = 30,
                description = "Excavación y preparación de terreno.",
                reportType = "Semanal",
                highlights = "Terreno estable.",
                issues = "Retraso por permisos.",
                delayReason = "Permisos municipales pendientes.",
                date = System.currentTimeMillis() - 259_200_000 // 3 days ago
            ),
            ProjectProgress(
                id = "report-4",
                projectId = "proj-5",
                projectTitle = "Parque Industrial Oriente",
                providerUid = "prov-3",
                providerName = "Electro Servicios MX",
                progressPercentage = 15,
                description = "Tendido de cableado principal.",
                reportType = "Diario",
                highlights = "Materiales de primera.",
                issues = "",
                date = System.currentTimeMillis() - 43_200_000 // 12 hours ago
            )
        )

        // ── Seed invitations ─────────────────────────────────────
        _invitations.value = listOf(
            ProviderInvitation(
                id = "inv-1",
                projectId = "proj-4",
                projectTitle = "Plaza Comercial Sur",
                providerUid = "prov-3",
                providerName = "Electro Servicios MX",
                supervisorUid = "sup-uid",
                message = "Invitación para cotizar instalaciones eléctricas.",
                status = "Cotizada"
            )
        )

        // ── Seed quotations ──────────────────────────────────────
        _quotations.value = listOf(
            Quotation(
                id = "quot-1",
                invitationId = "inv-1",
                projectId = "proj-4",
                projectTitle = "Plaza Comercial Sur",
                providerUid = "prov-3",
                providerName = "Electro Servicios MX",
                amount = 450_000.0,
                estimatedDays = 45,
                description = "Incluye cableado, tableros y luminarias LED.",
                sentToClient = true
            ),
            Quotation(
                id = "quot-2",
                invitationId = "inv-2",
                projectId = "proj-2",
                projectTitle = "Residencial Las Lomas",
                providerUid = "prov-2",
                providerName = "Materiales del Norte",
                amount = 890_000.0,
                estimatedDays = 60,
                description = "Estructura principal y cimentación profunda. Materiales premium.",
                sentToClient = true
            )
        )

        // ── Seed evaluations ─────────────────────────────────────
        _evaluations.value = listOf(
            PerformanceEvaluation(
                id = "eval-1",
                evaluatedUid = currentConsultantUid,
                evaluatedName = "Consultor Externo",
                evaluatedRole = "Consultor",
                projectId = "proj-101",
                projectTitle = "Hospital General - Fase 1",
                qualityRating = 5f,
                timelinessRating = 5f,
                communicationRating = 4.5f,
                overallRating = 4.8f,
                comments = "Excelente desempeño."
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

    // ── Supervisor CRUD functions ─────────────────────────────────

    fun addProject(project: Project) {
        val currentList = _projects.value.toMutableList()
        currentList.add(project)
        _projects.value = currentList
    }

    fun updateProject(project: Project) {
        val currentList = _projects.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == project.id }
        if (index != -1) {
            currentList[index] = project
            _projects.value = currentList
        }
    }

    fun assignConsultantToProject(projectId: String, consultantUid: String) {
        val currentProjects = _projects.value.toMutableList()
        val index = currentProjects.indexOfFirst { it.id == projectId }
        if (index != -1) {
            val old = currentProjects[index]
            currentProjects[index] = old.copy(consultantUid = consultantUid)
            _projects.value = currentProjects
        }
    }

    fun assignProviderToProject(projectId: String, providerUid: String, providerName: String) {
        val currentProjects = _projects.value.toMutableList()
        val index = currentProjects.indexOfFirst { it.id == projectId }
        if (index != -1) {
            val old = currentProjects[index]
            currentProjects[index] = old.copy(providerUid = providerUid, providerName = providerName)
            _projects.value = currentProjects
        }
    }

    fun addInvitation(invitation: ProviderInvitation) {
        val currentList = _invitations.value.toMutableList()
        currentList.add(invitation)
        _invitations.value = currentList
    }

    fun addQuotation(quotation: Quotation) {
        val currentList = _quotations.value.toMutableList()
        currentList.add(quotation)
        _quotations.value = currentList
    }

    fun markQuotationSentToClient(quotationId: String) {
        val currentList = _quotations.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == quotationId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(sentToClient = true)
            _quotations.value = currentList
        }
    }

    fun updateQuotationClientStatus(quotationId: String, status: String) {
        val currentList = _quotations.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == quotationId }
        val quotation = currentList.getOrNull(index)
        if (quotation != null) {
            currentList[index] = quotation.copy(clientStatus = status)
            _quotations.value = currentList
            
            // If the client approves the quotation, the project goes to "En Progreso"
            // and we assign the provider if not already
            if (status == "Aprobada") {
                val currentProjects = _projects.value.toMutableList()
                val projIndex = currentProjects.indexOfFirst { it.id == quotation.projectId }
                if (projIndex != -1) {
                    val p = currentProjects[projIndex]
                    currentProjects[projIndex] = p.copy(
                        status = "En Progreso",
                        providerUid = quotation.providerUid,
                        providerName = quotation.providerName
                    )
                    _projects.value = currentProjects
                }
            }
        }
    }

    fun addPerformanceEvaluation(evaluation: PerformanceEvaluation) {
        val currentList = _evaluations.value.toMutableList()
        currentList.add(evaluation)
        _evaluations.value = currentList
    }

    fun evaluateProject(projectId: String, providerRating: Float, consultantRating: Float, evaluationResult: String, comments: String) {
        val currentProjects = _projects.value.toMutableList()
        val index = currentProjects.indexOfFirst { it.id == projectId }
        if (index != -1) {
            val old = currentProjects[index]
            currentProjects[index] = old.copy(
                providerRating = providerRating,
                consultantRating = consultantRating,
                evaluationResult = evaluationResult,
                comments = comments
            )
            _projects.value = currentProjects
        }
    }
}

