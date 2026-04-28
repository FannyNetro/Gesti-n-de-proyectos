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
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.vgtech.mobile.data.model.*
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
                    "Gestión de Días y Nómina",
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
        ManageEmployeeDaysDialog(
            employee = employee,
            onDismiss = { selectedEmployeeState.value = null },
            onConfirmRequest = { request ->
                InternalDb.addVacationRequest(request)
                selectedEmployeeState.value = null
            },
            onAdjustBalance = { newBalance ->
                InternalDb.updateEmployee(employee.copy(diasVacaciones = newBalance))
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
                Text("vacaciones disp.", style = MaterialTheme.typography.labelSmall, color = TextMuted)
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
                    Text(request.type.label, style = MaterialTheme.typography.labelSmall, color = Teal, fontWeight = FontWeight.Bold)
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
fun ManageEmployeeDaysDialog(
    employee: Employee,
    onDismiss: () -> Unit,
    onConfirmRequest: (VacationRequest) -> Unit,
    onAdjustBalance: (Double) -> Unit
) {
    var tabIndex by remember { mutableIntStateOf(0) }

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
                Text("Gestión de Días", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Navy)
                
                // Info del empleado
                Column(modifier = Modifier.fillMaxWidth().background(SurfaceGray, RoundedCornerShape(12.dp)).padding(12.dp)) {
                    Text("Empleado: ${employee.nombreCompleto}", fontWeight = FontWeight.Bold, color = Navy)
                    Text("Puesto: ${employee.puesto}", style = MaterialTheme.typography.bodySmall)
                    Text("Vacaciones actuales: ${employee.diasVacaciones} días", fontWeight = FontWeight.ExtraBold, color = Teal)
                }

                TabRow(
                    selectedTabIndex = tabIndex,
                    containerColor = Color.Transparent,
                    contentColor = Teal,
                    indicator = { tabPositions ->
                        SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[tabIndex]), color = Teal)
                    }
                ) {
                    Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }) {
                        Text("Registrar Ausencia", modifier = Modifier.padding(vertical = 12.dp))
                    }
                    Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }) {
                        Text("Ajustar Saldo", modifier = Modifier.padding(vertical = 12.dp))
                    }
                }

                if (tabIndex == 0) {
                    AddRequestContent(employee, onConfirmRequest)
                } else {
                    AdjustBalanceContent(employee, onAdjustBalance)
                }

                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRequestContent(employee: Employee, onConfirm: (VacationRequest) -> Unit) {
    val requests by InternalDb.vacationRequests.collectAsState()
    val approvedRequests = requests.filter { it.employeeUid == employee.uid && it.status == VacationStatus.APPROVED }
    
    val usedConGoce = approvedRequests.filter { it.type == RequestType.CON_GOCE }.sumOf { it.effectiveDays }
    val usedSinGoce = approvedRequests.filter { it.type == RequestType.SIN_GOCE }.sumOf { it.effectiveDays }

    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var observations by remember { mutableStateOf("") }
    var requestType by remember { mutableStateOf(RequestType.VACACIONES) }
    var isHourly by remember { mutableStateOf(false) }
    var hoursInput by remember { mutableStateOf("") }
    var expandedType by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val availableHours = employee.diasVacaciones * 8.0
    val requestedHours = if (isHourly) hoursInput.toDoubleOrNull() ?: 0.0 else 0.0
    
    // Total Limits minus already used
    val currentLimit = when(requestType) {
        RequestType.VACACIONES -> employee.diasVacaciones
        RequestType.CON_GOCE -> (1.0 - usedConGoce).coerceAtLeast(0.0)
        RequestType.SIN_GOCE -> (3.0 - usedSinGoce).coerceAtLeast(0.0)
    }

    val isInvalidHours = requestType == RequestType.VACACIONES && requestedHours > availableHours

    val effectiveDays = remember(startDate, endDate, isHourly, requestedHours) {
        if (isHourly) {
            requestedHours / 8.0
        } else {
            if (endDate < startDate) return@remember 0.0
            val cal = Calendar.getInstance().apply { timeInMillis = startDate }
            val endCal = Calendar.getInstance().apply { timeInMillis = endDate }
            var total = 0.0
            while (!cal.after(endCal)) {
                when (cal.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.SATURDAY -> total += 0.5
                    Calendar.SUNDAY -> { /* Skip */ }
                    else -> total += 1.0
                }
                cal.add(Calendar.DAY_OF_MONTH, 1)
            }
            total
        }
    }

    val saldoRestante = (currentLimit - effectiveDays).coerceAtLeast(0.0)
    val isLimitExceeded = effectiveDays > currentLimit

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Tipo de Solicitud", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        
        ExposedDropdownMenuBox(
            expanded = expandedType,
            onExpandedChange = { expandedType = !expandedType },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = requestType.label,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = expandedType,
                onDismissRequest = { expandedType = false }
            ) {
                RequestType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.label) },
                        onClick = {
                            requestType = type
                            expandedType = false
                        }
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isHourly, onCheckedChange = { isHourly = it })
            Text("¿Solicitar por horas?", style = MaterialTheme.typography.bodyMedium)
        }

        if (isHourly) {
            OutlinedTextField(
                value = hoursInput,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) hoursInput = it },
                label = { Text("Horas") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isInvalidHours,
                supportingText = { if (isInvalidHours) Text("Excede disponible", color = ErrorRed) },
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
                        if (isHourly) endDate = startDate
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
                }.padding(12.dp)) {
                    Text(sdf.format(Date(startDate)))
                }
            }
            if (!isHourly) {
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
            label = { Text("Comentario") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        val errorStatus = isInvalidHours || isLimitExceeded
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = (if (errorStatus) ErrorRed else Teal).copy(alpha = 0.05f),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, (if (errorStatus) ErrorRed else Teal).copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(if (isHourly) "Horas en días:" else "Días solicitados:")
                    Text("$effectiveDays", fontWeight = FontWeight.ExtraBold, color = if (errorStatus) ErrorRed else Teal)
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Días restantes:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text("$saldoRestante", fontWeight = FontWeight.ExtraBold, color = if (saldoRestante > 0 || !isLimitExceeded) Teal else ErrorRed)
                }
                if (isLimitExceeded) {
                    val maxPossible = if (requestType == RequestType.VACACIONES) employee.diasVacaciones else if (requestType == RequestType.CON_GOCE) 1.0 - usedConGoce else 3.0 - usedSinGoce
                    Text("No puedes solicitar más de ${maxPossible.coerceAtLeast(0.0)} días para este tipo de ausencia.", style = MaterialTheme.typography.labelSmall, color = ErrorRed, fontWeight = FontWeight.Bold)
                } else if (requestType != RequestType.VACACIONES) {
                    Text("Nota: Este tipo de ausencia no afecta el saldo acumulado.", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }
        }

        Button(
            onClick = {
                onConfirm(VacationRequest(
                    employeeUid = employee.uid,
                    employeeName = employee.nombreCompleto,
                    startDate = startDate,
                    endDate = if (isHourly) startDate else endDate,
                    daysRequested = if (isHourly) 0.0 else effectiveDays,
                    hoursRequested = requestedHours,
                    type = requestType,
                    observations = observations,
                    status = VacationStatus.PENDING
                ))
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = if (errorStatus) ErrorRed else Teal),
            enabled = effectiveDays > 0 && !errorStatus
        ) {
            Text("Registrar")
        }
    }
}

@Composable
fun AdjustBalanceContent(employee: Employee, onAdjust: (Double) -> Unit) {
    var balanceInput by remember { mutableStateOf(employee.diasVacaciones.toString()) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Modificar saldo de vacaciones directamente.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        
        OutlinedTextField(
            value = balanceInput,
            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) balanceInput = it },
            label = { Text("Nuevo saldo (días)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp)
        )

        Button(
            onClick = { onAdjust(balanceInput.toDoubleOrNull() ?: employee.diasVacaciones) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Navy)
        ) {
            Text("Actualizar Saldo")
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
                    "Resumen de sueldos y ausencias",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        items(employees) { employee ->
            val empRequests = requests.filter { it.employeeUid == employee.uid && it.status == VacationStatus.APPROVED }
            val vacationDays = empRequests.filter { it.type == RequestType.VACACIONES }.sumOf { it.effectiveDays }
            val paidLeaveDays = empRequests.filter { it.type == RequestType.CON_GOCE }.sumOf { it.effectiveDays }
            val unpaidLeaveDays = empRequests.filter { it.type == RequestType.SIN_GOCE }.sumOf { it.effectiveDays }
            
            PayrollCard(employee, vacationDays, paidLeaveDays, unpaidLeaveDays)
        }
    }
}

@Composable
fun PayrollCard(employee: Employee, vacationDays: Double, paidLeave: Double, unpaidLeave: Double) {
    var isExpanded by remember { mutableStateOf(false) }

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
                    val finalPay = employee.sueldo - (unpaidLeave * (employee.sueldo / 30.0))
                    Text(
                        "$${String.format("%,.2f", finalPay)}",
                        fontWeight = FontWeight.Bold,
                        color = Teal,
                        fontSize = 18.sp
                    )
                    Text("Total a pagar", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))

                PayrollDetailRow("Sueldo base mensual", "$${String.format("%,.2f", employee.sueldo)}")
                PayrollDetailRow("Vacaciones tomadas", "$vacationDays días")
                PayrollDetailRow("Días con goce de sueldo", "$paidLeave días")
                PayrollDetailRow("Días sin goce de sueldo", "$unpaidLeave días")
                
                if (unpaidLeave > 0) {
                    val deduction = unpaidLeave * (employee.sueldo / 30.0)
                    PayrollDetailRow("Deducción (días sin goce)", "-$${String.format("%,.2f", deduction)}")
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("TOTAL NÓMINA", fontWeight = FontWeight.ExtraBold, color = Navy, fontSize = 16.sp)
                    val finalPay = employee.sueldo - (unpaidLeave * (employee.sueldo / 30.0))
                    Text(
                        "$${String.format("%,.2f", finalPay)}",
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
