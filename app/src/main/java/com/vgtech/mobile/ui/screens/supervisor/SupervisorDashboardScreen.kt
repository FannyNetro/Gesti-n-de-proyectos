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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.vgtech.mobile.data.local.InternalDb
import com.vgtech.mobile.data.model.*
import com.vgtech.mobile.ui.screens.rh.ProviderPayableScreen
import com.vgtech.mobile.ui.screens.rh.ManagePhasesDialog
import com.vgtech.mobile.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

// ═════════════════════════════════════════════════════════════════
//  SupervisorDashboardScreen — HU1 through HU8
// ═════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorDashboardScreen(
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf("Tablero") }

    val projects by InternalDb.projects.collectAsState()
    val employees by InternalDb.employees.collectAsState()
    val progressReports by InternalDb.projectProgressReports.collectAsState()
    val invitations by InternalDb.invitations.collectAsState()
    val quotations by InternalDb.quotations.collectAsState()
    val evaluations by InternalDb.evaluations.collectAsState()
    val phases by InternalDb.paymentPhases.collectAsState()

    val consultants = employees.filter { it.puesto.lowercase() == "consultor" && it.activo }
    val providers   = employees.filter { it.puesto.lowercase() == "proveedor" && it.activo }
    val clients     = employees.filter { it.puesto.lowercase() == "cliente"   && it.activo }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Navy,
                drawerShape = RoundedCornerShape(0.dp),
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text("VG Tech", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Panel de Supervisor", style = MaterialTheme.typography.labelMedium, color = Teal, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(40.dp))
                DrawerItem(Icons.Default.Dashboard, "Tablero de Control", selectedTab == "Tablero") { selectedTab = "Tablero"; scope.launch { drawerState.close() } }
                DrawerItem(Icons.Default.Folder, "Proyectos Activos", selectedTab == "Proyectos") { selectedTab = "Proyectos"; scope.launch { drawerState.close() } }
                DrawerItem(Icons.Default.RequestQuote, "Enviar Cotizaciones", selectedTab == "Cotizaciones") { selectedTab = "Cotizaciones"; scope.launch { drawerState.close() } }
                DrawerItem(Icons.Default.Payments, "Pagos a Proveedores", selectedTab == "Pagos") { selectedTab = "Pagos"; scope.launch { drawerState.close() } }
                DrawerItem(Icons.Default.Timeline, "Avances Semanales", selectedTab == "Avances") { selectedTab = "Avances"; scope.launch { drawerState.close() } }
                DrawerItem(Icons.Default.Star, "Evaluar y Calificar", selectedTab == "Evaluar") { selectedTab = "Evaluar"; scope.launch { drawerState.close() } }
                DrawerItem(Icons.Default.Assessment, "Reportes", selectedTab == "Reportes") { selectedTab = "Reportes"; scope.launch { drawerState.close() } }
                DrawerItem(Icons.Default.Chat, "Chat", selectedTab == "Chat") { selectedTab = "Chat"; scope.launch { drawerState.close() } }
                Spacer(modifier = Modifier.weight(1f))
                DrawerItem(Icons.AutoMirrored.Filled.Logout, "Cerrar Sesión", false, ErrorRed) { onLogout() }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Panel de Supervisor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SurfaceWhite) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menú", tint = SurfaceWhite) } },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Navy)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(BackgroundLight)
            ) {
                when (selectedTab) {
                    "Tablero" -> DashboardTab(projects, consultants, providers, quotations)
                    "Proyectos" -> ProjectsTab(projects, consultants, providers, invitations, quotations)
                    "Cotizaciones" -> SupervisorQuotationsTab(projects, providers)
                    "Pagos" -> SupervisorProjectPaymentsTab(projects, phases)
                    "Avances" -> ProgressTab(projects, progressReports)
                    "Evaluar" -> EvaluationsTab(projects, consultants, providers, evaluations)
                    "Reportes" -> ReportsTab(projects, consultants, providers, progressReports, evaluations)
                    "Chat" -> SupervisorChatTab(projects, consultants, providers, clients, quotations)
                    else -> SupervisorPlaceholderContent(selectedTab)
                }
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
    providers: List<Employee>,
    quotations: List<Quotation>
) {
    val activeProjects = projects.filter { it.status == "En Progreso" }
    val completedProjects = projects.filter { it.status == "Finalizado" }
    val pendingProjects = projects.filter { it.status == "Pendiente" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header moved directly to cards to clean up the UI
        item { Spacer(modifier = Modifier.height(4.dp)) }

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

        // ── Proyectos aprobados por cliente (alerta destacada) ──────
        val pendingClientApprovals = quotations.filter { it.clientStatus == "Aprobada" && !it.supervisorConfirmed }
        if (pendingClientApprovals.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SuccessGreen)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Proyectos aprobados por cliente",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = SuccessGreen
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Box(
                                modifier = Modifier.size(24.dp).clip(CircleShape).background(SuccessGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${pendingClientApprovals.size}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        pendingClientApprovals.forEach { quotation ->
                            val project = projects.find { it.id == quotation.projectId }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        project?.title ?: quotation.projectTitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Navy
                                    )
                                    Text(
                                        "${quotation.providerName} · $${String.format("%,.0f", quotation.amount)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        InternalDb.supervisorAcceptProject(
                                            quotationId   = quotation.id,
                                            projectId     = quotation.projectId,
                                            supervisorUid = "sup-uid",
                                            clientUid     = "cliente-uid"
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Aceptar", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
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
    var showQuoteDialog by remember { mutableStateOf(false) }
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
                                            Text(q.projectTitle, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Navy)
                                            Text("${q.providerName} · $${String.format("%,.0f", q.amount)}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                        }
                                        val statusColor = when (q.clientStatus) {
                                            "Aprobada" -> SuccessGreen
                                            "Rechazada" -> ErrorRed
                                            else -> WarningAmber
                                        }
                                        Text(q.clientStatus, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = statusColor)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { selectedProject = project; showAssignDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Navy),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Navy)
                                ) {
                                    Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Asignar", fontSize = 11.sp)
                                }
                                OutlinedButton(
                                    onClick = { selectedProject = project; showInviteDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Mustard),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Mustard)
                                ) {
                                    Icon(Icons.Default.Mail, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Invitar", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { selectedProject = project; showQuoteDialog = true },
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                ) {
                                    Icon(Icons.Default.RequestQuote, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Cotizar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    if (showAssignDialog && selectedProject != null) {
        AssignReassignDialog(
            project = selectedProject!!,
            consultants = consultants,
            providers = providers,
            onDismiss = { showAssignDialog = false },
            onAssign = { pid, cid, prid, prName ->
                val p = projects.find { it.id == pid } ?: return@AssignReassignDialog
                InternalDb.updateProject(p.copy(consultantUid = cid, providerUid = prid, providerName = prName, status = if (prid != null) "En Progreso" else p.status))
                snackMessage = "✅ Personal asignado correctamente"
                showAssignDialog = false
            }
        )
    }

    if (showInviteDialog && selectedProject != null) {
        InviteProviderDialog(
            project = selectedProject!!,
            providers = providers,
            onDismiss = { showInviteDialog = false },
            onInvite = { pid, title, prid, prName, msg ->
                val inv = ProviderInvitation(projectId = pid, projectTitle = title, providerUid = prid, providerName = prName, message = msg)
                InternalDb.addInvitation(inv)
                snackMessage = "📩 Invitación enviada al proveedor"
                showInviteDialog = false
            }
        )
    }

    if (showQuoteDialog && selectedProject != null) {
        SendQuotationDialog(
            project = selectedProject!!,
            providers = providers,
            onDismiss = { showQuoteDialog = false },
            onSend = { projectId, projectTitle, providerUid, providerName, amount, days, description ->
                val newQuotation = Quotation(
                    projectId = projectId,
                    projectTitle = projectTitle,
                    providerUid = providerUid,
                    providerName = providerName,
                    amount = amount,
                    estimatedDays = days,
                    description = description,
                    sentToClient = true
                )
                InternalDb.addQuotation(newQuotation)
                snackMessage = "✅ Cotización enviada al cliente correctamente"
                showQuoteDialog = false
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
private fun SendQuotationDialog(
    project: Project,
    providers: List<Employee>,
    onDismiss: () -> Unit,
    onSend: (String, String, String, String, Double, Int, String) -> Unit
) {
    var selectedProvider by remember { mutableStateOf(project.providerUid ?: "") }
    var amount by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Nueva Cotización al Cliente", fontWeight = FontWeight.Bold, color = Navy)
                Text(
                    project.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Provider
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

                // Amount
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Monto total (MXN)") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Days
                OutlinedTextField(
                    value = days,
                    onValueChange = { days = it.filter { c -> c.isDigit() } },
                    label = { Text("Días estimados") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción / Condiciones") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2
                )

                // NOTICE
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Teal.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Info, null, tint = Teal, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Esta cotización será enviada directamente al portal del cliente para su autorización.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Teal
                        )
                    }
                }
            }
        },
        confirmButton = {
            val isValid = selectedProvider.isNotBlank() && amount.isNotBlank() && days.isNotBlank()
            Button(
                onClick = {
                    if (isValid) {
                        val provName = providers.find { it.uid == selectedProvider }?.nombreCompleto ?: ""
                        onSend(
                            project.id,
                            project.title,
                            selectedProvider,
                            provName,
                            amount.toDoubleOrNull() ?: 0.0,
                            days.toIntOrNull() ?: 0,
                            description
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Navy),
                shape = RoundedCornerShape(10.dp),
                enabled = isValid
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Enviar al Cliente")
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

// ═════════════════════════════════════════════════════════════════
//  DRAWER ITEM & PLACEHOLDER
// ═════════════════════════════════════════════════════════════════

@Composable
fun DrawerItem(
    icon: ImageVector, 
    label: String, 
    isSelected: Boolean, 
    color: Color = Color.White,
    onClick: () -> Unit
) {
    val background = if (isSelected) NavyItemHover else Color.Transparent
    val tint = if (isSelected) Mustard else color
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = tint.copy(alpha = if (isSelected) 1f else 0.7f), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = tint.copy(alpha = if (isSelected) 1f else 0.8f))
    }
}

@Composable
fun SupervisorPlaceholderContent(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Construction, null, modifier = Modifier.size(64.dp), tint = TextMuted.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
            Text("Módulo en desarrollo", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
    }
}

// ═════════════════════════════════════════════════════════════════
//  NEW TAB — Cotizaciones a Cliente
// ═════════════════════════════════════════════════════════════════

@Composable
fun SupervisorQuotationsTab(projects: List<Project>, providers: List<Employee>) {
    var showQuoteDialog by remember { mutableStateOf(false) }
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionHeader("Enviar Cotizaciones al Cliente")
                Text("Selecciona un proyecto activo para generar y enviar la cotización de forma directa.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                Spacer(modifier = Modifier.height(8.dp))
            }

            val activeProjects = projects.filter { it.status == "Pendiente" || it.status == "En Progreso" }
            if (activeProjects.isEmpty()) {
                item {
                    Text("No hay proyectos disponibles para cotizar.", color = TextMuted)
                }
            } else {
                items(activeProjects) { project ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(project.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Navy)
                            Text(project.description, style = MaterialTheme.typography.bodySmall, color = TextMuted, maxLines = 2)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    selectedProject = project
                                    showQuoteDialog = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                            ) {
                                Icon(Icons.Default.RequestQuote, null, modifier = Modifier.size(16.dp), tint = SurfaceWhite)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Crear y Enviar Cotización", fontSize = 12.sp, color = SurfaceWhite, fontWeight = FontWeight.ExtraBold)
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

    if (showQuoteDialog && selectedProject != null) {
        SendQuotationDialog(
            project = selectedProject!!,
            providers = providers,
            onDismiss = { showQuoteDialog = false },
            onSend = { projectId, projectTitle, providerUid, providerName, amount, days, description ->
                val newQuotation = Quotation(
                    projectId = projectId,
                    projectTitle = projectTitle,
                    providerUid = providerUid,
                    providerName = providerName,
                    amount = amount,
                    estimatedDays = days,
                    description = description,
                    sentToClient = true
                )
                InternalDb.addQuotation(newQuotation)
                scope.launch { snackbarHostState.showSnackbar("✅ Cotización enviada al cliente exitosamente") }
                showQuoteDialog = false
            }
        )
    }
}

// ═════════════════════════════════════════════════════════════════
//  NEW TAB — Chat Organizacional
// ═════════════════════════════════════════════════════════════════

@Composable
fun SupervisorChatTab(
    projects: List<Project>,
    consultants: List<Employee>,
    providers: List<Employee>,
    clients: List<Employee>,
    quotations: List<Quotation>
) {
    var selectedUserUid  by remember { mutableStateOf<String?>(null) }
    var selectedUserName by remember { mutableStateOf("") }
    var selectedIsClient by remember { mutableStateOf(false) }

    val currentSupervisorUid = "sup-uid"

    if (selectedUserUid != null) {
        if (selectedIsClient) {
            SupervisorClientChatDetailScreen(
                supervisorUid = currentSupervisorUid,
                clientUid     = selectedUserUid!!,
                clientName    = selectedUserName,
                quotations    = quotations,
                projects      = projects,
                onBack        = { selectedUserUid = null; selectedIsClient = false }
            )
        } else {
            SupervisorChatDetailScreen(
                supervisorUid  = currentSupervisorUid,
                otherUserUid   = selectedUserUid!!,
                otherUserName  = selectedUserName,
                onBack         = { selectedUserUid = null }
            )
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text(
                "Chat Organizacional",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Navy
            )
            Text(
                "Comunícate con consultores, proveedores y clientes.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                // ── Clientes (sección destacada) ───────────────────────────
                if (clients.isNotEmpty()) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Business, null, tint = Teal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "CLIENTES",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Teal
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    items(clients) { client ->
                        // Check if there are approved quotations pending supervisor confirmation
                        val pendingApprovals = quotations.count {
                            it.clientStatus == "Aprobada" && !it.supervisorConfirmed
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                selectedUserUid  = client.uid
                                selectedUserName = client.nombreCompleto
                                selectedIsClient = true
                            },
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                            border = if (pendingApprovals > 0)
                                androidx.compose.foundation.BorderStroke(1.5.dp, SuccessGreen)
                            else null
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Teal.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Business, null, tint = Teal)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(client.nombreCompleto, fontWeight = FontWeight.Bold, color = Navy)
                                    Text(
                                        if (pendingApprovals > 0)
                                            "$pendingApprovals proyecto(s) aprobado(s) — requiere aceptación"
                                        else "Chat directo con el cliente",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (pendingApprovals > 0) SuccessGreen else TextMuted,
                                        fontWeight = if (pendingApprovals > 0) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                                if (pendingApprovals > 0) {
                                    Box(
                                        modifier = Modifier.size(22.dp).clip(CircleShape).background(SuccessGreen),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("$pendingApprovals", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // ── Consultores ─────────────────────────────────────
                if (consultants.isNotEmpty()) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Engineering, null, tint = Navy, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "CONSULTORES",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Navy
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    items(consultants) { person ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                selectedUserUid  = person.uid
                                selectedUserName = person.nombreCompleto
                                selectedIsClient = false
                            },
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = Navy.copy(alpha = 0.1f)) {
                                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, null, tint = Navy) }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(person.nombreCompleto, fontWeight = FontWeight.Bold, color = Navy)
                                    Text(person.puesto.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                }
                                Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // ── Proveedores ─────────────────────────────────────
                if (providers.isNotEmpty()) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Handyman, null, tint = Mustard, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "PROVEEDORES",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Mustard
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    items(providers) { person ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                selectedUserUid  = person.uid
                                selectedUserName = person.nombreCompleto
                                selectedIsClient = false
                            },
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = Mustard.copy(alpha = 0.1f)) {
                                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, null, tint = Mustard) }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(person.nombreCompleto, fontWeight = FontWeight.Bold, color = Navy)
                                    Text(person.puesto.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                }
                                Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
                            }
                        }
                    }
                }

                if (clients.isEmpty() && consultants.isEmpty() && providers.isEmpty()) {
                    item { Text("No hay contactos disponibles.", color = TextMuted) }
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════
//  Chat Supervisor ↔ Cliente  (con panel de aceptación de proyecto)
// ═════════════════════════════════════════════════════════════════
@Composable
fun SupervisorClientChatDetailScreen(
    supervisorUid : String,
    clientUid     : String,
    clientName    : String,
    quotations    : List<Quotation>,
    projects      : List<Project>,
    onBack        : () -> Unit
) {
    val allMessages by InternalDb.chatMessages.collectAsState()
    val chatMessages = remember(allMessages) {
        allMessages.filter {
            (it.senderUid == supervisorUid && it.receiverUid == clientUid) ||
            (it.senderUid == clientUid     && it.receiverUid == supervisorUid)
        }.sortedBy { it.timestamp }
    }

    // Quotations the client approved but supervisor hasn't confirmed yet
    val pendingApprovalPairs = remember(quotations, projects) {
        quotations
            .filter { it.clientStatus == "Aprobada" && !it.supervisorConfirmed }
            .mapNotNull { q -> projects.find { p -> p.id == q.projectId }?.let { p -> Pair(q, p) } }
    }

    var messageText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(SurfaceGray)) {

        // ── Top bar ───────────────────────────────────────────────
        Surface(color = SurfaceWhite, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Navy) }
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Teal.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Business, null, tint = Teal, modifier = Modifier.size(20.dp)) }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(clientName, fontWeight = FontWeight.Bold, color = Navy)
                    Text("Cliente · Chat Directo", style = MaterialTheme.typography.labelSmall, color = Teal, fontWeight = FontWeight.Bold)
                }
            }
        }

        // ── Panel de proyectos aprobados ────────────────────────────
        if (pendingApprovalPairs.isNotEmpty()) {
            Surface(
                color = SuccessGreen.copy(alpha = 0.07f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "El cliente aprobó ${pendingApprovalPairs.size} cotización(es) — pendiente de aceptar",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    pendingApprovalPairs.forEach { (quotation, project) ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(project.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Navy)
                                    Text(
                                        "Monto: $${String.format("%,.0f", quotation.amount)} · ${quotation.estimatedDays} días",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                    Text(
                                        "Estado del proyecto: ${project.status}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (project.status == "Pendiente") WarningAmber else SuccessGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        InternalDb.supervisorAcceptProject(
                                            quotationId    = quotation.id,
                                            projectId      = project.id,
                                            supervisorUid  = supervisorUid,
                                            clientUid      = clientUid
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Aceptar", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Mensajes ───────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (chatMessages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxWidth().height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Chat, null, tint = TextMuted.copy(alpha = 0.3f), modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Inicia la conversación con el cliente", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            items(chatMessages) { msg ->
                val isMine = msg.senderUid == supervisorUid
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart) {
                    Surface(
                        color = if (isMine) Navy else Color.White,
                        shape = RoundedCornerShape(
                            topStart = 16.dp, topEnd = 16.dp,
                            bottomStart = if (isMine) 16.dp else 0.dp,
                            bottomEnd   = if (isMine) 0.dp  else 16.dp
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.message,
                            modifier = Modifier.padding(12.dp),
                            color = if (isMine) Color.White else Navy,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // ── Input ───────────────────────────────────────────────
        Surface(color = SurfaceWhite, modifier = Modifier.imePadding()) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Escribe un mensaje al cliente...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            InternalDb.addChatMessage(ChatMessage(
                                senderUid   = supervisorUid,
                                receiverUid = clientUid,
                                projectId   = "SUP_CLI_DIRECT",
                                message     = messageText.trim()
                            ))
                            messageText = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Navy, contentColor = Color.White),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null)
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════
//  Chat estándar Supervisor ↔ Consultor/Proveedor
// ═════════════════════════════════════════════════════════════════
@Composable
fun SupervisorChatDetailScreen(supervisorUid: String, otherUserUid: String, otherUserName: String, onBack: () -> Unit) {
    val allMessages by InternalDb.chatMessages.collectAsState()
    val chatMessages = allMessages.filter { 
        (it.senderUid == supervisorUid && it.receiverUid == otherUserUid) || 
        (it.senderUid == otherUserUid && it.receiverUid == supervisorUid)
    }.sortedBy { it.timestamp }

    var messageText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(SurfaceGray)) {
        Surface(color = SurfaceWhite, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Navy) }
                Column {
                    Text(otherUserName, fontWeight = FontWeight.Bold, color = Navy)
                    Text("Chat Privado", style = MaterialTheme.typography.labelSmall, color = Mustard, fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatMessages) { msg ->
                val isMine = msg.senderUid == supervisorUid
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart) {
                    Surface(
                        color = if (isMine) Navy else Color.White,
                        shape = RoundedCornerShape(
                            topStart = 16.dp, 
                            topEnd = 16.dp, 
                            bottomStart = if (isMine) 16.dp else 0.dp, 
                            bottomEnd = if (isMine) 0.dp else 16.dp
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.message,
                            modifier = Modifier.padding(12.dp),
                            color = if (isMine) Color.White else Navy,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Surface(color = SurfaceWhite, modifier = Modifier.imePadding()) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Escribe un mensaje...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            InternalDb.addChatMessage(ChatMessage(
                                senderUid = supervisorUid,
                                receiverUid = otherUserUid,
                                projectId = "SUPERVISION",
                                message = messageText
                            ))
                            messageText = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Navy, contentColor = Color.White),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null)
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════
//  TAB — Pagos a Proveedores (Project Centric for Supervisor)
// ═════════════════════════════════════════════════════════════════
@Composable
fun SupervisorProjectPaymentsTab(
    projects: List<Project>,
    phases: List<PaymentPhase>
) {
    val activeProjects = projects.filter { it.status == "En Progreso" }
    var selectedProjectForPhases by remember { mutableStateOf<Project?>(null) }
    val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    if (activeProjects.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay proyectos activos en este momento.", color = TextMuted)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionHeader("Definir Fases de Pago por Proyecto")
                Text("Gestiona los montos y etapas de pago para cada obra activa.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(activeProjects) { project ->
                val projectPhases = phases.filter { it.projectId == project.id }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusDot(project.status)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(project.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Navy, modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Proveedor: ${project.providerName}", style = MaterialTheme.typography.labelMedium, color = Teal, fontWeight = FontWeight.Bold)
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = SurfaceGray)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text("Resumen de Fases:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Navy)
                        if (projectPhases.isEmpty()) {
                            Text("1 fase por defecto (sin definir monto)", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        } else {
                            projectPhases.sortedBy { it.phaseNumber }.forEach { phase ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Fase ${phase.phaseNumber}/${phase.totalPhases}", style = MaterialTheme.typography.bodySmall, color = Navy)
                                    Text(fmt.format(phase.amountToPay), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Teal)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { selectedProjectForPhases = project },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Navy),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Settings, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gestionar Fases")
                        }
                    }
                }
            }
        }
    }

    if (selectedProjectForPhases != null) {
        val project = selectedProjectForPhases!!
        ManagePhasesDialog(
            provider = ProviderAccountSummary(
                providerId = project.providerUid ?: "",
                providerName = project.providerName,
                totalServiceAmountEarned = 0.0,
                totalAmountPaid = 0.0,
                pendingBalance = 0.0,
                totalCompanyProfit = 0.0
            ),
            phases = phases.filter { it.projectId == project.id },
            availableProjects = listOf(project),
            onDismiss = { selectedProjectForPhases = null },
            onAddPhase = { InternalDb.addPaymentPhase(it) },
            onDeletePhase = { InternalDb.deletePaymentPhase(it) }
        )
    }
}
