package com.vgtech.mobile.ui.screens.rh

import android.app.DatePickerDialog
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vgtech.mobile.data.local.InternalDb
import com.vgtech.mobile.data.model.Employee
import com.vgtech.mobile.data.model.VacationRequest
import com.vgtech.mobile.data.model.VacationStatus
import com.vgtech.mobile.data.model.WorkLog
import com.vgtech.mobile.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// ═══════════════════════════════════════════════════════════════════
//  VacationHistoryScreen — Vacaciones + Horas Extras + Nómina
// ═══════════════════════════════════════════════════════════════════

@Composable
fun VacationHistoryScreen() {
    val employees by InternalDb.employees.collectAsState()
    val requests by InternalDb.vacationRequests.collectAsState()
    val workLogs by InternalDb.workLogs.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    val selectedEmployeeState = remember { mutableStateOf<Employee?>(null) }
    var currentView by remember { mutableIntStateOf(0) } // 0=Empleados, 1=Historial, 2=Nómina

    val filteredEmployees = remember(searchQuery, employees) {
        employees.filter { 
            it.activo && 
            it.puesto != "Proveedor" && 
            (searchQuery.isBlank() || it.nombreCompleto.contains(searchQuery, ignoreCase = true))
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(SurfaceWhite)) {
        // ── Header ──────────────────────────────────────────────
        Surface(
            color = Navy,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Vacaciones y Nómina",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                // Tab selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Empleados", "Historial", "Nómina").forEachIndexed { index, label ->
                        FilterChip(
                            selected = currentView == index,
                            onClick = { currentView = index },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Teal,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White.copy(alpha = 0.1f),
                                labelColor = Color.White.copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
                
                if (currentView == 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar empleado por nombre...", color = Color.LightGray) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Teal) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                            focusedBorderColor = Teal,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }
            }
        }

        // ── Content ─────────────────────────────────────────────
        when (currentView) {
            0 -> {
                // ── Employee table ──────────────────────────────
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    item {
                        Text("Selecciona un empleado para registrar vacaciones", style = MaterialTheme.typography.titleSmall, color = TextMuted)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Table header
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                                .background(Navy)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Nombre", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1.4f))
                            Text("Puesto", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
                            Text("Días Disp.", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(0.6f), textAlign = TextAlign.Center)
                            Box(modifier = Modifier.width(40.dp))
                        }
                    }

                    // Table rows
                    items(filteredEmployees) { emp ->
                        val bgColor = if (filteredEmployees.indexOf(emp) % 2 == 0) SurfaceWhite else SurfaceGray
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(bgColor)
                                .clickable { selectedEmployeeState.value = emp }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(emp.nombreCompleto, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Navy, modifier = Modifier.weight(1.4f))
                            Text(emp.puesto, style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
                            Text(
                                "${emp.diasVacaciones}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (emp.diasVacaciones > 0) Teal else ErrorRed,
                                modifier = Modifier.weight(0.6f),
                                textAlign = TextAlign.Center
                            )
                            Icon(
                                Icons.Default.ChevronRight, null,
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                    }

                    if (filteredEmployees.isEmpty()) {
                        item {
                            Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No se encontraron empleados.", color = TextMuted)
                            }
                        }
                    }
                }
            }
            1 -> {
                // ── Vacation history TABLE ───────────────────────
                val sortedRequests = requests.sortedByDescending { it.requestDate }
                val sdf = remember { SimpleDateFormat("dd/MM/yy", Locale.getDefault()) }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    item {
                        Text("Historial de Solicitudes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Navy)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Table header
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Row(
                                modifier = Modifier
                                    .widthIn(min = 700.dp)
                                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                                    .background(Navy)
                                    .padding(horizontal = 8.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableHeaderCell("Empleado", Modifier.width(130.dp))
                                TableHeaderCell("Inicio", Modifier.width(75.dp))
                                TableHeaderCell("Término", Modifier.width(75.dp))
                                TableHeaderCell("Días", Modifier.width(45.dp))
                                TableHeaderCell("½ Día", Modifier.width(45.dp))
                                TableHeaderCell("Efectivo", Modifier.width(55.dp))
                                TableHeaderCell("Estado", Modifier.width(85.dp))
                                TableHeaderCell("Acciones", Modifier.width(90.dp))
                            }
                        }
                    }

                    if (sortedRequests.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No hay solicitudes registradas.", color = TextMuted)
                            }
                        }
                    }

                    // Table rows
                    items(sortedRequests) { request ->
                        val rowBg = if (sortedRequests.indexOf(request) % 2 == 0) SurfaceWhite else SurfaceGray
                        val statusColor = when (request.status) {
                            VacationStatus.PENDING -> WarningAmber
                            VacationStatus.APPROVED -> SuccessGreen
                            VacationStatus.REJECTED -> ErrorRed
                        }
                        val statusLabel = when (request.status) {
                            VacationStatus.PENDING -> "Pendiente"
                            VacationStatus.APPROVED -> "Aprobado"
                            VacationStatus.REJECTED -> "Rechazado"
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Row(
                                modifier = Modifier
                                    .widthIn(min = 700.dp)
                                    .background(rowBg)
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Empleado
                                Text(
                                    request.employeeName,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Navy,
                                    modifier = Modifier.width(130.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                // Inicio
                                Text(
                                    sdf.format(Date(request.startDate)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Navy,
                                    modifier = Modifier.width(75.dp),
                                    textAlign = TextAlign.Center
                                )
                                // Término
                                Text(
                                    sdf.format(Date(request.endDate)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Navy,
                                    modifier = Modifier.width(75.dp),
                                    textAlign = TextAlign.Center
                                )
                                // Días
                                Text(
                                    "${request.daysRequested}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy,
                                    modifier = Modifier.width(45.dp),
                                    textAlign = TextAlign.Center
                                )
                                // ½ Día
                                Text(
                                    if (request.halfDays > 0) "${request.halfDays}" else "-",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (request.halfDays > 0) MustardDark else TextMuted,
                                    fontWeight = if (request.halfDays > 0) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.width(45.dp),
                                    textAlign = TextAlign.Center
                                )
                                // Efectivo
                                Text(
                                    "${request.effectiveDays}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Teal,
                                    modifier = Modifier.width(55.dp),
                                    textAlign = TextAlign.Center
                                )
                                // Estado chip
                                Box(modifier = Modifier.width(85.dp), contentAlignment = Alignment.Center) {
                                    Surface(
                                        color = statusColor.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            statusLabel,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = statusColor,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                                // Acciones
                                Row(modifier = Modifier.width(90.dp), horizontalArrangement = Arrangement.Center) {
                                    if (request.status == VacationStatus.PENDING) {
                                        IconButton(
                                            onClick = { InternalDb.updateVacationStatus(request.id, VacationStatus.REJECTED) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Close, null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                                        }
                                        IconButton(
                                            onClick = { InternalDb.updateVacationStatus(request.id, VacationStatus.APPROVED) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Check, null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                        }
                                    } else {
                                        Text("—", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                    }
                }
            }
            2 -> {
                // Payroll / Nómina
                PayrollView(employees = filteredEmployees, workLogs = workLogs, requests = requests)
            }
        }
    }

    // ── Add Vacation Dialog ──────────────────────────────────────
    selectedEmployeeState.value?.let { employee ->
        AddVacationDialog(
            employee = employee,
            onDismiss = { selectedEmployeeState.value = null },
            onConfirm = { request ->
                InternalDb.addVacationRequest(request)
                // Descontar días de vacaciones del empleado
                val newDays = (employee.diasVacaciones - request.daysRequested).coerceAtLeast(0)
                InternalDb.updateEmployee(employee.copy(diasVacaciones = newDays))
                selectedEmployeeState.value = null
            }
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
//  Employee Row
// ═══════════════════════════════════════════════════════════════════

@Composable
fun EmployeeVacationRow(employee: Employee, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(8.dp), color = TealLight.copy(alpha = 0.3f)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(employee.nombreCompleto.take(1), fontWeight = FontWeight.Bold, color = Teal)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(employee.nombreCompleto, fontWeight = FontWeight.Bold, color = Navy)
                Text(employee.puesto, style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${employee.diasVacaciones}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = if (employee.diasVacaciones > 0) Teal else ErrorRed)
                Text("disponibles", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  Vacation Request Item (with half-day info)
// ═══════════════════════════════════════════════════════════════════

@Composable
fun VacationRequestItem(request: VacationRequest) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val color = when (request.status) {
        VacationStatus.PENDING -> Color(0xFFFFA000)
        VacationStatus.APPROVED -> Teal
        VacationStatus.REJECTED -> ErrorRed
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(request.employeeName, fontWeight = FontWeight.Bold, color = Navy)
                Text("Solicitado: ${sdf.format(Date(request.requestDate))}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                Text("${sdf.format(Date(request.startDate))} al ${sdf.format(Date(request.endDate))}", style = MaterialTheme.typography.bodySmall, color = Navy)
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Total: ", style = MaterialTheme.typography.bodySmall, color = Navy)
                    Text("${request.effectiveDays}", fontWeight = FontWeight.Bold, color = Teal)
                    Text(" días", style = MaterialTheme.typography.bodySmall, color = Navy)
                    if (request.halfDays > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            color = Mustard.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                " ${request.halfDays} medio día${if (request.halfDays > 1) "s" else ""} ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MustardDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        when (request.status) {
                            VacationStatus.PENDING -> "PENDIENTE"
                            VacationStatus.APPROVED -> "APROBADO"
                            VacationStatus.REJECTED -> "RECHAZADO"
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (request.status == VacationStatus.PENDING) {
                    Row {
                        IconButton(onClick = { InternalDb.updateVacationStatus(request.id, VacationStatus.REJECTED) }) { Icon(Icons.Default.Close, null, tint = ErrorRed) }
                        IconButton(onClick = { InternalDb.updateVacationStatus(request.id, VacationStatus.APPROVED) }) { Icon(Icons.Default.Check, null, tint = Teal) }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  Add Vacation Dialog — Fixed consecutive days + half-day support
// ═══════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVacationDialog(employee: Employee, onDismiss: () -> Unit, onConfirm: (VacationRequest) -> Unit) {
    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableLongStateOf(System.currentTimeMillis() + 86400000L) }
    var isHalfDayStart by remember { mutableStateOf(false) }
    var isHalfDayEnd by remember { mutableStateOf(false) }
    
    // Horas extras
    var overtimeHours by remember { mutableStateOf("") }
    var overtimeRate by remember { mutableStateOf("200") } // porcentaje

    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // ── Fixed: consecutive business days calculation ──────────
    val totalCalendarDays = remember(startDate, endDate) {
        if (endDate >= startDate) {
            val cal = Calendar.getInstance()
            cal.timeInMillis = startDate
            val endCal = Calendar.getInstance()
            endCal.timeInMillis = endDate
            
            var count = 0
            while (!cal.after(endCal)) {
                val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                // Contar solo días laborales (lunes a viernes)
                if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                    count++
                }
                cal.add(Calendar.DAY_OF_MONTH, 1)
            }
            count
        } else 0
    }

    // Half day adjustments
    val halfDayCount = (if (isHalfDayStart) 1 else 0) + (if (isHalfDayEnd) 1 else 0)
    val effectiveDays = totalCalendarDays.toDouble() - (halfDayCount * 0.5)
    val effectiveHours = effectiveDays * 8.0

    // Overtime
    val overtimeHoursVal = overtimeHours.toDoubleOrNull() ?: 0.0
    val overtimeMultiplier = (overtimeRate.toDoubleOrNull() ?: 200.0) / 100.0
    val hourlyRate = employee.pagoPorHora
    val overtimePay = overtimeHoursVal * hourlyRate * overtimeMultiplier

    // Vacation balance
    val pendActual = employee.diasVacaciones
    val pendAnteriores = 0 
    val totalDispPend = pendActual + pendAnteriores
    val finalDisponibles = (totalDispPend - totalCalendarDays).coerceAtLeast(0)
    
    val canRegister = totalCalendarDays in 1..totalDispPend

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.95f).padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Fecha de solicitud:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(sdf.format(Date()), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    "Solicitud de Vacaciones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Navy,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Employee info
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    FormLabelValue("Nombre:", employee.nombreCompleto.uppercase())
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) { FormLabelValue("Departamento:", employee.puesto.uppercase()) }
                        Box(modifier = Modifier.weight(0.5f)) { FormLabelValue("Num Empleado:", employee.uid.take(6).uppercase()) }
                    }
                }

                HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.5f))

                // ── Dates ────────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Start date row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Fecha inicio:", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(100.dp))
                        DatePickerBox(sdf.format(Date(startDate))) { showDatePicker(context, startDate) { startDate = it } }
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = isHalfDayStart,
                            onClick = { isHalfDayStart = !isHalfDayStart },
                            label = { Text("½ día", fontSize = 11.sp) },
                            leadingIcon = if (isHalfDayStart) {{ Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp)) }} else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Mustard.copy(alpha = 0.2f),
                                selectedLabelColor = MustardDark
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // End date row  
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Fecha término:", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(100.dp))
                        DatePickerBox(sdf.format(Date(endDate))) { showDatePicker(context, endDate) { endDate = it } }
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = isHalfDayEnd,
                            onClick = { isHalfDayEnd = !isHalfDayEnd },
                            label = { Text("½ día", fontSize = 11.sp) },
                            leadingIcon = if (isHalfDayEnd) {{ Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp)) }} else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Mustard.copy(alpha = 0.2f),
                                selectedLabelColor = MustardDark
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                // ── Total days/hours summary ─────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TealLight.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Días laborales", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text("$totalCalendarDays", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Navy)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Días efectivos", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text("$effectiveDays", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Teal)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Horas", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text("${effectiveHours.toInt()}h", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Mustard)
                            }
                        }
                        if (halfDayCount > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Incluye $halfDayCount medio día${if (halfDayCount > 1) "s" else ""}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MustardDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // ── Vacation balance ─────────────────────────────
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CalculationLine("Días pendientes del periodo actual:", "$pendActual")
                    CalculationLine("Días pendientes de periodos anteriores:", "$pendAnteriores")
                    CalculationLine("Total vacaciones disponibles:", "$totalDispPend", isBold = true, isGray = true)
                    CalculationLine("Días solicitados:", "$totalCalendarDays", isGray = true)
                    CalculationLine("Días disponibles después:", "$finalDisponibles", isBold = true, isGray = true, textColor = if (finalDisponibles > 0) Teal else ErrorRed)
                }

                if (totalCalendarDays > totalDispPend) {
                    Text("⚠️ Saldo insuficiente de días de vacaciones.", color = ErrorRed, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }

                HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.5f))

                // ── Overtime section ─────────────────────────────
                Text("Horas Extras", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Navy)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = overtimeHours,
                        onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) overtimeHours = it },
                        label = { Text("Horas extras") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Schedule, null, tint = Mustard, modifier = Modifier.size(18.dp)) }
                    )
                    OutlinedTextField(
                        value = overtimeRate,
                        onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) overtimeRate = it },
                        label = { Text("Tasa %") },
                        modifier = Modifier.width(100.dp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        suffix = { Text("%") }
                    )
                }

                if (overtimeHoursVal > 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Mustard.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tarifa por hora:", style = MaterialTheme.typography.bodySmall, color = Navy)
                                Text(currencyFormatter.format(hourlyRate), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Navy)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Horas extra × ${overtimeRate}%:", style = MaterialTheme.typography.bodySmall, color = Navy)
                                Text("${overtimeHoursVal}h × ${currencyFormatter.format(hourlyRate)} × $overtimeMultiplier", style = MaterialTheme.typography.bodySmall, color = Navy)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pago horas extras:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MustardDark)
                                Text(currencyFormatter.format(overtimePay), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = MustardDark)
                            }
                        }
                    }
                }

                // ── Action buttons ───────────────────────────────
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar", color = Navy, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            // Register vacation request
                            val request = VacationRequest(
                                employeeUid = employee.uid,
                                employeeName = employee.nombreCompleto,
                                startDate = startDate,
                                endDate = endDate,
                                daysRequested = totalCalendarDays,
                                halfDays = halfDayCount,
                                isHalfDayStart = isHalfDayStart,
                                isHalfDayEnd = isHalfDayEnd
                            )
                            onConfirm(request)

                            // Also register overtime as a work log if hours exist
                            if (overtimeHoursVal > 0) {
                                InternalDb.addWorkLog(WorkLog(
                                    employeeUid = employee.uid,
                                    employeeName = employee.nombreCompleto,
                                    hoursWorked = 0.0,
                                    overtimeHours = overtimeHoursVal,
                                    overtimeRate = overtimeMultiplier,
                                    hourlyRateAtTime = hourlyRate,
                                    observations = "Horas extras - Periodo vacacional"
                                ))
                            }
                        },
                        enabled = canRegister,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Teal),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Procesar")
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  Payroll / Nómina View  (HU: Generar la nómina)
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun PayrollView(
    employees: List<Employee>,
    workLogs: List<WorkLog>,
    requests: List<VacationRequest>
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Nómina Generada", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Navy)
            Text("Resumen de sueldos, horas extras y deducciones", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
        }

        val activeEmployees = employees.filter { it.activo && it.puesto != "Proveedor" && it.puesto != "Cliente" }

        items(activeEmployees) { emp ->
            val empLogs = workLogs.filter { it.employeeUid == emp.uid }
            val empVacations = requests.filter { it.employeeUid == emp.uid && it.status == VacationStatus.APPROVED }
            
            val totalRegularHours = empLogs.sumOf { it.hoursWorked }
            val totalOvertimeHours = empLogs.sumOf { it.overtimeHours }
            val totalOvertimePay = empLogs.sumOf { it.overtimeHours * it.hourlyRateAtTime * it.overtimeRate }
            val regularPay = emp.sueldo
            val totalPay = regularPay + totalOvertimePay
            val vacDaysTaken = empVacations.sumOf { it.daysRequested }

            var expanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .clickable { expanded = !expanded },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(10.dp), color = TealLight.copy(alpha = 0.3f)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(emp.nombreCompleto.take(1), fontWeight = FontWeight.Bold, color = Teal)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(emp.nombreCompleto, fontWeight = FontWeight.Bold, color = Navy)
                            Text(emp.puesto, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(currencyFormatter.format(totalPay), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = Teal)
                            Text("Total nómina", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }

                    if (expanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = BorderColor)
                        Spacer(modifier = Modifier.height(12.dp))

                        PayrollLine("Sueldo base mensual", currencyFormatter.format(regularPay))
                        PayrollLine("Pago por hora", "${currencyFormatter.format(emp.pagoPorHora)}/h")
                        PayrollLine("Horas regulares registradas", "${totalRegularHours}h")
                        
                        if (totalOvertimeHours > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Mustard.copy(alpha = 0.1f))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Horas extras", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MustardDark)
                                    Text("${totalOvertimeHours}h", style = MaterialTheme.typography.bodySmall, color = Navy)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Pago extras", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                    Text(currencyFormatter.format(totalOvertimePay), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = MustardDark)
                                }
                            }
                        }

                        PayrollLine("Días vacaciones tomados", "$vacDaysTaken días")
                        PayrollLine("Días vacaciones disponibles", "${emp.diasVacaciones} días")

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = BorderColor)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TOTAL NÓMINA", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = Navy)
                            Text(currencyFormatter.format(totalPay), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = Teal)
                        }
                    }
                }
            }
        }

        // Grand total
        item {
            val grandTotal = activeEmployees.sumOf { emp ->
                val overtimePay = workLogs.filter { it.employeeUid == emp.uid }.sumOf { it.overtimeHours * it.hourlyRateAtTime * it.overtimeRate }
                emp.sueldo + overtimePay
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Navy),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Nómina General", style = MaterialTheme.typography.titleSmall, color = SurfaceWhite.copy(alpha = 0.7f))
                        Text("${activeEmployees.size} empleado(s)", style = MaterialTheme.typography.labelSmall, color = SurfaceWhite.copy(alpha = 0.5f))
                    }
                    Text(currencyFormatter.format(grandTotal), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Teal)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  Shared Composables
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun PayrollLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Navy)
    }
}

@Composable
fun FormLabelValue(label: String, value: String) {
    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(vertical = 2.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Black)
        Spacer(modifier = Modifier.width(4.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Navy)
            HorizontalDivider(thickness = 1.dp, color = Color.Black.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun DatePickerBox(dateText: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(130.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(dateText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Navy)
    }
}

@Composable
fun CalculationLine(label: String, value: String, isBold: Boolean = false, isGray: Boolean = false, textColor: Color = Navy) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            label, 
            modifier = Modifier.weight(1f), 
            style = MaterialTheme.typography.bodySmall, 
            color = if (isBold) Color.Black else Color.DarkGray,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Surface(
            modifier = Modifier.width(60.dp).height(32.dp),
            color = if (isGray) Color.LightGray.copy(alpha = 0.5f) else Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.Gray),
            shape = RoundedCornerShape(2.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = textColor)
            }
        }
    }
}

fun showDatePicker(context: android.content.Context, initialDate: Long, onDateSelected: (Long) -> Unit) {
    val calendar = Calendar.getInstance().apply { timeInMillis = initialDate }
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val result = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onDateSelected(result.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

@Composable
private fun TableHeaderCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = modifier,
        textAlign = TextAlign.Center,
        fontSize = 10.sp
    )
}
