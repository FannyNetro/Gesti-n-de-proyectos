package com.vgtech.mobile.ui.screens.proveedor

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
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vgtech.mobile.data.local.InternalDb
import com.vgtech.mobile.data.model.ChatMessage
import com.vgtech.mobile.data.model.Project
import com.vgtech.mobile.data.model.ProjectProgress
import com.vgtech.mobile.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProveedorDashboardScreen(onLogout: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedProjectForDetail by remember { mutableStateOf<Project?>(null) }
    var showReportDialogFor by remember { mutableStateOf<Project?>(null) }
    
    // For demo purposes, current provider UID
    val currentProviderUid = "prov-uid"

    val tabs = listOf(
        TabItem("Activos", Icons.Default.Engineering),
        TabItem("Mensajes", Icons.AutoMirrored.Filled.Message),
        TabItem("Historial", Icons.Default.History),
        TabItem("Cotizar", Icons.Default.AddBusiness),
        TabItem("Reportes", Icons.Default.Assessment)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("VG Tech", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Navy)
                        Text("Panel de Proveedor", style = MaterialTheme.typography.labelMedium, color = Teal)
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Salir", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize().background(SurfaceGray)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceWhite,
                contentColor = Teal,
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, item ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(item.title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(item.icon, contentDescription = null) }
                    )
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> ProyectosActivosView(
                        providerUid = currentProviderUid,
                        onProjectClick = { selectedProjectForDetail = it },
                        onReportClick = { showReportDialogFor = it }
                    )
                    1 -> ProviderChatListScreen(currentProviderUid)
                    2 -> HistorialProyectosView(currentProviderUid)
                    3 -> FormularioCotizacionView()
                    4 -> HistorialReportesView(currentProviderUid)
                }
            }
        }

        if (selectedProjectForDetail != null) {
            ProjectDetailDialog(
                project = selectedProjectForDetail!!,
                onDismiss = { selectedProjectForDetail = null },
                onReportProgress = { 
                    showReportDialogFor = it
                    selectedProjectForDetail = null
                }
            )
        }

        if (showReportDialogFor != null) {
            ReportProgressDialog(
                project = showReportDialogFor!!,
                providerUid = currentProviderUid,
                onDismiss = { showReportDialogFor = null },
                onSend = { report ->
                    InternalDb.addProjectProgress(report)
                    showReportDialogFor = null
                }
            )
        }
    }
}

data class TabItem(val title: String, val icon: ImageVector)

@Composable
fun ProviderChatListScreen(providerUid: String) {
    val allProjects by InternalDb.projects.collectAsState()
    val providerProjects = allProjects.filter { it.providerUid == providerUid }
    val selectedProjectForChatState = remember { mutableStateOf<Project?>(null) }

    selectedProjectForChatState.value?.let { project ->
        ProviderChatDetailScreen(
            project = project,
            providerUid = providerUid,
            onBack = { selectedProjectForChatState.value = null }
        )
    } ?: run {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text("Mensajes con Consultores", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Navy)
            Text("Conversaciones con los consultores de tus proyectos.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val consultants = providerProjects.filter { it.consultantUid != null }
                if (consultants.isEmpty()) {
                    item { Text("No tienes consultores asignados en tus proyectos.", color = TextMuted) }
                } else {
                    items(consultants) { project ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { selectedProjectForChatState.value = project },
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = Teal.copy(alpha = 0.1f)) {
                                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, null, tint = Teal) }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Consultor del Proyecto", fontWeight = FontWeight.Bold, color = Navy)
                                    Text("Proyecto: ${project.title}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                }
                                Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderChatDetailScreen(project: Project, providerUid: String, onBack: () -> Unit) {
    val allMessages by InternalDb.chatMessages.collectAsState()
    val consultantUid = project.consultantUid ?: ""
    val chatMessages = allMessages.filter { 
        (it.senderUid == providerUid && it.receiverUid == consultantUid) || 
        (it.senderUid == consultantUid && it.receiverUid == providerUid)
    }.sortedBy { it.timestamp }

    var messageText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(SurfaceGray)) {
        Surface(color = SurfaceWhite, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Navy) }
                Column {
                    Text("Consultor", fontWeight = FontWeight.Bold, color = Navy)
                    Text(project.title, style = MaterialTheme.typography.labelSmall, color = Teal, fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatMessages) { msg ->
                val isMine = msg.senderUid == providerUid
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart) {
                    Surface(
                        color = if (isMine) Teal else Color.White,
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
                    placeholder = { Text("Escribe un mensaje al consultor...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            InternalDb.addChatMessage(ChatMessage(
                                senderUid = providerUid,
                                receiverUid = consultantUid,
                                projectId = project.id,
                                message = messageText
                            ))
                            messageText = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Teal, contentColor = Color.White),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null)
                }
            }
        }
    }
}

@Composable
fun ProyectosActivosView(providerUid: String, onProjectClick: (Project) -> Unit, onReportClick: (Project) -> Unit) {
    val projects by InternalDb.projects.collectAsState()
    val providerProjects = remember(projects) { projects.filter { it.providerUid == providerUid && it.status != "Finalizado" } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { 
            Text("Proyectos en Curso", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Navy)
        }
        
        if (providerProjects.isEmpty()) {
            item {
                Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes proyectos activos.", color = TextMuted)
                }
            }
        } else {
            items(providerProjects) { project ->
                ProjectCard(
                    project = project,
                    onClick = { onProjectClick(project) },
                    onReportClick = { onReportClick(project) }
                )
            }
        }
    }
}

@Composable
fun ProjectCard(project: Project, onClick: () -> Unit, onReportClick: () -> Unit) {
    val statusColor = when(project.status) {
        "En Progreso" -> Teal
        "Recién Iniciado" -> Color(0xFFFFA000)
        else -> Navy
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(project.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Navy)
                    Text(project.description, style = MaterialTheme.typography.bodySmall, color = Teal, fontWeight = FontWeight.Medium)
                }
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        project.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { project.progress },
                    modifier = Modifier.weight(1f).height(8.dp),
                    color = Teal,
                    trackColor = TealLight.copy(alpha = 0.3f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("${(project.progress * 100).toInt()}%", fontWeight = FontWeight.Bold, color = Navy)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onReportClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Navy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reportar Avance")
            }
        }
    }
}

@Composable
fun ProjectDetailDialog(project: Project, onDismiss: () -> Unit, onReportProgress: (Project) -> Unit) {
    val reports by InternalDb.projectProgressReports.collectAsState()
    val projectReports = remember(reports) { reports.filter { it.projectId == project.id }.sortedByDescending { it.date } }
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(project.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Navy)
                        Text(project.description, color = Teal)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { project.progress },
                        modifier = Modifier.weight(1f).height(10.dp),
                        color = Teal,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("${(project.progress * 100).toInt()}%", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = Navy)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onReportProgress(project) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nuevo Reporte de Avance")
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Historial de Avances", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Navy)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (projectReports.isEmpty()) {
                        item { Text("No hay reportes registrados para este proyecto.", style = MaterialTheme.typography.bodySmall, color = TextMuted) }
                    } else {
                        items(projectReports) { report ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text(report.reportType, style = MaterialTheme.typography.labelLarge, color = Teal, fontWeight = FontWeight.Bold)
                                        Text(sdf.format(Date(report.date)), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                    }
                                    Text("Avance: ${report.progressPercentage}%", fontWeight = FontWeight.Bold, color = Navy)
                                    Text(report.description, style = MaterialTheme.typography.bodySmall)
                                    
                                    if (report.highlights.isNotBlank()) {
                                        Text("Puntos Positivos:", style = MaterialTheme.typography.labelSmall, color = SuccessGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                                        Text(report.highlights, style = MaterialTheme.typography.bodySmall)
                                    }
                                    if (report.issues.isNotBlank()) {
                                        Text("Obstáculos / Problemas:", style = MaterialTheme.typography.labelSmall, color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                                        Text(report.issues, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportProgressDialog(
    project: Project,
    providerUid: String,
    onDismiss: () -> Unit,
    onSend: (ProjectProgress) -> Unit
) {
    var percentage by remember { mutableFloatStateOf(project.progress * 100f) }
    var description by remember { mutableStateOf(project.description) }
    var reportType by remember { mutableStateOf("Diario") }
    var highlights by remember { mutableStateOf("") }
    var issues by remember { mutableStateOf("") }
    var delayReason by remember { mutableStateOf("") }
    
    val reportTypes = listOf("Diario", "Semanal", "Mensual")
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(scrollState)) {
                Text("Nuevo Reporte de Avance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Navy)
                Text(project.title, style = MaterialTheme.typography.bodySmall, color = Teal)
                
                Spacer(modifier = Modifier.height(20.dp))

                Text("Frecuencia del Reporte", style = MaterialTheme.typography.labelMedium, color = Navy)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    reportTypes.forEach { type ->
                        FilterChip(
                            selected = reportType == type,
                            onClick = { reportType = type },
                            label = { Text(type) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Porcentaje de avance: ${percentage.toInt()}%", fontWeight = FontWeight.Bold, color = Navy)
                Slider(
                    value = percentage,
                    onValueChange = { percentage = it },
                    valueRange = 0f..100f,
                    steps = 19,
                    colors = SliderDefaults.colors(thumbColor = Teal, activeTrackColor = Teal)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Actividad Principal / Fase Actual") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = highlights,
                    onValueChange = { highlights = it },
                    label = { Text("Puntos Positivos / Logros") },
                    placeholder = { Text("¿Qué salió bien?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = issues,
                    onValueChange = { issues = it },
                    label = { Text("Obstáculos / Problemas") },
                    placeholder = { Text("¿Qué salió mal?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = delayReason,
                    onValueChange = { delayReason = it },
                    label = { Text("Motivo de retraso (si aplica)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { 
                            onSend(ProjectProgress(
                                projectId = project.id,
                                projectTitle = project.title,
                                providerUid = providerUid, 
                                providerName = "Proveedor General",
                                progressPercentage = percentage.toInt(),
                                description = description,
                                reportType = reportType,
                                highlights = highlights,
                                issues = issues,
                                delayReason = if (delayReason.isBlank()) null else delayReason
                            ))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Teal),
                        enabled = description.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Enviar Reporte")
                    }
                }
            }
        }
    }
}

@Composable
fun HistorialProyectosView(providerUid: String) {
    val projects by InternalDb.projects.collectAsState()
    val finishedProjects = remember(projects) { projects.filter { it.providerUid == providerUid && it.status == "Finalizado" } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Proyectos Finalizados", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Navy)
            Text("Registro de obras concluidas satisfactoriamente", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (finishedProjects.isEmpty()) {
            item {
                Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay proyectos finalizados aún.", color = TextMuted)
                }
            }
        } else {
            items(finishedProjects) { proyecto ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(TealLight.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Teal)
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(proyecto.title, fontWeight = FontWeight.Bold, color = Navy)
                            Text(proyecto.description, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                        
                        Text(
                            "Completado",
                            fontWeight = FontWeight.ExtraBold,
                            color = Teal,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistorialReportesView(providerUid: String) {
    val reports by InternalDb.projectProgressReports.collectAsState()
    val providerReports = remember(reports) { reports.filter { it.providerUid == providerUid } }
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { 
            Text("Historial de Avances", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Navy)
            Text("Reportes enviados a supervisión", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (providerReports.isEmpty()) {
            item {
                Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No has enviado reportes de avance todavía.", color = TextMuted)
                }
            }
        } else {
            items(providerReports.sortedByDescending { it.date }) { report ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Assessment, contentDescription = null, tint = Teal, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(report.reportType, style = MaterialTheme.typography.labelLarge, color = Teal, fontWeight = FontWeight.Bold)
                                }
                                Text(report.projectTitle, fontWeight = FontWeight.Bold, color = Navy)
                            }
                            Text("${report.progressPercentage}%", fontWeight = FontWeight.ExtraBold, color = Teal, style = MaterialTheme.typography.titleLarge)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(sdf.format(Date(report.date)), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        
                        Text(report.description, color = Navy, style = MaterialTheme.typography.bodyMedium)
                        
                        if (report.highlights.isNotBlank()) {
                            Text("Puntos Positivos:", style = MaterialTheme.typography.labelSmall, color = SuccessGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                            Text(report.highlights, style = MaterialTheme.typography.bodySmall)
                        }
                        
                        if (report.issues.isNotBlank()) {
                            Text("Obstáculos / Problemas:", style = MaterialTheme.typography.labelSmall, color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                            Text(report.issues, style = MaterialTheme.typography.bodySmall)
                        }

                        if (!report.delayReason.isNullOrBlank()) {
                            Surface(
                                color = Color.Red.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("MOTIVO DE RETRASO:", style = MaterialTheme.typography.labelSmall, color = Color.Red, fontWeight = FontWeight.ExtraBold)
                                    Text(report.delayReason, style = MaterialTheme.typography.bodySmall, color = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormularioCotizacionView() {
    var costo by remember { mutableStateOf("") }
    var tiempo by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    
    Column(modifier = Modifier.padding(16.dp).verticalScroll(scrollState)) {
        Text("Cargar Nueva Cotización", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Navy)
        Text("Envía tu propuesta comercial para nuevos proyectos", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = costo, 
            onValueChange = { costo = it }, 
            label = { Text("Costo Propuesto") }, 
            modifier = Modifier.fillMaxWidth(),
            prefix = { Text("$ ") },
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = tiempo, 
            onValueChange = { tiempo = it }, 
            label = { Text("Tiempo Estimado (Días)") }, 
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.LightGray.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(40.dp), tint = Teal)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Selecciona tu archivo PDF", fontWeight = FontWeight.Bold)
                Text("Máximo 10MB", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(onClick = { }) {
                    Text("Explorar Archivos", color = Teal)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { }, 
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Teal),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Enviar Propuesta Formal", fontWeight = FontWeight.Bold)
        }
    }
}
