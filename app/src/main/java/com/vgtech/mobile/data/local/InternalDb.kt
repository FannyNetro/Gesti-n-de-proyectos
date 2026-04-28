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

    // ── Provider Payment stores ───────────────────────────────────
    private val _providerTransactions = MutableStateFlow<List<ProviderTransaction>>(emptyList())
    val providerTransactions: StateFlow<List<ProviderTransaction>> = _providerTransactions.asStateFlow()

    private val _paymentPhases = MutableStateFlow<List<PaymentPhase>>(emptyList())
    val paymentPhases: StateFlow<List<PaymentPhase>> = _paymentPhases.asStateFlow()

    init {
        val currentConsultantUid = "consultor-uid"
        val provUid = "prov-uid"

        // ── Empleados RH / Administrativos ───────────────────────────────
        val generatedUsers = mutableListOf<Employee>()
        generatedUsers.add(Employee(uid = "admin-uid",   nombreCompleto = "Laura Ramírez Torres",   email = "admin@vgtech.com",       puesto = "RH",             password = "admin",  activo = true, sueldo = 25000.0, pagoPorHora = 156.25, diasVacaciones = 12.0))
        generatedUsers.add(Employee(uid = "adm-2",       nombreCompleto = "Jorge Herrera Salinas",  email = "jorge.h@vgtech.com",     puesto = "Administrativo", password = "adm2",   activo = true, sueldo = 20000.0, pagoPorHora = 125.00, diasVacaciones = 10.0))
        generatedUsers.add(Employee(uid = "adm-3",       nombreCompleto = "Patricia Vela Muñoz",    email = "patricia.v@vgtech.com",  puesto = "Administrativo", password = "adm3",   activo = true, sueldo = 18500.0, pagoPorHora = 115.62, diasVacaciones = 10.0))
        generatedUsers.add(Employee(uid = "adm-4",       nombreCompleto = "Miguel Ángel Cruz",      email = "miguel.c@vgtech.com",    puesto = "Empleado",       password = "adm4",   activo = true, sueldo = 16000.0, pagoPorHora = 100.00, diasVacaciones = 8.0))
        generatedUsers.add(Employee(uid = "adm-5",       nombreCompleto = "Sofía Mendez Arriaga",   email = "sofia.m@vgtech.com",     puesto = "Empleado",       password = "adm5",   activo = true, sueldo = 16500.0, pagoPorHora = 103.12, diasVacaciones = 8.0))

        generatedUsers.add(Employee(uid = "sup-uid",     nombreCompleto = "Carlos Mendoza Ríos",    email = "supervisor@vgtech.com",  puesto = "Supervisor",     password = "super",  activo = true, sueldo = 38000.0, pagoPorHora = 237.50, diasVacaciones = 15.0))
        generatedUsers.add(Employee(uid = "sup-2",       nombreCompleto = "Alejandro Fuentes",      email = "alejandro.f@vgtech.com", puesto = "Supervisor",     password = "sup2",   activo = true, sueldo = 35000.0, pagoPorHora = 218.75, diasVacaciones = 14.0))
        generatedUsers.add(Employee(uid = "sup-3",       nombreCompleto = "Daniela Ortiz Peña",     email = "daniela.o@vgtech.com",   puesto = "Supervisor",     password = "sup3",   activo = true, sueldo = 33000.0, pagoPorHora = 206.25, diasVacaciones = 12.0))

        generatedUsers.add(Employee(uid = currentConsultantUid, nombreCompleto = "Fernando Castillo",  email = "consultor@vgtech.com",   puesto = "Consultor",  password = "cons",   activo = true, sueldo = 32000.0, pagoPorHora = 200.00, diasVacaciones = 12.0))
        generatedUsers.add(Employee(uid = "cons-2",      nombreCompleto = "Ana García López",        email = "ana@vgtech.com",          puesto = "Consultor",  password = "cons2",  activo = true, sueldo = 30000.0, pagoPorHora = 187.50, diasVacaciones = 12.0))
        generatedUsers.add(Employee(uid = "cons-3",      nombreCompleto = "Roberto Díaz Martín",    email = "roberto@vgtech.com",      puesto = "Consultor",  password = "cons3",  activo = true, sueldo = 28000.0, pagoPorHora = 175.00, diasVacaciones = 10.0))
        generatedUsers.add(Employee(uid = "cons-4",      nombreCompleto = "Isabel Navarro Vega",    email = "isabel.n@vgtech.com",     puesto = "Consultor",  password = "cons4",  activo = true, sueldo = 26500.0, pagoPorHora = 165.62, diasVacaciones = 10.0))

        generatedUsers.add(Employee(uid = provUid,       nombreCompleto = "Constructora Pérez S.A.", email = "proveedor@vgtech.com",   puesto = "Proveedor",  password = "prov",   activo = true, tipoTrabajo = listOf("Obra Civil", "Cimentación"), sueldo = 0.0, pagoPorHora = 0.0, diasVacaciones = 0.0))
        generatedUsers.add(Employee(uid = "prov-2",      nombreCompleto = "Materiales del Norte",    email = "norte@vgtech.com",        puesto = "Proveedor",  password = "prov2",  activo = true, tipoTrabajo = listOf("Materiales", "Acabados"),    sueldo = 0.0, pagoPorHora = 0.0, diasVacaciones = 0.0))
        generatedUsers.add(Employee(uid = "prov-3",      nombreCompleto = "Electro Servicios MX",    email = "electro@vgtech.com",      puesto = "Proveedor",  password = "prov3",  activo = true, tipoTrabajo = listOf("Eléctrico", "Iluminación"),  sueldo = 0.0, pagoPorHora = 0.0, diasVacaciones = 0.0))
        generatedUsers.add(Employee(uid = "prov-4",      nombreCompleto = "Hidráulica Integral",     email = "hidro@vgtech.com",        puesto = "Proveedor",  password = "prov4",  activo = true, tipoTrabajo = listOf("Plomería", "Hidráulica"),    sueldo = 0.0, pagoPorHora = 0.0, diasVacaciones = 0.0))
        generatedUsers.add(Employee(uid = "prov-5",      nombreCompleto = "Arquitectura & Diseño MX",email = "arqdmx@vgtech.com",       puesto = "Proveedor",  password = "prov5",  activo = true, tipoTrabajo = listOf("Diseño", "Acabados"),        sueldo = 0.0, pagoPorHora = 0.0, diasVacaciones = 0.0))

        generatedUsers.add(Employee(uid = "cliente-uid", nombreCompleto = "Grupo Empresarial León",  email = "cliente@vgtech.com",      puesto = "Cliente",    password = "cli",    activo = true))

        _employees.value = generatedUsers

        // ── Proyectos coherentes con proveedores y transacciones ─────────
        _projects.value = listOf(
            Project(id = "proj-101", title = "Hospital General – Fase 1", description = "Obra civil e instalaciones iniciales. Presupuesto cliente: \$360,000.", consultantUid = currentConsultantUid, providerUid = provUid, providerName = "Constructora Pérez S.A.", supervisorUid = "sup-uid", progress = 1.0f, status = "Finalizado", providerRating = 4.8f, evaluationResult = "Aprobado con Distinción"),
            Project(id = "proj-102", title = "Plaza Comercial Sur", description = "Instalaciones eléctricas completas. Presupuesto cliente: \$300,000.", consultantUid = "cons-2", providerUid = "prov-3", providerName = "Electro Servicios MX", supervisorUid = "sup-2", progress = 1.0f, status = "Finalizado"),
            Project(id = "proj-103", title = "Centro Educativo Federal", description = "Diseño arquitectónico y acabados. Presupuesto cliente: \$220,000.", consultantUid = "cons-4", providerUid = "prov-5", providerName = "Arquitectura & Diseño MX", supervisorUid = "sup-3", progress = 1.0f, status = "Finalizado"),
            Project(id = "proj-1",   title = "Hospital General – Fase 2", description = "Ampliación de instalaciones y sistemas de oxígeno. Presupuesto cliente: \$240,000.", consultantUid = currentConsultantUid, providerUid = provUid, providerName = "Constructora Pérez S.A.", supervisorUid = "sup-uid", progress = 0.75f, status = "En Progreso"),
            Project(id = "proj-2",   title = "Residencial Las Lomas", description = "Estructura principal y cimentación. Presupuesto cliente: \$500,000.", consultantUid = "cons-2", providerUid = "prov-2", providerName = "Materiales del Norte", supervisorUid = "sup-2", progress = 0.60f, status = "En Progreso"),
            Project(id = "proj-3",   title = "Torre Corporativa Alfa", description = "Diseño, supervisión y suministro de materiales. Presupuesto cliente: \$420,000.", consultantUid = "cons-3", providerUid = "prov-2", providerName = "Materiales del Norte", supervisorUid = "sup-uid", progress = 0.35f, status = "En Progreso", hasDelays = true, delayReason = "Retraso en permisos municipales."),
            Project(id = "proj-4",   title = "Parque Industrial Oriente", description = "Red eléctrica e iluminación industrial. Presupuesto cliente: \$280,000.", consultantUid = "cons-2", providerUid = "prov-3", providerName = "Electro Servicios MX", supervisorUid = "sup-3", progress = 0.45f, status = "En Progreso"),
            Project(id = "proj-5",   title = "Clínica Privada Norte", description = "Sistema hidráulico y plomería completa. Presupuesto cliente: \$180,000.", consultantUid = "cons-4", providerUid = "prov-4", providerName = "Hidráulica Integral", supervisorUid = "sup-2", progress = 0.20f, status = "En Progreso"),
            Project(id = "proj-6",   title = "Estadio Municipal Reforma", description = "Obra civil integral. Pendiente de asignación de proveedor.", supervisorUid = "sup-uid", progress = 0f, status = "Pendiente")
        )
        
        // ── Seed Transactions & Phases (Migrated from ProviderTxDb) ───────
        val oneDay = 86_400_000L
        val now = System.currentTimeMillis()

        _paymentPhases.value = listOf(
            PaymentPhase(providerId = "prov-uid", projectId = "proj-1", phaseNumber = 1, totalPhases = 2, amountToPay = 60000.0, scheduledDate = now - 15 * oneDay, status = PaymentPhaseStatus.PAGADO, paidDate = now - 15 * oneDay),
            PaymentPhase(providerId = "prov-uid", projectId = "proj-1", phaseNumber = 2, totalPhases = 2, amountToPay = 60000.0, scheduledDate = now + 15 * oneDay, status = PaymentPhaseStatus.PENDIENTE),
            PaymentPhase(providerId = "prov-2",   projectId = "proj-2", phaseNumber = 1, totalPhases = 3, amountToPay = 100000.0, scheduledDate = now - 45 * oneDay, status = PaymentPhaseStatus.PAGADO, paidDate = now - 45 * oneDay),
            PaymentPhase(providerId = "prov-2",   projectId = "proj-2", phaseNumber = 2, totalPhases = 3, amountToPay = 75000.0, scheduledDate = now + 10 * oneDay, status = PaymentPhaseStatus.PENDIENTE),
            PaymentPhase(providerId = "prov-2",   projectId = "proj-2", phaseNumber = 3, totalPhases = 3, amountToPay = 75000.0, scheduledDate = now + 30 * oneDay, status = PaymentPhaseStatus.PENDIENTE)
        )

        _providerTransactions.value = listOf(
            ProviderTransaction(id = "tx-01", providerId = "prov-uid", projectId = "proj-101", type = TransactionType.SERVICE, rawAmount = 360000.0, companyCut = 180000.0, providerCut = 180000.0, description = "Hospital General – Fase 1: Obra civil", timestamp = now - 90 * oneDay),
            ProviderTransaction(id = "tx-02", providerId = "prov-uid", projectId = "proj-101", type = TransactionType.PAYMENT, rawAmount = 180000.0, description = "Liquidación total – Hospital Fase 1", timestamp = now - 75 * oneDay),
            ProviderTransaction(id = "tx-03", providerId = "prov-uid", projectId = "proj-1",   type = TransactionType.SERVICE, rawAmount = 240000.0, companyCut = 120000.0, providerCut = 120000.0, description = "Hospital General – Fase 2: Ampliación", timestamp = now - 30 * oneDay),
            ProviderTransaction(id = "tx-04", providerId = "prov-uid", projectId = "proj-1",   type = TransactionType.PAYMENT, rawAmount = 60000.0,  description = "Anticipo 50% – Hospital Fase 2", timestamp = now - 15 * oneDay),
            ProviderTransaction(id = "tx-05", providerId = "prov-2",   projectId = "proj-2",   type = TransactionType.SERVICE, rawAmount = 500000.0, companyCut = 250000.0, providerCut = 250000.0, description = "Residencial Las Lomas – Estructura", timestamp = now - 60 * oneDay),
            ProviderTransaction(id = "tx-06", providerId = "prov-2",   projectId = "proj-2",   type = TransactionType.PAYMENT, rawAmount = 100000.0, description = "Abono Fase 1/3 – Las Lomas", timestamp = now - 45 * oneDay),
            ProviderTransaction(id = "tx-07", providerId = "prov-2",   projectId = "proj-3",   type = TransactionType.SERVICE, rawAmount = 420000.0, companyCut = 210000.0, providerCut = 210000.0, description = "Torre Corporativa Alfa – Materiales", timestamp = now - 40 * oneDay),
            ProviderTransaction(id = "tx-08", providerId = "prov-2",   projectId = "proj-3",   type = TransactionType.PAYMENT, rawAmount = 100000.0, description = "Anticipo inicial – Torre Alfa", timestamp = now - 20 * oneDay),
            ProviderTransaction(id = "tx-09", providerId = "prov-3",   projectId = "proj-102", type = TransactionType.SERVICE, rawAmount = 300000.0, companyCut = 150000.0, providerCut = 150000.0, description = "Plaza Comercial Sur – Eléctrico", timestamp = now - 80 * oneDay),
            ProviderTransaction(id = "tx-10", providerId = "prov-3",   projectId = "proj-102", type = TransactionType.PAYMENT, rawAmount = 150000.0, description = "Liquidación total – Plaza Comercial", timestamp = now - 60 * oneDay),
            ProviderTransaction(id = "tx-11", providerId = "prov-3",   projectId = "proj-4",   type = TransactionType.SERVICE, rawAmount = 280000.0, companyCut = 140000.0, providerCut = 140000.0, description = "Parque Industrial Oriente – Iluminación", timestamp = now - 35 * oneDay),
            ProviderTransaction(id = "tx-12", providerId = "prov-3",   projectId = "proj-4",   type = TransactionType.PAYMENT, rawAmount = 60000.0,  description = "Anticipo – Parque Industrial", timestamp = now - 10 * oneDay),
            ProviderTransaction(id = "tx-13", providerId = "prov-4",   projectId = "proj-5",   type = TransactionType.SERVICE, rawAmount = 180000.0, companyCut = 90000.0,  providerCut = 90000.0,  description = "Clínica Privada Norte – Hidráulico", timestamp = now - 20 * oneDay),
            ProviderTransaction(id = "tx-14", providerId = "prov-5",   projectId = "proj-103", type = TransactionType.SERVICE, rawAmount = 220000.0, companyCut = 110000.0, providerCut = 110000.0, description = "Centro Educativo Federal – Diseño", timestamp = now - 70 * oneDay),
            ProviderTransaction(id = "tx-15", providerId = "prov-5",   projectId = "proj-103", type = TransactionType.PAYMENT, rawAmount = 110000.0, description = "Liquidación total – Centro Educativo", timestamp = now - 50 * oneDay)
        )

        // Mock some progress reports
        _projectProgressReports.value = listOf(
            ProjectProgress(id = "report-1", projectId = "proj-1", projectTitle = "Hospital General - Fase 3", providerUid = provUid, providerName = "Proveedor General", progressPercentage = 85, description = "Instalación de tuberías de cobre completada.", reportType = "Diario", date = now - 86_400_000),
            ProjectProgress(id = "report-2", projectId = "proj-2", projectTitle = "Residencial Las Lomas",     providerUid = provUid, providerName = "Proveedor General", progressPercentage = 50, description = "Colado de losa de entrepiso.", reportType = "Semanal", date = now - 172_800_000),
            ProjectProgress(id = "report-3", projectId = "proj-3", projectTitle = "Torre Corporativa Alfa",    providerUid = "prov-2", providerName = "Materiales del Norte", progressPercentage = 30, description = "Excavación y preparación de terreno.", reportType = "Semanal", date = now - 259_200_000)
        )

        _invitations.value = listOf(ProviderInvitation(id = "inv-1", projectId = "proj-4", projectTitle = "Plaza Comercial Sur", providerUid = "prov-3", providerName = "Electro Servicios MX", supervisorUid = "sup-uid", message = "Invitación para cotizar instalaciones eléctricas.", status = "Cotizada"))
        _quotations.value = listOf(Quotation(id = "quot-1", invitationId = "inv-1", projectId = "proj-4", projectTitle = "Plaza Comercial Sur", providerUid = "prov-3", providerName = "Electro Servicios MX", amount = 450000.0, estimatedDays = 45, description = "Incluye cableado y tableros.", sentToClient = true))
    }

    // ── Employee Methods ──────────────────────────
    fun getEmployeeById(uid: String): Employee? = _employees.value.find { it.uid == uid }
    fun getEmployeeByEmail(email: String): Employee? = _employees.value.find { it.email == email }
    fun addEmployee(employee: Employee) { _employees.value += employee }
    fun updateEmployee(updated: Employee) { _employees.value = _employees.value.map { if (it.uid == updated.uid) updated else it } }
    fun deactivateEmployee(uid: String, motivo: String = "") { getEmployeeById(uid)?.let { updateEmployee(it.copy(activo = false, motivoInactivo = motivo)) } }
    fun updateEmployeePassword(uid: String, newPass: String) { getEmployeeById(uid)?.let { updateEmployee(it.copy(password = newPass)) } }
    fun updateEmployeeRates(uid: String, sueldo: Double, hourly: Double) { getEmployeeById(uid)?.let { updateEmployee(it.copy(sueldo = sueldo, pagoPorHora = hourly)) } }

    // ── Vacation Methods ──────────────────────────
    fun addVacationRequest(req: VacationRequest) { _vacationRequests.value += req }
    fun updateVacationRequest(updated: VacationRequest) { _vacationRequests.value = _vacationRequests.value.map { if (it.id == updated.id) updated else it } }
    fun updateVacationStatus(requestId: String, status: VacationStatus) {
        val request = _vacationRequests.value.find { it.id == requestId } ?: return
        updateVacationRequest(request.copy(status = status))
        if (status == VacationStatus.APPROVED) {
            getEmployeeById(request.employeeUid)?.let { updateEmployee(it.copy(diasVacaciones = it.diasVacaciones - request.effectiveDays)) }
        }
    }

    // ── Work Log Methods ──────────────────────────
    fun addWorkLog(log: WorkLog) { _workLogs.value += log }

    // ── Project Methods ──────────────────────────
    fun updateProject(updated: Project) { _projects.value = _projects.value.map { if (it.id == updated.id) updated else it } }
    fun assignConsultantToProject(projectId: String, consultantUid: String) { _projects.value.find { it.id == projectId }?.let { updateProject(it.copy(consultantUid = consultantUid)) } }
    fun assignProviderToProject(projectId: String, providerUid: String, providerName: String) { _projects.value.find { it.id == projectId }?.let { updateProject(it.copy(providerUid = providerUid, providerName = providerName, status = "En Progreso")) } }
    fun toggleProjectMark(projectId: String) { _projects.value.find { it.id == projectId }?.let { updateProject(it.copy(isMarked = !it.isMarked)) } }
    fun updateProjectProgress(projectId: String, progress: Float, status: String = "En Progreso") { _projects.value.find { it.id == projectId }?.let { updateProject(it.copy(progress = progress, status = status)) } }
    fun addProjectProgress(report: ProjectProgress) { _projectProgressReports.value += report }
    fun addProjectCancellationRequest(req: ProjectCancellationRequest) { _cancellationRequests.value += req }

    fun evaluateProgress(
        reportId: String,
        evaluation: String,
        comments: String,
        rating: Float,
        imageUrl: String?,
        modifiedProgress: Int?,
        modReason: String
    ) {
        val report = _projectProgressReports.value.find { it.id == reportId } ?: return
        val isModified = modifiedProgress != null && modifiedProgress != report.progressPercentage
        
        val updatedReport = report.copy(
            evaluated = true,
            consultantEvaluation = evaluation,
            consultantComments = comments,
            evaluationRating = rating,
            evaluationImageUrl = imageUrl,
            wasModified = isModified,
            originalProgress = if (isModified) report.progressPercentage else report.originalProgress,
            progressPercentage = modifiedProgress ?: report.progressPercentage,
            modificationReason = modReason
        )

        _projectProgressReports.value = _projectProgressReports.value.map {
            if (it.id == reportId) updatedReport else it
        }

        if (evaluation != "Rechazado") {
            updateProjectProgress(report.projectId, (modifiedProgress ?: report.progressPercentage) / 100f)
        }
    }

    // ── Provider Payment Methods ──────────────────
    fun addProviderTransaction(tx: ProviderTransaction) { _providerTransactions.value += tx }
    fun addPaymentPhase(phase: PaymentPhase) { _paymentPhases.value += phase }
    fun updatePaymentPhase(updated: PaymentPhase) { _paymentPhases.value = _paymentPhases.value.map { if (it.id == updated.id) updated else it } }
    fun deletePaymentPhase(phaseId: String) { _paymentPhases.value = _paymentPhases.value.filter { it.id != phaseId } }

    // ── Chat, Invitations, etc ────────────────────
    fun addChatMessage(msg: ChatMessage) { _chatMessages.value += msg }
    fun addInvitation(inv: ProviderInvitation) { _invitations.value += inv }
    fun updateInvitation(updated: ProviderInvitation) { _invitations.value = _invitations.value.map { if (it.id == updated.id) updated else it } }
    fun addQuotation(quot: Quotation) { _quotations.value += quot }
    fun markQuotationSentToClient(quotationId: String) { _quotations.value = _quotations.value.map { if (it.id == quotationId) it.copy(sentToClient = true) else it } }
    fun updateQuotationClientStatus(quotationId: String, status: String) { _quotations.value = _quotations.value.map { if (it.id == quotationId) it.copy(clientStatus = status) else it } }
    fun addPerformanceEvaluation(eval: PerformanceEvaluation) { _evaluations.value += eval }

    fun supervisorAcceptProject(quotationId: String, projectId: String, supervisorUid: String, clientUid: String) {
        val quotation = _quotations.value.find { it.id == quotationId } ?: return
        updateQuotationClientStatus(quotationId, "Aceptada")
        assignProviderToProject(projectId, quotation.providerUid, quotation.providerName)
    }
}
