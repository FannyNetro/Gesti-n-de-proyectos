package com.vgtech.mobile.ui.screens.rh

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vgtech.mobile.data.local.InternalDb
import com.vgtech.mobile.data.model.Employee
import com.vgtech.mobile.data.model.VacationRequest
import com.vgtech.mobile.data.model.VacationStatus
import com.vgtech.mobile.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun VacationHistoryScreen() {
    val employees by InternalDb.employees.collectAsState()
    val requests by InternalDb.vacationRequests.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    val selectedEmployeeState = remember { mutableStateOf<Employee?>(null) }
    var showHistory by remember { mutableStateOf(false) }

    val filteredEmployees = remember(searchQuery, employees) {
        employees.filter { 
            it.activo && 
            it.puesto != "Proveedor" && 
            (searchQuery.isBlank() || it.nombreCompleto.contains(searchQuery, ignoreCase = true))
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(SurfaceWhite)) {
        // --- Header ---
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
                        "Vacaciones",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showHistory = !showHistory }) {
                        Icon(
                            if (showHistory) Icons.Default.People else Icons.Default.History,
                            contentDescription = "Alternar Vista",
                            tint = Color.White
                        )
                    }
                }
                
                if (!showHistory) {
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

        if (showHistory) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Text("Historial de Solicitudes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Navy) }
                items(requests.sortedByDescending { it.requestDate }) { request ->
                    VacationRequestItem(request)
                }
                if (requests.isEmpty()) {
                    item {
                        Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay solicitudes registradas.", color = TextMuted)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Text("Selecciona un empleado para registrar vacaciones", style = MaterialTheme.typography.titleSmall, color = TextMuted) }
                items(filteredEmployees) { emp ->
                    EmployeeVacationRow(emp) { selectedEmployeeState.value = emp }
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
    }

    selectedEmployeeState.value?.let { employee ->
        AddVacationDialog(
            employee = employee,
            onDismiss = { selectedEmployeeState.value = null },
            onConfirm = { request ->
                InternalDb.addVacationRequest(request)
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
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}

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
                Text("Total: ${request.daysRequested} días", fontWeight = FontWeight.Bold, color = Teal)
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(request.status.name, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVacationDialog(employee: Employee, onDismiss: () -> Unit, onConfirm: (VacationRequest) -> Unit) {
    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableLongStateOf(System.currentTimeMillis() + 86400000L) }
    
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Cálculo automático de días
    val diff = endDate - startDate
    val daysRequested = if (diff >= 0) (TimeUnit.MILLISECONDS.toDays(diff).toInt() + 1) else 0

    // Datos del saldo
    val pendActual = employee.diasVacaciones
    val pendAnteriores = 0 
    val totalDispPend = pendActual + pendAnteriores
    val finalDisponibles = (totalDispPend - daysRequested).coerceAtLeast(0)
    
    val canRegister = daysRequested in 1..totalDispPend

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
                modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Fecha de solicitud:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(sdf.format(Date()), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }

                Text("Solicitud de Vacaciones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Navy, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    FormLabelValue("Nombre:", employee.nombreCompleto.uppercase())
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) { FormLabelValue("Departamento:", employee.puesto.uppercase()) }
                        Box(modifier = Modifier.weight(0.5f)) { FormLabelValue("Num Empleado:", employee.uid.take(6).uppercase()) }
                    }
                }

                HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.5f))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Fecha inició de Vacaciones:", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(180.dp))
                        DatePickerBox(sdf.format(Date(startDate))) { showDatePicker(context, startDate) { startDate = it } }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Fecha termino de Vacaciones:", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(180.dp))
                        DatePickerBox(sdf.format(Date(endDate))) { showDatePicker(context, endDate) { endDate = it } }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Box(
                            modifier = Modifier
                                .border(1.5.dp, ErrorRed, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Total días", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("$daysRequested", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = ErrorRed)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CalculationLine("Días de Vacaciones pendientes de disfrutar del periodo actual:", "$pendActual")
                    CalculationLine("Días de Vacaciones pendientes de disfrutar de periodos anteriores:", "$pendAnteriores")
                    CalculationLine("Total de Vacaciones disponibles pendientes de disfrutar:", "$totalDispPend", isBold = true, isGray = true)
                    CalculationLine("Días de Vacaciones que solicita:", "$daysRequested", isGray = true)
                    CalculationLine("Días de Vacaciones disponibles:", "$finalDisponibles", isBold = true, isGray = true, textColor = Teal)
                }

                if (daysRequested > totalDispPend) {
                    Text("⚠️ Saldo insuficiente.", color = ErrorRed, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar", color = Navy, fontWeight = FontWeight.Bold) }
                    Button(
                        onClick = {
                            onConfirm(VacationRequest(
                                employeeUid = employee.uid,
                                employeeName = employee.nombreCompleto,
                                startDate = startDate,
                                endDate = endDate,
                                daysRequested = daysRequested
                            ))
                        },
                        enabled = canRegister,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Teal),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Registrar")
                    }
                }
            }
        }
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
            .width(150.dp)
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
