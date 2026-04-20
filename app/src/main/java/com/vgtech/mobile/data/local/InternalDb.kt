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
        generatedUsers.add(Employee(uid = "admin-uid", nombreCompleto = "Admin RH User", email = "admin@vgtech.com", puesto = "RH", password = "admin", activo = true, sueldo = 25000.0, pagoPorHora = 156.25, diasVacaciones = 12.0))
        generatedUsers.add(Employee(uid = "sup-uid", nombreCompleto = "Carlos Mendoza", email = "supervisor@vgtech.com", puesto = "Supervisor", password = "super", activo = true, sueldo = 35000.0, pagoPorHora = 218.75, diasVacaciones = 14.0))
        generatedUsers.add(Employee(uid = provUid, nombreCompleto = "Proveedor General", email = "proveedor@vgtech.com", puesto = "Proveedor", password = "prov", activo = true, tipoTrabajo = listOf("Instalaciones", "Obra Civil"), sueldo = 0.0, pagoPorHora = 180.0, diasVacaciones = 0.0))
        generatedUsers.add(Employee(uid = "prov-2", nombreCompleto = "Materiales del Norte", email = "norte@vgtech.com", puesto = "Proveedor", password = "prov2", activo = true, tipoTrabajo = listOf("Materiales", "Acabados"), sueldo = 0.0, pagoPorHora = 160.0, diasVacaciones = 0.0))
        generatedUsers.add(Employee(uid = "prov-3", nombreCompleto = "Electro Servicios MX", email = "electro@vgtech.com", puesto = "Proveedor", password = "prov3", activo = true, tipoTrabajo = listOf("Eléctrico", "Iluminación"), sueldo = 0.0, pagoPorHora = 200.0, diasVacaciones = 0.0))
        generatedUsers.add(Employee(uid = currentConsultantUid, nombreCompleto = "Consultor Externo", email = "consultor@vgtech.com", puesto = "Consultor", password = "cons", activo = true, sueldo = 28000.0, pagoPorHora = 175.0, diasVacaciones = 10.0))
        generatedUsers.add(Employee(uid = "cons-2", nombreCompleto = "Ana García López", email = "ana@vgtech.com", puesto = "Consultor", password = "cons2", activo = true, sueldo = 30000.0, pagoPorHora = 187.50, diasVacaciones = 12.0))
        generatedUsers.add(Employee(uid = "cons-3", nombreCompleto = "Roberto Díaz Martín", email = "roberto@vgtech.com", puesto = "Consultor", password = "cons3", activo = true, sueldo = 26000.0, pagoPorHora = 162.50, diasVacaciones = 8.0))
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
                amount = 450000.0,
                estimatedDays = 45,
                description = "Incluye cableado y tableros.",
                sentToClient = true
            )
        )
    }

    // ── Employee Methods ──────────────────────────
    fun getEmployeeById(uid: String): Employee? {
        return _employees.value.find { it.uid == uid }
    }

    fun getEmployeeByEmail(email: String): Employee? {
        return _employees.value.find { it.email == email }
    }

    fun addEmployee(employee: Employee) {
        _employees.value += employee
    }

    fun updateEmployee(updated: Employee) {
        _employees.value = _employees.value.map { if (it.uid == updated.uid) updated else it }
    }

    fun deactivateEmployee(uid: String, motivo: String = "") {
        val employee = getEmployeeById(uid) ?: return
        updateEmployee(employee.copy(activo = false, motivoInactivo = motivo))
    }

    fun updateEmployeePassword(uid: String, newPass: String) {
        val employee = getEmployeeById(uid) ?: return
        updateEmployee(employee.copy(password = newPass))
    }

    fun updateEmployeeRates(uid: String, sueldo: Double, pagoPorHora: Double) {
        val employee = getEmployeeById(uid) ?: return
        updateEmployee(employee.copy(sueldo = sueldo, pagoPorHora = pagoPorHora))
    }

    // ── Vacation Methods ──────────────────────────
    fun addVacationRequest(req: VacationRequest) {
        _vacationRequests.value += req
    }

    fun updateVacationRequest(updated: VacationRequest) {
        _vacationRequests.value = _vacationRequests.value.map { if (it.id == updated.id) updated else it }
    }

    fun updateVacationStatus(requestId: String, status: VacationStatus) {
        val request = _vacationRequests.value.find { it.id == requestId } ?: return
        val updatedRequest = request.copy(status = status)
        updateVacationRequest(updatedRequest)

        // If approved, deduct days from employee
        if (status == VacationStatus.APPROVED) {
            val employee = getEmployeeById(request.employeeUid)
            employee?.let {
                updateEmployee(it.copy(diasVacaciones = it.diasVacaciones - request.effectiveDays))
            }
        }
    }

    // ── Work Log Methods ──────────────────────────
    fun addWorkLog(log: WorkLog) {
        _workLogs.value += log
    }

    // ── Project Methods ──────────────────────────
    fun updateProject(updated: Project) {
        _projects.value = _projects.value.map { if (it.id == updated.id) updated else it }
    }

    fun assignConsultantToProject(projectId: String, consultantUid: String) {
        val project = _projects.value.find { it.id == projectId } ?: return
        updateProject(project.copy(consultantUid = consultantUid))
    }

    fun assignProviderToProject(projectId: String, providerUid: String, providerName: String) {
        val project = _projects.value.find { it.id == projectId } ?: return
        updateProject(project.copy(providerUid = providerUid, providerName = providerName, status = "En Progreso"))
    }

    fun toggleProjectMark(projectId: String) {
        val project = _projects.value.find { it.id == projectId } ?: return
        updateProject(project.copy(isMarked = !project.isMarked))
    }

    fun updateProjectProgress(projectId: String, progress: Float, status: String = "En Progreso") {
        val project = _projects.value.find { it.id == projectId } ?: return
        updateProject(project.copy(progress = progress, status = status))
    }

    fun addProjectProgress(report: ProjectProgress) {
        _projectProgressReports.value += report
    }

    fun evaluateProgress(
        reportId: String,
        status: String = "Aprobado",
        comment: String = "",
        rating: Float = 5f,
        imageUrl: String? = null,
        modProgress: Int? = null,
        modReason: String = ""
    ) {
        _projectProgressReports.value = _projectProgressReports.value.map {
            if (it.id == reportId) {
                it.copy(
                    consultantComments = comment,
                    evaluated = true,
                    consultantEvaluation = status,
                    evaluationRating = rating,
                    evaluationImageUrl = imageUrl,
                    wasModified = modProgress != null,
                    originalProgress = if (modProgress != null) it.progressPercentage else it.originalProgress,
                    progressPercentage = modProgress ?: it.progressPercentage,
                    modificationReason = modReason
                )
            } else it
        }
    }

    fun addProjectCancellationRequest(req: ProjectCancellationRequest) {
        _cancellationRequests.value += req
    }

    fun updateProjectCancellationRequest(updated: ProjectCancellationRequest) {
        _cancellationRequests.value = _cancellationRequests.value.map { if (it.id == updated.id) updated else it }
    }

    // ── Chat Methods ──────────────────────────
    fun addChatMessage(msg: ChatMessage) {
        _chatMessages.value += msg
    }

    // ── Invitation & Quotation Methods ──────────────────────────
    fun addInvitation(inv: ProviderInvitation) {
        _invitations.value += inv
    }

    fun updateInvitation(updated: ProviderInvitation) {
        _invitations.value = _invitations.value.map { if (it.id == updated.id) updated else it }
    }

    fun addQuotation(quot: Quotation) {
        _quotations.value += quot
    }

    fun markQuotationSentToClient(quotationId: String) {
        _quotations.value = _quotations.value.map {
            if (it.id == quotationId) it.copy(sentToClient = true) else it
        }
    }

    fun updateQuotationClientStatus(quotationId: String, status: String) {
        _quotations.value = _quotations.value.map {
            if (it.id == quotationId) it.copy(clientStatus = status) else it
        }
    }

    fun addEvaluation(eval: PerformanceEvaluation) {
        _evaluations.value += eval
    }

    fun addPerformanceEvaluation(eval: PerformanceEvaluation) {
        _evaluations.value += eval
    }
}
