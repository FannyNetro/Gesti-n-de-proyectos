package com.vgtech.mobile.ui.screens.supervisor

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vgtech.mobile.data.local.InternalDb
import com.vgtech.mobile.data.model.*
import com.vgtech.mobile.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// ═════════════════════════════════════════════════════════════════
//  SupervisorDashboardScreen — HU1 through HU8
// ═════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorDashboardScreen(
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val projects by InternalDb.projects.collectAsState()
    val employees by InternalDb.employees.collectAsState()
    val progressReports by InternalDb.projectProgressReports.collectAsState()
    val invitations by InternalDb.invitations.collectAsState()
    val quotations by InternalDb.quotations.collectAsState()
    val evaluations by InternalDb.evaluations.collectAsState()

    val consultants = employees.filter { it.puesto.lowercase() == "consultor" && it.activo }
    val providers = employees.filter { it.puesto.lowercase() == "proveedor" && it.activo }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "VG Tech",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = SurfaceWhite
                        )
                        Text(
                            "Panel de Supervisor",
                            style = MaterialTheme.typography.labelSmall,
                            color = SurfaceWhite.copy(alpha = 0.6f)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Navy
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = SurfaceWhite.copy(alpha = 0.7f)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceWhite,
                tonalElevation = 8.dp
            ) {
                val tabs = listOf(
                    Triple("Inicio", Icons.Default.Dashboard, 0),
                    Triple("Proyectos", Icons.Default.Folder, 1),
                    Triple("Avances", Icons.Default.Timeline, 2),
                    Triple("Evaluar", Icons.Default.Star, 3),
                    Triple("Reportes", Icons.Default.Assessment, 4)
                )
                tabs.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(22.dp)) },
                        label = { Text(label, fontSize = 10.sp, maxLines = 1) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Teal,
                            selectedTextColor = Teal,
                            indicatorColor = Teal.copy(alpha = 0.12f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            when (selectedTab) {
                0 -> DashboardTab(projects, consultants, providers)
                1 -> ProjectsTab(projects, consultants, providers, invitations, quotations)
                2 -> ProgressTab(projects, progressReports)
                3 -> EvaluationsTab(projects, consultants, providers, evaluations)
                4 -> ReportsTab(projects, consultants, providers, progressReports, evaluations)
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════
//  TAB 0 — Dashboard  (HU1 + HU3)
// ═════════════════════════════════════════════════════════════════

@Composable
private fun DashboardTab(
    projects: List<Project>,
    consultants: List<Employee>,
    providers: List<Employee>
) {
    val activeProjects = projects.filter { it.status == "En Progreso" }
    val completedProjects = projects.filter { it.status == "Finalizado" }
    val pendingProjects = projects.filter { it.status == "Pendiente" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                "Bienvenido, Supervisor",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Navy
            )
            Text(
                "Resumen de proyectos y equipo",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(36.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Mustard)
            )
        }

        // KPI Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashCard(Modifier.weight(1f), Icons.Default.Engineering, "Activos", "${activeProjects.size}", Teal)
                DashCard(Modifier.weight(1f), Icons.Default.Groups, "Personal", "${consultants.size + providers.size}", Mustard)
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashCard(Modifier.weight(1f), Icons.Default.CheckCircle, "Completados", "${completedProjects.size}", SuccessGreen)
                DashCard(Modifier.weight(1f), Icons.Default.Warning, "Pendientes", "${pendingProjects.size}", WarningAmber)
            }
        }

        // HU1 — Consultores y sus proyectos asignados
        item {
            SectionHeader("Consultores y Proyectos Asignados")
        }
        items(consultants) { consultant ->
            val assignedProjects = projects.filter { it.consultantUid == consultant.uid }
            ConsultantWorkloadCard(consultant, assignedProjects)
        }

        // HU3 — Carga de trabajo
        item {
            SectionHeader("Carga de Trabajo por Consultor")
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    consultants.forEach { c ->
                        val count = projects.count { it.consultantUid == c.uid && it.status != "Finalizado" }
                        val maxProjects = 5
                        val fraction = (count.toFloat() / maxProjects).coerceIn(0f, 1f)
                        val barColor = when {
                            count >= 4 -> ErrorRed
                            count >= 3 -> WarningAmber
                            else -> SuccessGreen
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                c.nombreCompleto,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Navy,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "$count proyectos",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        LinearProgressIndicator(
                            progress = { fraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = barColor,
                            trackColor = SurfaceGray
                        )
                        if (count >= 4) {
                            Text(
                                "⚠️ Sobrecarga de tareas",
                                style = MaterialTheme.typography.labelSmall,
                                color = ErrorRed,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════
//  TAB 1 — Proyectos  (HU2 + HU7 + HU8)
// ═════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectsTab(
    projects: List<Project>,
    consultants: List<Employee>,
    providers: List<Employee>,
    invitations: List<ProviderInvitation>,
    quotations: List<Quotation>
) {
    var expandedProjectId by remember { mutableStateOf<String?>(null) }
    var showAssignDialog by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var snackMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackMessage = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionHeader("Gestión de Proyectos")
                Text(
                    "Asigna, reasigna y envía invitaciones",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            items(projects) { project ->
                val isExpanded = expandedProjectId == project.id
                val consultant = consultants.find { it.uid == project.consultantUid }
                val provider = providers.find { it.uid == project.providerUid }
                val projectQuotations = quotations.filter { it.projectId == project.id }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .clickable { expandedProjectId = if (isExpanded) null else project.id },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Title row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusDot(project.status)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    project.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy
                                )
                                Text(
                                    project.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(
                                if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                        // Progress bar
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { project.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = when (project.status) {
                                "Finalizado" -> SuccessGreen
                                "En Progreso" -> Teal
                                else -> WarningAmber
                            },
                            trackColor = SurfaceGray
                        )
                        Text(
                            "${(project.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = BorderColor)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Assigned consultant
                            InfoRow("Consultor:", consultant?.nombreCompleto ?: "Sin asignar")
                            InfoRow("Proveedor:", provider?.nombreCompleto ?: project.providerName.ifBlank { "Sin asignar" })

                            // Quotations for this project
                            if (projectQuotations.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Cotizaciones:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Navy)
                                projectQuotations.forEach { q ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(TealLight)
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(q.providerName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Navy)
                                            Text("$${String.format("%,.0f", q.amount)} · ${q.estimatedDays} días", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                        }
                                        if (!q.sentToClient) {
                                            TextButton(
                                                onClick = {
                                                    InternalDb.markQuotationSentToClient(q.id)
                                                    snackMessage = "Cotización enviada al cliente"
                                                }
                                            ) {
                                                Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Enviar", fontSize = 11.sp)
                                            }
                                        } else {
                                            Text("✓ Enviada", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // HU2 / HU8 — Assign / Reassign
                                OutlinedButton(
                                    onClick = {
                                        selectedProject = project
                                        showAssignDialog = true
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Teal)
                                ) {
                                    Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (project.consultantUid != null) "Reasignar" else "Asignar", fontSize = 11.sp)
                                }
                                // HU7 — Invite provider
                                OutlinedButton(
                                    onClick = {
                                        selectedProject = project
                                        showInviteDialog = true
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Mustard)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Invitar Prov.", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )
    }

    // ── Assign / Reassign Dialog (HU2 + HU8) ────────────────────
    if (showAssignDialog && selectedProject != null) {
        AssignReassignDialog(
            project = selectedProject!!,
            consultants = consultants,
            providers = providers,
            onDismiss = { showAssignDialog = false },
            onAssign = { projId, consultantUid, providerUid, providerName ->
                if (consultantUid != null) InternalDb.assignConsultantToProject(projId, consultantUid)
                if (providerUid != null) InternalDb.assignProviderToProject(projId, providerUid, providerName)
                snackMessage = "Asignación actualizada"
                showAssignDialog = false
            }
        )
    }

    // ── Invite Provider Dialog (HU7) ─────────────────────────────
    if (showInviteDialog && selectedProject != null) {
        InviteProviderDialog(
            project = selectedProject!!,
            providers = providers,
            onDismiss = { showInviteDialog = false },
            onInvite = { projId, projTitle, provUid, provName, msg ->
                InternalDb.addInvitation(
                    ProviderInvitation(
                        projectId = projId,
                        projectTitle = projTitle,
                        providerUid = provUid,
                        providerName = provName,
                        supervisorUid = "sup-uid",
                        message = msg,
                        status = "Enviada"
                    )
                )
                snackMessage = "Invitación enviada a $provName"
                showInviteDialog = false
            }
        )
    }
}

// ═════════════════════════════════════════════════════════════════
//  TAB 2 — Avances  (HU4)
// ═════════════════════════════════════════════════════════════════

@Composable
private fun ProgressTab(
    projects: List<Project>,
    progressReports: List<ProjectProgress>
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy  HH:mm", Locale("es", "MX")) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader("Historial de Avances y Retrasos")
            Text(
                "Monitorea el progreso de cada proyecto",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Show projects with their progress reports
        items(projects.filter { it.status != "Pendiente" }) { project ->
            val reports = progressReports
                .filter { it.projectId == project.id }
                .sortedByDescending { it.date }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusDot(project.status)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            project.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Navy,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "${(project.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Teal
                        )
                    }

                    // Delay warning
                    if (project.hasDelays) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ErrorRed.copy(alpha = 0.08f))
                                .padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.Warning, null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Retraso detectado", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = ErrorRed)
                                if (project.delayReason.isNotBlank()) {
                                    Text(project.delayReason, style = MaterialTheme.typography.bodySmall, color = Navy)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { project.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (project.hasDelays) WarningAmber else Teal,
                        trackColor = SurfaceGray
                    )

                    // Timeline of reports
                    if (reports.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Reportes de avance:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Navy)
                        Spacer(modifier = Modifier.height(8.dp))

                        reports.forEach { report ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                // Timeline dot + line
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(24.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(if (report.evaluated) SuccessGreen else Teal)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(40.dp)
                                            .background(BorderColor)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        dateFormat.format(Date(report.date)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                    Text(
                                        report.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = Navy
                                    )
                                    Row {
                                        Text("${report.progressPercentage}%", style = MaterialTheme.typography.labelSmall, color = Teal, fontWeight = FontWeight.Bold)
                                        if (report.reportType.isNotBlank()) {
                                            Text(" · ${report.reportType}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                        }
                                    }
                                    if (report.issues.isNotBlank()) {
                                        Text("⚠ ${report.issues}", style = MaterialTheme.typography.labelSmall, color = WarningAmber)
                                    }
                                    if (report.delayReason != null) {
                                        Text("🕐 ${report.delayReason}", style = MaterialTheme.typography.labelSmall, color = ErrorRed)
                                    }
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Sin reportes de avance aún.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════
//  TAB 3 — Evaluaciones  (HU5)
// ═════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EvaluationsTab(
    projects: List<Project>,
    consultants: List<Employee>,
    providers: List<Employee>,
    evaluations: List<PerformanceEvaluation>
) {
    var showNewEvalDialog by remember { mutableStateOf(false) }
    val allPersonnel = consultants + providers

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    SectionHeader("Evaluaciones de Desempeño")
                    Text(
                        "Evalúa proveedores y consultores",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                FloatingActionButton(
                    onClick = { showNewEvalDialog = true },
                    containerColor = Teal,
                    contentColor = SurfaceWhite,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(Icons.Default.Add, "Nueva evaluación", modifier = Modifier.size(20.dp))
                }
            }
        }

        if (evaluations.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📋", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Sin evaluaciones registradas", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                    }
                }
            }
        }

        items(evaluations) { eval ->
            EvaluationCard(eval)
        }
    }

    if (showNewEvalDialog) {
        NewEvaluationDialog(
            personnel = allPersonnel,
            projects = projects,
            onDismiss = { showNewEvalDialog = false },
            onSave = { evaluation ->
                InternalDb.addPerformanceEvaluation(evaluation)
                showNewEvalDialog = false
            }
        )
    }
}

// ═════════════════════════════════════════════════════════════════
//  TAB 4 — Reportes  (HU6)
// ═════════════════════════════════════════════════════════════════

@Composable
private fun ReportsTab(
    projects: List<Project>,
    consultants: List<Employee>,
    providers: List<Employee>,
    progressReports: List<ProjectProgress>,
    evaluations: List<PerformanceEvaluation>
) {
    val activeProjects = projects.filter { it.status == "En Progreso" }
    val completedProjects = projects.filter { it.status == "Finalizado" }
    val delayedProjects = projects.filter { it.hasDelays }
    val avgProgress = if (activeProjects.isNotEmpty()) activeProjects.map { it.progress }.average() else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        SectionHeader("Reportes Consolidados")
        Text(
            "Métricas de avance y rendimiento",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Summary KPI cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Navy),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Resumen General", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ReportMetric("Total", "${projects.size}", SurfaceWhite)
                    ReportMetric("Activos", "${activeProjects.size}", Teal)
                    ReportMetric("Completados", "${completedProjects.size}", SuccessGreen)
                    ReportMetric("Retrasos", "${delayedProjects.size}", ErrorRed)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Average progress
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Avance Promedio en Proyectos Activos", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Navy)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${(avgProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Teal
                )
                LinearProgressIndicator(
                    progress = { avgProgress.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Teal,
                    trackColor = SurfaceGray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Per-project breakdown
        Text("Desglose por Proyecto", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Navy)
        Spacer(modifier = Modifier.height(8.dp))

        projects.forEach { project ->
            val consultant = consultants.find { it.uid == project.consultantUid }
            val provider = providers.find { it.uid == project.providerUid }
            val reportsCount = progressReports.count { it.projectId == project.id }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusDot(project.status)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(project.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Navy, modifier = Modifier.weight(1f))
                        Text("${(project.progress * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, color = Teal)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { project.progress },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = when (project.status) { "Finalizado" -> SuccessGreen; "En Progreso" -> Teal; else -> WarningAmber },
                        trackColor = SurfaceGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Consultor: ${consultant?.nombreCompleto ?: "N/A"}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("Reportes: $reportsCount", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    if (project.providerRating > 0) {
                        Row(modifier = Modifier.padding(top = 4.dp)) {
                            Text("Prov: ", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            repeat(5) { i -> Text(if (i < project.providerRating.toInt()) "★" else "☆", color = Mustard, fontSize = 12.sp) }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Cons: ", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            repeat(5) { i -> Text(if (i < project.consultantRating.toInt()) "★" else "☆", color = Mustard, fontSize = 12.sp) }
                        }
                    }
                    if (project.hasDelays) {
                        Text("⚠ Retraso: ${project.delayReason}", style = MaterialTheme.typography.labelSmall, color = ErrorRed, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Evaluation summary
        if (evaluations.isNotEmpty()) {
            Text("Evaluaciones Registradas", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Navy)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    evaluations.forEach { eval ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(eval.evaluatedName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Navy)
                                Text("${eval.evaluatedRole} · ${eval.projectTitle}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                            Row {
                                repeat(5) { i -> Text(if (i < eval.overallRating.toInt()) "★" else "☆", color = Mustard, fontSize = 14.sp) }
                            }
                        }
                        HorizontalDivider(color = BorderColor)
                    }
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════
//  DIALOGS
// ═════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignReassignDialog(
    project: Project,
    consultants: List<Employee>,
    providers: List<Employee>,
    onDismiss: () -> Unit,
    onAssign: (String, String?, String?, String) -> Unit
) {
    var selectedConsultant by remember { mutableStateOf(project.consultantUid ?: "") }
    var selectedProvider by remember { mutableStateOf(project.providerUid ?: "") }
    var expandConsultant by remember { mutableStateOf(false) }
    var expandProvider by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (project.consultantUid != null) "Reasignar Personal" else "Asignar Personal",
                fontWeight = FontWeight.Bold, color = Navy
            )
        },
        text = {
            Column {
                Text("Proyecto: ${project.title}", style = MaterialTheme.typography.bodyMedium, color = Navy)
                Spacer(modifier = Modifier.height(16.dp))

                // Consultant dropdown
                Text("Consultor", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Navy)
                ExposedDropdownMenuBox(expanded = expandConsultant, onExpandedChange = { expandConsultant = it }) {
                    OutlinedTextField(
                        value = consultants.find { it.uid == selectedConsultant }?.nombreCompleto ?: "Seleccionar...",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandConsultant) },
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(expanded = expandConsultant, onDismissRequest = { expandConsultant = false }) {
                        consultants.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c.nombreCompleto) },
                                onClick = {
                                    selectedConsultant = c.uid
                                    expandConsultant = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Provider dropdown
                Text("Proveedor", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Navy)
                ExposedDropdownMenuBox(expanded = expandProvider, onExpandedChange = { expandProvider = it }) {
                    OutlinedTextField(
                        value = providers.find { it.uid == selectedProvider }?.nombreCompleto ?: "Seleccionar...",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandProvider) },
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(expanded = expandProvider, onDismissRequest = { expandProvider = false }) {
                        providers.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.nombreCompleto) },
                                onClick = {
                                    selectedProvider = p.uid
                                    expandProvider = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val provName = providers.find { it.uid == selectedProvider }?.nombreCompleto ?: ""
                    onAssign(
                        project.id,
                        selectedConsultant.ifBlank { null },
                        selectedProvider.ifBlank { null },
                        provName
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Teal),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InviteProviderDialog(
    project: Project,
    providers: List<Employee>,
    onDismiss: () -> Unit,
    onInvite: (String, String, String, String, String) -> Unit
) {
    var selectedProvider by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Invitar Proveedor", fontWeight = FontWeight.Bold, color = Navy) },
        text = {
            Column {
                Text("Proyecto: ${project.title}", style = MaterialTheme.typography.bodyMedium, color = Navy)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Proveedor", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Navy)
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = providers.find { it.uid == selectedProvider }?.nombreCompleto ?: "Seleccionar...",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        providers.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.nombreCompleto) },
                                onClick = {
                                    selectedProvider = p.uid
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Mensaje / Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedProvider.isNotBlank()) {
                        val provName = providers.find { it.uid == selectedProvider }?.nombreCompleto ?: ""
                        onInvite(project.id, project.title, selectedProvider, provName, message)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Mustard),
                shape = RoundedCornerShape(10.dp),
                enabled = selectedProvider.isNotBlank()
            ) {
                Text("Enviar Invitación")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewEvaluationDialog(
    personnel: List<Employee>,
    projects: List<Project>,
    onDismiss: () -> Unit,
    onSave: (PerformanceEvaluation) -> Unit
) {
    var selectedPerson by remember { mutableStateOf("") }
    var selectedProject by remember { mutableStateOf("") }
    var quality by remember { mutableFloatStateOf(3f) }
    var timeliness by remember { mutableFloatStateOf(3f) }
    var communication by remember { mutableFloatStateOf(3f) }
    var comments by remember { mutableStateOf("") }
    var expandPerson by remember { mutableStateOf(false) }
    var expandProject by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Evaluación", fontWeight = FontWeight.Bold, color = Navy) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Person dropdown
                Text("Persona a evaluar", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Navy)
                ExposedDropdownMenuBox(expanded = expandPerson, onExpandedChange = { expandPerson = it }) {
                    OutlinedTextField(
                        value = personnel.find { it.uid == selectedPerson }?.let { "${it.nombreCompleto} (${it.puesto})" } ?: "Seleccionar...",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandPerson) },
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(expanded = expandPerson, onDismissRequest = { expandPerson = false }) {
                        personnel.forEach { p ->
                            DropdownMenuItem(
                                text = { Text("${p.nombreCompleto} (${p.puesto})") },
                                onClick = {
                                    selectedPerson = p.uid
                                    expandPerson = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Project dropdown
                Text("Proyecto", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Navy)
                ExposedDropdownMenuBox(expanded = expandProject, onExpandedChange = { expandProject = it }) {
                    OutlinedTextField(
                        value = projects.find { it.id == selectedProject }?.title ?: "Seleccionar...",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandProject) },
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(expanded = expandProject, onDismissRequest = { expandProject = false }) {
                        projects.forEach { proj ->
                            DropdownMenuItem(
                                text = { Text(proj.title) },
                                onClick = {
                                    selectedProject = proj.id
                                    expandProject = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ratings
                RatingSlider("Calidad", quality) { quality = it }
                RatingSlider("Puntualidad", timeliness) { timeliness = it }
                RatingSlider("Comunicación", communication) { communication = it }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = comments,
                    onValueChange = { comments = it },
                    label = { Text("Comentarios") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            val person = personnel.find { it.uid == selectedPerson }
            val proj = projects.find { it.id == selectedProject }
            Button(
                onClick = {
                    if (person != null && proj != null) {
                        val overall = (quality + timeliness + communication) / 3f
                        onSave(
                            PerformanceEvaluation(
                                evaluatedUid = person.uid,
                                evaluatedName = person.nombreCompleto,
                                evaluatedRole = person.puesto,
                                projectId = proj.id,
                                projectTitle = proj.title,
                                qualityRating = quality,
                                timelinessRating = timeliness,
                                communicationRating = communication,
                                overallRating = overall,
                                comments = comments
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Teal),
                shape = RoundedCornerShape(10.dp),
                enabled = selectedPerson.isNotBlank() && selectedProject.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// ═════════════════════════════════════════════════════════════════
//  SHARED COMPOSABLE HELPERS
// ═════════════════════════════════════════════════════════════════

@Composable
private fun DashCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    accent: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accent, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Navy)
            Text(title, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        color = Navy
    )
}

@Composable
private fun StatusDot(status: String) {
    val color = when (status) {
        "Finalizado" -> SuccessGreen
        "En Progreso" -> Teal
        "Pendiente" -> WarningAmber
        else -> TextMuted
    }
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Navy)
        Spacer(modifier = Modifier.width(6.dp))
        Text(value, style = MaterialTheme.typography.bodySmall, color = TextMuted)
    }
}

@Composable
private fun ConsultantWorkloadCard(consultant: Employee, assignedProjects: List<Project>) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Teal.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        consultant.nombreCompleto.take(1).uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Teal
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(consultant.nombreCompleto, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Navy)
                    Text("${assignedProjects.size} proyecto(s) asignado(s)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null, tint = TextMuted
                )
            }

            if (expanded && assignedProjects.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = BorderColor)
                assignedProjects.forEach { p ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusDot(p.status)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(p.title, style = MaterialTheme.typography.bodySmall, color = Navy, modifier = Modifier.weight(1f))
                        Text("${(p.progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Teal)
                    }
                }
            }
            if (expanded && assignedProjects.isEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Sin proyectos asignados", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }
    }
}

@Composable
private fun EvaluationCard(eval: PerformanceEvaluation) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("es", "MX")) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Mustard.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Star, null, tint = Mustard, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(eval.evaluatedName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Navy)
                    Text("${eval.evaluatedRole} · ${eval.projectTitle}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                Text(dateFormat.format(Date(eval.date)), style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Star ratings row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MiniRating("Calidad", eval.qualityRating)
                MiniRating("Puntualidad", eval.timelinessRating)
                MiniRating("Comunicación", eval.communicationRating)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Promedio: ", style = MaterialTheme.typography.labelMedium, color = Navy)
                repeat(5) { i ->
                    Text(
                        if (i < eval.overallRating.toInt()) "★" else "☆",
                        color = Mustard,
                        fontSize = 16.sp
                    )
                }
                Text(
                    "  ${String.format("%.1f", eval.overallRating)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
            }

            if (eval.comments.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("\"${eval.comments}\"", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }
    }
}

@Composable
private fun MiniRating(label: String, rating: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 10.sp)
        Row {
            repeat(5) { i ->
                Text(if (i < rating.toInt()) "★" else "☆", color = Mustard, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun RatingSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Navy)
            Row {
                repeat(5) { i -> Text(if (i < value.toInt()) "★" else "☆", color = Mustard, fontSize = 14.sp) }
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..5f,
            steps = 3,
            colors = SliderDefaults.colors(
                thumbColor = Mustard,
                activeTrackColor = Mustard,
                inactiveTrackColor = SurfaceGray
            )
        )
    }
}

@Composable
private fun ReportMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = SurfaceWhite.copy(alpha = 0.7f))
    }
}
