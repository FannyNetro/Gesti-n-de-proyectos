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

        // ── Empleados RH / Administrativos ───────────────────────────────
        // Coherencia salarial:
        //   RH/Admin:     $18,000–$28,000/mes | $112–$175/h
        //   Supervisor:   $32,000–$42,000/mes | $200–$262/h
        //   Consultor:    $26,000–$35,000/mes | $162–$218/h
        //   Proveedores:  Sin sueldo fijo — se paga por servicio (50/50 del ingreso del cliente)
        val generatedUsers = mutableListOf<Employee>()

        // ── RH & Administrativos ─────────────────────────────────────────
        generatedUsers.add(Employee(uid = "admin-uid",   nombreCompleto = "Laura Ramírez Torres",   email = "admin@vgtech.com",       puesto = "RH",             password = "admin",  activo = true, sueldo = 25000.0, pagoPorHora = 156.25, diasVacaciones = 12.0))
        generatedUsers.add(Employee(uid = "adm-2",       nombreCompleto = "Jorge Herrera Salinas",  email = "jorge.h@vgtech.com",     puesto = "Administrativo", password = "adm2",   activo = true, sueldo = 20000.0, pagoPorHora = 125.00, diasVacaciones = 10.0))
        generatedUsers.add(Employee(uid = "adm-3",       nombreCompleto = "Patricia Vela Muñoz",    email = "patricia.v@vgtech.com",  puesto = "Administrativo", password = "adm3",   activo = true, sueldo = 18500.0, pagoPorHora = 115.62, diasVacaciones = 10.0))
        generatedUsers.add(Employee(uid = "adm-4",       nombreCompleto = "Miguel Ángel Cruz",      email = "miguel.c@vgtech.com",    puesto = "Empleado",       password = "adm4",   activo = true, sueldo = 16000.0, pagoPorHora = 100.00, diasVacaciones = 8.0))
        generatedUsers.add(Employee(uid = "adm-5",       nombreCompleto = "Sofía Mendez Arriaga",   email = "sofia.m@vgtech.com",     puesto = "Empleado",       password = "adm5",   activo = true, sueldo = 16500.0, pagoPorHora = 103.12, diasVacaciones = 8.0))

        // ── Supervisores ─────────────────────────────────────────────────
        generatedUsers.add(Employee(uid = "sup-uid",     nombreCompleto = "Carlos Mendoza Ríos",    email = "supervisor@vgtech.com",  puesto = "Supervisor",     password = "super",  activo = true, sueldo = 38000.0, pagoPorHora = 237.50, diasVacaciones = 15.0))
        generatedUsers.add(Employee(uid = "sup-2",       nombreCompleto = "Alejandro Fuentes",      email = "alejandro.f@vgtech.com", puesto = "Supervisor",     password = "sup2",   activo = true, sueldo = 35000.0, pagoPorHora = 218.75, diasVacaciones = 14.0))
        generatedUsers.add(Employee(uid = "sup-3",       nombreCompleto = "Daniela Ortiz Peña",     email = "daniela.o@vgtech.com",   puesto = "Supervisor",     password = "sup3",   activo = true, sueldo = 33000.0, pagoPorHora = 206.25, diasVacaciones = 12.0))

        // ── Consultores ──────────────────────────────────────────────────
        generatedUsers.add(Employee(uid = currentConsultantUid, nombreCompleto = "Fernando Castillo",  email = "consultor@vgtech.com",   puesto = "Consultor",  password = "cons",   activo = true, sueldo = 32000.0, pagoPorHora = 200.00, diasVacaciones = 12.0))
        generatedUsers.add(Employee(uid = "cons-2",      nombreCompleto = "Ana García López",        email = "ana@vgtech.com",          puesto = "Consultor",  password = "cons2",  activo = true, sueldo = 30000.0, pagoPorHora = 187.50, diasVacaciones = 12.0))
        generatedUsers.add(Employee(uid = "cons-3",      nombreCompleto = "Roberto Díaz Martín",    email = "roberto@vgtech.com",      puesto = "Consultor",  password = "cons3",  activo = true, sueldo = 28000.0, pagoPorHora = 175.00, diasVacaciones = 10.0))
        generatedUsers.add(Employee(uid = "cons-4",      nombreCompleto = "Isabel Navarro Vega",    email = "isabel.n@vgtech.com",     puesto = "Consultor",  password = "cons4",  activo = true, sueldo = 26500.0, pagoPorHora = 165.62, diasVacaciones = 10.0))

        // ── Proveedores (pago por servicio, sueldo = 0) ──────────────────
        // Cada proyecto genera un ingreso del cliente. La mitad va al proveedor (50/50).
        // Transacciones en ProviderTxDb reflejan los proyectos reales.
        generatedUsers.add(Employee(uid = provUid,       nombreCompleto = "Constructora Pérez S.A.", email = "proveedor@vgtech.com",   puesto = "Proveedor",  password = "prov",   activo = true, tipoTrabajo = listOf("Obra Civil", "Cimentación"), sueldo = 0.0, pagoPorHora = 0.0, diasVacaciones = 0.0))
        generatedUsers.add(Employee(uid = "prov-2",      nombreCompleto = "Materiales del Norte",    email = "norte@vgtech.com",        puesto = "Proveedor",  password = "prov2",  activo = true, tipoTrabajo = listOf("Materiales", "Acabados"),    sueldo = 0.0, pagoPorHora = 0.0, diasVacaciones = 0.0))
        generatedUsers.add(Employee(uid = "prov-3",      nombreCompleto = "Electro Servicios MX",    email = "electro@vgtech.com",      puesto = "Proveedor",  password = "prov3",  activo = true, tipoTrabajo = listOf("Eléctrico", "Iluminación"),  sueldo = 0.0, pagoPorHora = 0.0, diasVacaciones = 0.0))
        generatedUsers.add(Employee(uid = "prov-4",      nombreCompleto = "Hidráulica Integral",     email = "hidro@vgtech.com",        puesto = "Proveedor",  password = "prov4",  activo = true, tipoTrabajo = listOf("Plomería", "Hidráulica"),    sueldo = 0.0, pagoPorHora = 0.0, diasVacaciones = 0.0))
        generatedUsers.add(Employee(uid = "prov-5",      nombreCompleto = "Arquitectura & Diseño MX",email = "arqdmx@vgtech.com",       puesto = "Proveedor",  password = "prov5",  activo = true, tipoTrabajo = listOf("Diseño", "Acabados"),        sueldo = 0.0, pagoPorHora = 0.0, diasVacaciones = 0.0))

        // ── Cliente ──────────────────────────────────────────────────────
        generatedUsers.add(Employee(uid = "cliente-uid", nombreCompleto = "Grupo Empresarial León",  email = "cliente@vgtech.com",      puesto = "Cliente",    password = "cli",    activo = true))

        _employees.value = generatedUsers

        // ── Proyectos coherentes con proveedores y transacciones ─────────
        // Cada proyecto tiene un presupuesto del cliente. El 50% va al proveedor asignado.
        // Los montos de las transacciones en ProviderTxDb corresponden a estos proyectos.
        //
        // Resumen financiero (ingreso del cliente → 50% empresa / 50% proveedor):
        //   Hospital General – Fase 1 : $360,000 cliente → prov-uid  $180,000 (PAGADO)
        //   Hospital General – Fase 2 : $240,000 cliente → prov-uid  $120,000 (PENDIENTE $60,000)
        //   Residencial Las Lomas     : $500,000 cliente → prov-2    $250,000 (PENDIENTE $150,000)
        //   Torre Corporativa Alfa    : $420,000 cliente → prov-2    $210,000 (PENDIENTE $210,000)
        //   Plaza Comercial Sur       : $300,000 cliente → prov-3    $150,000 (PAGADO)
        //   Parque Industrial Oriente : $280,000 cliente → prov-3    $140,000 (PENDIENTE $80,000)
        //   Clínica Privada Norte     : $180,000 cliente → prov-4    $90,000  (PENDIENTE $90,000)
        //   Centro Educativo Federal  : $220,000 cliente → prov-5    $110,000 (PAGADO $110,000)
        _projects.value = listOf(
            // ── FINALIZADOS ─────────────────────────────────────────────
            Project(
                id = "proj-101",
                title = "Hospital General – Fase 1",
                description = "Obra civil e instalaciones iniciales. Presupuesto cliente: \$360,000.",
                consultantUid = currentConsultantUid,
                providerUid = provUid,
                providerName = "Constructora Pérez S.A.",
                supervisorUid = "sup-uid",
                progress = 1.0f,
                status = "Finalizado",
                comments = "Entrega puntual. El proveedor cumplió con los estándares de seguridad.",
                hasDelays = false,
                providerRating = 4.8f,
                consultantRating = 5.0f,
                evaluationResult = "Aprobado con Distinción"
            ),
            Project(
                id = "proj-102",
                title = "Plaza Comercial Sur",
                description = "Instalaciones eléctricas completas. Presupuesto cliente: \$300,000.",
                consultantUid = "cons-2",
                providerUid = "prov-3",
                providerName = "Electro Servicios MX",
                supervisorUid = "sup-2",
                progress = 1.0f,
                status = "Finalizado",
                comments = "Excelente trabajo eléctrico. Sin observaciones.",
                hasDelays = false,
                providerRating = 4.9f,
                consultantRating = 4.7f,
                evaluationResult = "Aprobado"
            ),
            Project(
                id = "proj-103",
                title = "Centro Educativo Federal",
                description = "Diseño arquitectónico y acabados. Presupuesto cliente: \$220,000.",
                consultantUid = "cons-4",
                providerUid = "prov-5",
                providerName = "Arquitectura & Diseño MX",
                supervisorUid = "sup-3",
                progress = 1.0f,
                status = "Finalizado",
                comments = "Acabados de alta calidad. Cliente satisfecho.",
                hasDelays = false,
                providerRating = 4.6f,
                consultantRating = 4.8f,
                evaluationResult = "Aprobado con Distinción"
            ),
            // ── EN PROGRESO ─────────────────────────────────────────────
            Project(
                id = "proj-1",
                title = "Hospital General – Fase 2",
                description = "Ampliación de instalaciones y sistemas de oxígeno. Presupuesto cliente: \$240,000.",
                consultantUid = currentConsultantUid,
                providerUid = provUid,
                providerName = "Constructora Pérez S.A.",
                supervisorUid = "sup-uid",
                progress = 0.75f,
                status = "En Progreso"
            ),
            Project(
                id = "proj-2",
                title = "Residencial Las Lomas",
                description = "Estructura principal y cimentación. Presupuesto cliente: \$500,000.",
                consultantUid = "cons-2",
                providerUid = "prov-2",
                providerName = "Materiales del Norte",
                supervisorUid = "sup-2",
                progress = 0.60f,
                status = "En Progreso"
            ),
            Project(
                id = "proj-3",
                title = "Torre Corporativa Alfa",
                description = "Diseño, supervisión y suministro de materiales. Presupuesto cliente: \$420,000.",
                consultantUid = "cons-3",
                providerUid = "prov-2",
                providerName = "Materiales del Norte",
                supervisorUid = "sup-uid",
                progress = 0.35f,
                status = "En Progreso",
                hasDelays = true,
                delayReason = "Retraso en permisos municipales."
            ),
            Project(
                id = "proj-4",
                title = "Parque Industrial Oriente",
                description = "Red eléctrica e iluminación industrial. Presupuesto cliente: \$280,000.",
                consultantUid = "cons-2",
                providerUid = "prov-3",
                providerName = "Electro Servicios MX",
                supervisorUid = "sup-3",
                progress = 0.45f,
                status = "En Progreso"
            ),
            Project(
                id = "proj-5",
                title = "Clínica Privada Norte",
                description = "Sistema hidráulico y plomería completa. Presupuesto cliente: \$180,000.",
                consultantUid = "cons-4",
                providerUid = "prov-4",
                providerName = "Hidráulica Integral",
                supervisorUid = "sup-2",
                progress = 0.20f,
                status = "En Progreso"
            ),
            // ── PENDIENTE ──────────────────────────────────────────────
            Project(
                id = "proj-6",
                title = "Estadio Municipal Reforma",
                description = "Obra civil integral. Pendiente de asignación de proveedor.",
                consultantUid = null,
                providerUid = null,
                providerName = "",
                supervisorUid = "sup-uid",
                progress = 0f,
                status = "Pendiente"
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
