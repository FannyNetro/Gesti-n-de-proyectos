package com.vgtech.mobile.ui.screens.consultor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.AssignmentReturn
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Rule
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vgtech.mobile.data.local.InternalDb
import com.vgtech.mobile.data.model.ChatMessage
import com.vgtech.mobile.data.model.Project
import com.vgtech.mobile.data.model.ProjectCancellationRequest
import com.vgtech.mobile.data.model.ProjectProgress
import com.vgtech.mobile.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ConsultorDashboardScreen — overview for consultant role with progress evaluation and image uploads.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultorDashboardScreen(
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf("Tablero") }
    
    val currentConsultantUid = "consultor-uid"
    val allProjects by InternalDb.projects.collectAsState()
    val myProjects = allProjects.filter { it.consultantUid == currentConsultantUid }

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
                    Text("Consultoría Profesional", style = MaterialTheme.typography.labelMedium, color = Teal, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(40.dp))
                DrawerItem(Icons.Default.Dashboard, "Tablero de Proyectos", selectedTab == "Tablero") { 
                    selectedTab = "Tablero"
                    scope.launch { drawerState.close() } 
                }
                DrawerItem(Icons.AutoMirrored.Filled.Message, "Mensajes", selectedTab == "Mensajes") { 
                    selectedTab = "Mensajes"
                    scope.launch { drawerState.close() } 
                }
                DrawerItem(Icons.AutoMirrored.Filled.Rule, "Evaluar Avances", selectedTab == "Evaluar") { 
                    selectedTab = "Evaluar"
                    scope.launch { drawerState.close() } 
                }
                DrawerItem(Icons.Default.History, "Historial de Proveedores", selectedTab == "Historial") { 
                    selectedTab = "Historial"
                    scope.launch { drawerState.close() } 
                }
                DrawerItem(Icons.AutoMirrored.Filled.AssignmentReturn, "Solicitud Baja de Proyecto", selectedTab == "Baja") { 
                    selectedTab = "Baja"
                    scope.launch { drawerState.close() } 
                }
                DrawerItem(Icons.Default.Star, "Evaluaciones", selectedTab == "Evaluaciones") { 
                    selectedTab = "Evaluaciones"
                    scope.launch { drawerState.close() } 
                }
                Spacer(modifier = Modifier.weight(1f))
                DrawerItem(Icons.AutoMirrored.Filled.Logout, "Cerrar Sesión", false, ErrorRed) {
                    onLogout()
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Panel de Consultor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SurfaceWhite) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menú", tint = SurfaceWhite) } },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Navy)
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding).background(SurfaceGray)) {
                when (selectedTab) {
                    "Tablero" -> {
                        val highPriority = myProjects.filter { it.progress >= 0.8f }
                        val mediumPriority = myProjects.filter { it.progress in 0.4f..0.79f }
                        val lowPriority = myProjects.filter { it.progress < 0.4f }
                        KanbanBoardContent(highPriority, mediumPriority, lowPriority)
                    }
                    "Evaluar" -> EvaluationListScreen(myProjects)
                    "Mensajes" -> ChatListScreen(myProjects, currentConsultantUid)
                    "Historial" -> ProviderHistoryScreen(myProjects)
                    "Baja" -> ProjectWithdrawalScreen(myProjects, currentConsultantUid)
                    "Evaluaciones" -> EvaluationsScreen(myProjects)
                    else -> PlaceholderContent(selectedTab)
                }
            }
        }
    }
}

@Composable
fun EvaluationListScreen(myProjects: List<Project>) {
    val allReports by InternalDb.projectProgressReports.collectAsState()
    val myProjectIds = myProjects.map { it.id }
    val reportsToEvaluate = allReports.filter { myProjectIds.contains(it.projectId) }
    
    val selectedReportState = remember { mutableStateOf<ProjectProgress?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Evaluar Avances", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Navy)
        Text("Revisa y califica los reportes enviados por tus proveedores.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (reportsToEvaluate.isEmpty()) {
                item { Text("No hay reportes de avance para evaluar.", color = TextMuted) }
            } else {
                items(reportsToEvaluate.sortedByDescending { it.date }) { report ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { selectedReportState.value = report },
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(report.projectTitle, fontWeight = FontWeight.Bold, color = Navy)
                                if (report.evaluated) {
                                    Surface(color = SuccessGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                                        Text("Evaluado", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = SuccessGreen, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Surface(color = ErrorRed.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                                        Text("Pendiente", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = ErrorRed, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(report.description, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                            Text("Avance Reportado: ${report.progressPercentage}%", fontWeight = FontWeight.Bold, color = Teal, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }

    selectedReportState.value?.let { report ->
        EvaluationDialog(
            report = report,
            onDismiss = { selectedReportState.value = null },
            onConfirm = { eval, comments, rating, img, modProgress, modReason ->
                InternalDb.evaluateProgress(report.id, eval, comments, rating, img, modProgress, modReason)
                selectedReportState.value = null
            }
        )
    }
}

@Composable
fun EvaluationDialog(
    report: ProjectProgress,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Float, String?, Int?, String) -> Unit
) {
    var evaluation by remember { mutableStateOf(if (report.evaluated) report.consultantEvaluation else "Aprobado") }
    var comments by remember { mutableStateOf(if (report.evaluated) report.consultantComments else "") }
    var rating by remember { mutableFloatStateOf(if (report.evaluated) report.evaluationRating else 5f) }
    
    // Modification logic
    var isCorrectingProgress by remember { mutableStateOf(report.wasModified) }
    var correctedProgress by remember { mutableFloatStateOf(report.progressPercentage.toFloat()) }
    var modificationReason by remember { mutableStateOf(report.modificationReason) }

    val options = listOf("Aprobado", "Con Observaciones", "Rechazado")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text("Evaluar Avance", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Navy)
                Text(report.projectTitle, color = Teal, fontWeight = FontWeight.Bold)
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                
                Text("Detalle del Reporte", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                Text(report.description, style = MaterialTheme.typography.bodyMedium)
                if (report.issues.isNotBlank()) {
                    Text("Problemas Reportados:", color = ErrorRed, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp))
                    Text(report.issues, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // Correction Section
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("¿Corregir porcentaje de avance?", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Switch(checked = isCorrectingProgress, onCheckedChange = { isCorrectingProgress = it })
                }

                if (isCorrectingProgress) {
                    Text("Reportado: ${report.progressPercentage}% | Ajustado: ${correctedProgress.toInt()}%", color = Mustard, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = correctedProgress, 
                        onValueChange = { correctedProgress = it }, 
                        valueRange = 0f..100f, 
                        steps = 99,
                        colors = SliderDefaults.colors(thumbColor = Mustard, activeTrackColor = Mustard)
                    )
                    OutlinedTextField(
                        value = modificationReason,
                        onValueChange = { modificationReason = it },
                        label = { Text("¿Por qué modificas el avance?") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej: El colado aún no fragua...") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Text("Veredicto Técnico", fontWeight = FontWeight.Bold, color = Navy)
                options.forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { evaluation = option }) {
                        RadioButton(selected = evaluation == option, onClick = { evaluation = option })
                        Text(option, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Calificación Técnica (1-5)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Slider(value = rating, onValueChange = { rating = it }, valueRange = 1f..5f, steps = 3, colors = SliderDefaults.colors(thumbColor = Navy, activeTrackColor = Navy))
                
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = comments,
                    onValueChange = { comments = it },
                    label = { Text("Comentarios Generales") },
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { /* Mock Upload */ },
                    color = SurfaceGray,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CloudUpload, null, tint = Navy)
                        Text("Subir Evidencia (Imagen)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Button(
                        onClick = { 
                            onConfirm(
                                evaluation, 
                                comments, 
                                rating, 
                                null, 
                                if (isCorrectingProgress) correctedProgress.toInt() else null,
                                modificationReason
                            ) 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Navy),
                        enabled = !isCorrectingProgress || modificationReason.isNotBlank()
                    ) {
                        Text("Guardar Evaluación")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListScreen(myProjects: List<Project>, consultantUid: String) {
    val selectedProjectForChatState = remember { mutableStateOf<Project?>(null) }

    selectedProjectForChatState.value?.let { selectedProject ->
        ChatDetailScreen(
            project = selectedProject,
            senderUid = consultantUid,
            onBack = { selectedProjectForChatState.value = null }
        )
    } ?: run {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text("Mensajería con Proveedores", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Navy)
            Text("Conversaciones internas con proveedores de tus proyectos.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (myProjects.isEmpty()) {
                    Text("No tienes proyectos asignados con proveedores.", modifier = Modifier.padding(top = 20.dp))
                } else {
                    myProjects.distinctBy { it.providerUid }.forEach { project ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { selectedProjectForChatState.value = project },
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = Navy.copy(alpha = 0.1f)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Person, null, tint = Navy)
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(project.providerName, fontWeight = FontWeight.Bold, color = Navy)
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
fun ChatDetailScreen(project: Project, senderUid: String, onBack: () -> Unit) {
    val allMessages by InternalDb.chatMessages.collectAsState()
    val chatMessages = allMessages.filter { 
        (it.senderUid == senderUid && it.receiverUid == project.providerUid) || 
        (it.senderUid == project.providerUid && it.receiverUid == senderUid)
    }.sortedBy { it.timestamp }

    var messageText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(SurfaceGray)) {
        Surface(
            color = SurfaceWhite, 
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Navy) }
                Column {
                    Text(project.providerName, fontWeight = FontWeight.Bold, color = Navy)
                    Text(project.title, style = MaterialTheme.typography.labelSmall, color = Mustard, fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatMessages) { msg ->
                val isMine = msg.senderUid == senderUid
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
                                senderUid = senderUid,
                                receiverUid = project.providerUid ?: "",
                                projectId = project.id,
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

@Composable
fun KanbanBoardContent(high: List<Project>, medium: List<Project>, low: List<Project>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Tablero de Control", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Navy)
            Text("Mueve tus proyectos y marca los más importantes con una estrella.", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(99.dp)).background(Mustard))
        }

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KanbanColumn("ALTA", "Fase Final / 80%+", ErrorRed, high)
            KanbanColumn("MEDIA", "En Proceso / 40%-79%", Mustard, medium)
            KanbanColumn("BAJA", "Planeación / < 40%", SuccessGreen, low)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun KanbanColumn(title: String, subtitle: String, color: Color, projects: List<Project>) {
    Column(modifier = Modifier.width(300.dp).fillMaxHeight()) {
        Card(
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(12.dp), 
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite), 
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(99.dp)).background(color))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = Navy)
                    Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                Spacer(modifier = Modifier.weight(1f))
                Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(projects.size.toString(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (projects.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Sin proyectos", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            } else {
                projects.forEach { ProjectKanbanCard(it) }
            }
        }
    }
}

@Composable
fun ProjectKanbanCard(project: Project) {
    var showMoveMenu by remember { mutableStateOf(false) }
    val cardBorder = if (project.isMarked) 2.dp else 0.dp
    val borderColor = if (project.isMarked) Mustard else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(cardBorder, borderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (project.isMarked) Mustard.copy(alpha = 0.05f) else SurfaceWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(project.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Navy, modifier = Modifier.weight(1f))
                
                IconButton(
                    onClick = { InternalDb.toggleProjectMark(project.id) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (project.isMarked) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Marcar",
                        tint = if (project.isMarked) Mustard else TextMuted.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(project.description, style = MaterialTheme.typography.bodySmall, color = TextMuted, maxLines = 2)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { project.progress }, 
                    modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(99.dp)), 
                    color = when {
                        project.progress >= 0.8f -> ErrorRed
                        project.progress >= 0.4f -> Mustard
                        else -> SuccessGreen
                    },
                    trackColor = Color.LightGray.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("${(project.progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = Navy)
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Box {
                Button(
                    onClick = { showMoveMenu = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceGray, contentColor = Navy),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    Icon(Icons.Default.OpenWith, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mover a Fase...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                DropdownMenu(
                    expanded = showMoveMenu,
                    onDismissRequest = { showMoveMenu = false },
                    modifier = Modifier.background(SurfaceWhite)
                ) {
                    DropdownMenuItem(
                        text = { Text("Fase BAJA (20%)") },
                        leadingIcon = { Icon(Icons.Default.ArrowDownward, null, tint = SuccessGreen) },
                        onClick = {
                            InternalDb.updateProjectProgress(project.id, 0.2f)
                            showMoveMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Fase MEDIA (60%)") },
                        leadingIcon = { Icon(Icons.Default.SyncAlt, null, tint = Mustard) },
                        onClick = {
                            InternalDb.updateProjectProgress(project.id, 0.6f)
                            showMoveMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Fase ALTA (90%)") },
                        leadingIcon = { Icon(Icons.Default.ArrowUpward, null, tint = ErrorRed) },
                        onClick = {
                            InternalDb.updateProjectProgress(project.id, 0.9f)
                            showMoveMenu = false
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("FINALIZAR (100%)") },
                        leadingIcon = { Icon(Icons.Default.CheckCircle, null, tint = Navy) },
                        onClick = {
                            InternalDb.updateProjectProgress(project.id, 1.0f)
                            showMoveMenu = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProviderHistoryScreen(myProjects: List<Project>) {
    val selectedProjectState = remember { mutableStateOf<Project?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Historial de Proveedores", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Navy)
        Text("Proveedores con los que has colaborado y el desempeño en sus proyectos.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        
        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (myProjects.isEmpty()) {
                Text("No hay historial disponible.", color = TextMuted)
            } else {
                myProjects.forEach { project ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { selectedProjectState.value = project },
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(project.providerName, fontWeight = FontWeight.Bold, color = Navy)
                                Text("Proyecto: ${project.title}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
                        }
                    }
                }
            }
        }
    }

    selectedProjectState.value?.let { selectedProject ->
        ProjectDetailDialog(
            project = selectedProject,
            title = "Detalle del Proveedor",
            isProviderFocus = true,
            onDismiss = { selectedProjectState.value = null }
        )
    }
}

@Composable
fun EvaluationsScreen(myProjects: List<Project>) {
    val selectedProjectState = remember { mutableStateOf<Project?>(null) }
    val finishedProjects = myProjects.filter { it.status == "Finalizado" }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Mis Evaluaciones", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Navy)
        Text("Resultados y feedback de tus proyectos concluidos.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        
        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (finishedProjects.isEmpty()) {
                Text("Aún no tienes evaluaciones registradas.", color = TextMuted)
            } else {
                finishedProjects.forEach { project ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { selectedProjectState.value = project },
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(project.title, fontWeight = FontWeight.Bold, color = Navy)
                                Text("Resultado: ${project.evaluationResult}", style = MaterialTheme.typography.bodySmall, color = SuccessGreen, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${project.consultantRating}", fontWeight = FontWeight.ExtraBold, color = Mustard, fontSize = 18.sp)
                                Text("Rating", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                        }
                    }
                }
            }
        }
    }

    selectedProjectState.value?.let { selectedProject ->
        ProjectDetailDialog(
            project = selectedProject,
            title = "Mi Desempeño",
            isProviderFocus = false,
            onDismiss = { selectedProjectState.value = null }
        )
    }
}

@Composable
fun ProjectDetailDialog(project: Project, title: String, isProviderFocus: Boolean, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } },
        title = { Text(title, fontWeight = FontWeight.Bold, color = Navy) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(project.title, style = MaterialTheme.typography.titleMedium, color = Mustard, fontWeight = FontWeight.Bold)
                if (isProviderFocus) Text("Proveedor: ${project.providerName}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Progress
                Text("Avance del Proyecto", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { project.progress },
                        modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(99.dp)),
                        color = Mustard
                    )
                    Text(" ${(project.progress * 100).toInt()}%", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Comments
                Text("Comentarios / Feedback", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Text(project.comments.ifEmpty { "Sin comentarios registrados." }, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                // Delays
                if (project.hasDelays) {
                    Surface(color = ErrorRed.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Retraso Detectado", color = ErrorRed, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(project.delayReason, style = MaterialTheme.typography.bodySmall, color = ErrorRed)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Rating
                val rating = if (isProviderFocus) project.providerRating else project.consultantRating
                Text(if (isProviderFocus) "Calificación del Proveedor" else "Mi Calificación", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (index < rating.toInt()) Mustard else TextMuted.copy(alpha = 0.3f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(" $rating", fontWeight = FontWeight.ExtraBold, color = Navy, modifier = Modifier.padding(start = 8.dp))
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = SurfaceWhite
    )
}

@Composable
fun ProjectWithdrawalScreen(myProjects: List<Project>, consultantUid: String) {
    var showHistory by remember { mutableStateOf(false) }
    val selectedProjectState = remember { mutableStateOf<Project?>(null) }
    val selectedProjectForBaja = selectedProjectState.value
    
    val cancellationHistory by InternalDb.cancellationRequests.collectAsState()
    val myHistory = cancellationHistory.filter { it.consultantUid == consultantUid }

    if (selectedProjectForBaja != null) {
        BajaForm(
            project = selectedProjectForBaja,
            onDismiss = { selectedProjectState.value = null },
            onSubmit = { reason, details ->
                InternalDb.addProjectCancellationRequest(
                    ProjectCancellationRequest(
                        projectId = selectedProjectForBaja.id,
                        projectTitle = selectedProjectForBaja.title,
                        consultantUid = consultantUid,
                        reason = reason,
                        details = details
                    )
                )
                selectedProjectState.value = null
            }
        )
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showHistory) "Historial de Bajas" else "Solicitud de Baja",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Navy
                )
                Button(
                    onClick = { showHistory = !showHistory },
                    colors = ButtonDefaults.buttonColors(containerColor = if (showHistory) Navy else Mustard)
                ) {
                    Icon(if (showHistory) Icons.AutoMirrored.Filled.List else Icons.Default.History, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (showHistory) "Ver Proyectos" else "Ver Historial")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            if (showHistory) {
                CancellationHistoryList(myHistory)
            } else {
                Text("Selecciona un proyecto en proceso para solicitar tu baja:", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val inProcess = myProjects.filter { it.status == "En Progreso" }
                    if (inProcess.isEmpty()) {
                        Text("No tienes proyectos activos en este momento.", modifier = Modifier.padding(top = 20.dp), style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    } else {
                        inProcess.forEach { project ->
                            ProjectBajaCard(project) { selectedProjectState.value = project }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectBajaCard(project: Project, onBajaClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(project.title, fontWeight = FontWeight.Bold, color = Navy)
                Text(project.description, style = MaterialTheme.typography.bodySmall, color = TextMuted, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { project.progress },
                    modifier = Modifier.width(100.dp).height(4.dp).clip(RoundedCornerShape(99.dp)),
                    color = Mustard
                )
            }
            Button(
                onClick = onBajaClick,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed.copy(alpha = 0.1f), contentColor = ErrorRed),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Solicitar Baja", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BajaForm(project: Project, onDismiss: () -> Unit, onSubmit: (String, String) -> Unit) {
    var reason by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    val reasons = listOf("Carga de trabajo", "Motivos personales", "Cambio de departamento", "Otro")
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        IconButton(onClick = onDismiss) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Navy) }
        Text("Formulario de Baja", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Navy)
        Text("Proyecto: ${project.title}", style = MaterialTheme.typography.bodyMedium, color = Mustard, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Motivo de la baja", fontWeight = FontWeight.Bold, color = Navy)
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            OutlinedTextField(
                value = reason,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Selecciona un motivo") },
                trailingIcon = { IconButton(onClick = { expanded = true }) { Icon(Icons.Default.ArrowDropDown, null) } }
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                reasons.forEach { r ->
                    DropdownMenuItem(text = { Text(r) }, onClick = { reason = r; expanded = false })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Detalles adicionales", fontWeight = FontWeight.Bold, color = Navy)
        OutlinedTextField(
            value = details,
            onValueChange = { details = it },
            modifier = Modifier.fillMaxWidth().height(150.dp).padding(vertical = 8.dp),
            placeholder = { Text("Explica brevemente los motivos de tu solicitud...") }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { if(reason.isNotEmpty()) onSubmit(reason, details) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Navy),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Enviar Solicitud", modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun CancellationHistoryList(history: List<ProjectCancellationRequest>) {
    if (history.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No tienes solicitudes previas.", color = TextMuted)
        }
    } else {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            history.reversed().forEach { req ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(req.projectTitle, fontWeight = FontWeight.Bold, color = Navy)
                            Surface(
                                color = if (req.status == "Pendiente") Mustard.copy(alpha = 0.1f) else SuccessGreen.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(req.status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = if (req.status == "Pendiente") Mustard else SuccessGreen)
                            }
                        }
                        Text("Motivo: ${req.reason}", style = MaterialTheme.typography.bodySmall)
                        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(req.date))
                        Text("Fecha: $dateStr", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                }
            }
        }
    }
}

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
fun PlaceholderContent(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Construction, null, modifier = Modifier.size(64.dp), tint = TextMuted.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
            Text("Módulo en desarrollo", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
    }
}
