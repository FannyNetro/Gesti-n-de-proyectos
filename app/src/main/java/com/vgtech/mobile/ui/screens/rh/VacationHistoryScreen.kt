package com.vgtech.mobile.ui.screens.rh

import android.app.DatePickerDialog
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vgtech.mobile.data.local.InternalDb
import com.vgtech.mobile.data.model.Employee
import com.vgtech.mobile.data.model.VacationRequest
import com.vgtech.mobile.data.model.VacationStatus
import com.vgtech.mobile.data.model.WorkLog
import com.vgtech.mobile.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VacationHistoryScreen() {
    val employees by InternalDb.employees.collectAsState()
    val requests by InternalDb.vacationRequests.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    val selectedEmployeeState = remember { mutableStateOf<Employee?>(null) }
    var currentView by remember { mutableIntStateOf(0) } // 0=Empleados, 1=Historial, 2=Nómina

    val filteredEmployees = remember(searchQuery, employees) {
        employees.filter { 
            it.activo && 
            it.puesto != "Proveedor" && 
            it.puesto != "Cliente" &&
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
                Text(
                    "Vacaciones y Nómina",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))

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
                        placeholder = { Text("Buscar empleado...", color = Color.LightGray) },
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredEmployees) { emp ->
                        EmployeeVacationRow(emp) { selectedEmployeeState.value = emp }
                    }
                }
            }
            1 -> {
                val sortedRequests = requests.sortedByDescending { it.requestDate }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(sortedRequests) { request ->
                        VacationRequestItem(request)
                    }
                }
            }
            2 -> {
                PayrollView(filteredEmployees, requests)
            }
        }
    }

    selectedEmployeeState.value?.let { employee ->
        AddVacationDialog(
            employee = employee,
            onDismiss = { selectedEmployeeState.value = null },
            onConfirm = { request ->
                InternalDb.addVacationRequest(request)
                if (request.status == VacationStatus.APPROVED) {
                    InternalDb.updateEmployee(employee.copy(diasVacaciones = employee.diasVacaciones - request.effectiveDays))
                }
                selectedEmployeeState.value = null
            }
        )
    }
}

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
        }
    }
}

@Composable
fun VacationRequestItem(request: VacationRequest) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val color = when (request.status) {
        VacationStatus.PENDING -> WarningAmber
        VacationStatus.APPROVED -> SuccessGreen
        VacationStatus.REJECTED -> ErrorRed
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(request.employeeName, fontWeight = FontWeight.Bold, color = Navy)
                    Text("Solicitado: ${sdf.format(Date(request.requestDate))}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        request.status.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("${sdf.format(Date(request.startDate))} al ${sdf.format(Date(request.endDate))}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            if (request.hoursRequested > 0) {
                Text("Horas solicitadas: ${request.hoursRequested}", style = MaterialTheme.typography.bodySmall, color = Teal, fontWeight = FontWeight.ExtraBold)
            } else {
                Text("Días efectivos: ${request.effectiveDays}", style = MaterialTheme.typography.bodySmall, color = Teal, fontWeight = FontWeight.ExtraBold)
            }
            
            if (request.status == VacationStatus.PENDING) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = { InternalDb.updateVacationStatus(request.id, VacationStatus.REJECTED) }) {
                        Icon(Icons.Default.Close, null, tint = ErrorRed)
                    }
                    IconButton(onClick = { 
                        InternalDb.updateVacationStatus(request.id, VacationStatus.APPROVED)
                    }) {
                        Icon(Icons.Default.Check, null, tint = SuccessGreen)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVacationDialog(employee: Employee, onDismiss: () -> Unit, onConfirm: (VacationRequest) -> Unit) {
    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var observations by remember { mutableStateOf("") }
    var requestType by remember { mutableStateOf("Vacaciones") }
    var hoursInput by remember { mutableStateOf("") }
    var expandedType by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val availableHours = employee.diasVacaciones * 8.0
    val requestedHours = if (requestType == "Horas trabajadas") hoursInput.toDoubleOrNull() ?: 0.0 else 0.0
    val isInvalidHours = requestedHours > availableHours

    // ── Lógica de Cálculo de Días ──────────────────────────
    val effectiveDays = remember(startDate, endDate, requestType, requestedHours) {
        if (requestType == "Horas trabajadas") {
            requestedHours / 8.0
        } else {
            if (endDate < startDate) return@remember 0.0
            
            val cal = Calendar.getInstance()
            cal.timeInMillis = startDate
            val endCal = Calendar.getInstance()
            endCal.timeInMillis = endDate
            
            var total = 0.0
            while (!cal.after(endCal)) {
                when (cal.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.SATURDAY -> total += 0.5
                    Calendar.SUNDAY -> { /* No cuenta */ }
                    else -> total += 1.0
                }
                cal.add(Calendar.DAY_OF_MONTH, 1)
            }
            total
        }
    }

    val saldoRestante = (employee.diasVacaciones - effectiveDays)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Nueva Solicitud", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Navy)
                
                // 1. Info del empleado
                Column(modifier = Modifier.fillMaxWidth().background(SurfaceGray, RoundedCornerShape(12.dp)).padding(12.dp)) {
                    Text("Empleado: ${employee.nombreCompleto}", fontWeight = FontWeight.Bold, color = Navy)
                    Text("Puesto: ${employee.puesto}", style = MaterialTheme.typography.bodySmall)
                    Text("Horas disponibles: $availableHours", fontWeight = FontWeight.ExtraBold, color = Teal)
                    Text("Días disponibles: ${employee.diasVacaciones}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }

                // 2. Formulario - Dropdown List for request type
                Text("Tipo de Solicitud", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = !expandedType },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = requestType,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        listOf("Vacaciones", "Horas trabajadas", "Permiso Especial").forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    requestType = type
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                if (requestType == "Horas trabajadas") {
                    OutlinedTextField(
                        value = hoursInput,
                        onValueChange = { hoursInput = it },
                        label = { Text("Horas a solicitar") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = isInvalidHours,
                        supportingText = {
                            if (isInvalidHours) {
                                Text("No puedes pedir más horas de las disponibles ($availableHours)", color = ErrorRed)
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Fecha Inicio", style = MaterialTheme.typography.labelSmall)
                        Box(modifier = Modifier.fillMaxWidth().border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)).clickable {
                            val c = Calendar.getInstance().apply { timeInMillis = startDate }
                            DatePickerDialog(context, { _, y, m, d ->
                                startDate = Calendar.getInstance().apply { set(y, m, d, 0, 0, 0) }.timeInMillis
                                if (requestType == "Horas trabajadas") endDate = startDate
                            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
                        }.padding(12.dp)) {
                            Text(sdf.format(Date(startDate)))
                        }
                    }
                    if (requestType != "Horas trabajadas") {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Fecha Fin", style = MaterialTheme.typography.labelSmall)
                            Box(modifier = Modifier.fillMaxWidth().border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)).clickable {
                                val c = Calendar.getInstance().apply { timeInMillis = endDate }
                                DatePickerDialog(context, { _, y, m, d ->
                                    endDate = Calendar.getInstance().apply { set(y, m, d, 0, 0, 0) }.timeInMillis
                                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
                            }.padding(12.dp)) {
                                Text(sdf.format(Date(endDate)))
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = observations,
                    onValueChange = { observations = it },
                    label = { Text("Comentario (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // 3. Resumen Automático
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = (if (isInvalidHours) ErrorRed else Teal).copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, (if (isInvalidHours) ErrorRed else Teal).copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(if (requestType == "Horas trabajadas") "Horas equivalentes (días):" else "Días solicitados:")
                            Text("$effectiveDays", fontWeight = FontWeight.ExtraBold, color = if (isInvalidHours) ErrorRed else Teal)
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Saldo anterior:", style = MaterialTheme.typography.bodySmall)
                            Text("${employee.diasVacaciones}", style = MaterialTheme.typography.bodySmall)
                        }
                        HorizontalDivider(color = (if (isInvalidHours) ErrorRed else Teal).copy(alpha = 0.1f))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Saldo restante:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("$saldoRestante", fontWeight = FontWeight.ExtraBold, color = if (saldoRestante >= 0) Teal else ErrorRed)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cerrar") }
                    Button(
                        onClick = {
                            onConfirm(VacationRequest(
                                employeeUid = employee.uid,
                                employeeName = employee.nombreCompleto,
                                startDate = startDate,
                                endDate = if (requestType == "Horas trabajadas") startDate else endDate,
                                daysRequested = if (requestType == "Horas trabajadas") 0 else effectiveDays.toInt(),
                                hoursRequested = requestedHours,
                                observations = observations,
                                status = VacationStatus.PENDING
                            ))
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isInvalidHours) ErrorRed else Teal),
                        enabled = effectiveDays > 0 && !isInvalidHours
                    ) {
                        Text("Enviar")
                    }
                }
            }
        }
    }
}

@Composable
fun PayrollView(employees: List<Employee>, requests: List<VacationRequest>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    "Nómina Generada",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
                Text(
                    "Resumen de sueldos, horas extras y deducciones",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        items(employees) { employee ->
            val daysTaken = requests.filter { it.employeeUid == employee.uid && it.status == VacationStatus.APPROVED }
                .sumOf { it.effectiveDays }
            
            PayrollCard(employee, daysTaken)
        }
    }
}

@Composable
fun PayrollCard(employee: Employee, daysTaken: Double) {
    var isExpanded by remember { mutableStateOf(employee.uid == "admin-uid") } // Expanded by default for first user in screenshot

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(TealLight.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        employee.nombreCompleto.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Teal
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(employee.nombreCompleto, fontWeight = FontWeight.Bold, color = Navy)
                    Text(employee.puesto, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "$${String.format("%,.2f", employee.sueldo)}",
                        fontWeight = FontWeight.Bold,
                        color = Teal,
                        fontSize = 18.sp
                    )
                    Text("Total nómina", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))

                PayrollDetailRow("Sueldo base mensual", "$${String.format("%,.2f", employee.sueldo)}")
                PayrollDetailRow("Pago por hora", "$${String.format("%.2f", employee.pagoPorHora)}/h")
                PayrollDetailRow("Horas regulares registradas", "0.0h") // Placeholder
                PayrollDetailRow("Días vacaciones tomados", "${daysTaken.toInt()} días")
                PayrollDetailRow("Días vacaciones disponibles", "${employee.diasVacaciones.toInt()} días")

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TOTAL NÓMINA", fontWeight = FontWeight.ExtraBold, color = Navy, fontSize = 16.sp)
                    Text(
                        "$${String.format("%,.2f", employee.sueldo)}",
                        fontWeight = FontWeight.ExtraBold,
                        color = Teal,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PayrollDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Navy)
    }
}
