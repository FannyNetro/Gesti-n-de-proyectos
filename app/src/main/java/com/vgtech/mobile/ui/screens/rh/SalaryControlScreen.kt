package com.vgtech.mobile.ui.screens.rh

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vgtech.mobile.data.local.InternalDb
import com.vgtech.mobile.data.model.Employee
import com.vgtech.mobile.data.model.WorkLog
import com.vgtech.mobile.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SalaryControlScreen() {
    val employees by InternalDb.employees.collectAsState()
    val workLogs by InternalDb.workLogs.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedEmployee by remember { mutableStateOf<Employee?>(null) }
    var showWorkLogs by remember { mutableStateOf(false) }

    val filteredEmployees = remember(searchQuery, employees) {
        employees.filter { 
            it.activo && it.puesto != "Proveedor" &&
            (searchQuery.isBlank() || it.nombreCompleto.contains(searchQuery, ignoreCase = true))
        }
    }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

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
                        "Sueldos y Pagos",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showWorkLogs = !showWorkLogs }) {
                        Icon(
                            if (showWorkLogs) Icons.Default.People else Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = "Ver Pagos",
                            tint = Color.White
                        )
                    }
                }
                
                if (!showWorkLogs) {
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

        if (showWorkLogs) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Text("Historial de Horas Trabajadas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Navy) }
                items(workLogs.sortedByDescending { it.date }) { log ->
                    WorkLogItem(log, currencyFormatter)
                }
                if (workLogs.isEmpty()) {
                    item { Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) { Text("No hay registros de horas.", color = TextMuted) } }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Text("Gestión de Salarios y Tarifas", style = MaterialTheme.typography.titleSmall, color = TextMuted) }
                items(filteredEmployees) { emp ->
                    SalaryEmployeeRow(emp, currencyFormatter) { selectedEmployee = emp }
                }
            }
        }
    }

    if (selectedEmployee != null) {
        ManageSalaryDialog(
            employee = selectedEmployee!!,
            onDismiss = { selectedEmployee = null },
            onConfirmAddHours = { hours, rate, obs ->
                InternalDb.addWorkLog(WorkLog(
                    employeeUid = selectedEmployee!!.uid,
                    employeeName = selectedEmployee!!.nombreCompleto,
                    hoursWorked = hours,
                    hourlyRateAtTime = rate,
                    observations = obs
                ))
                selectedEmployee = null
            },
            onUpdateRates = { sueldo, hourly ->
                InternalDb.updateEmployeeRates(selectedEmployee!!.uid, sueldo, hourly)
                selectedEmployee = null
            }
        )
    }
}

@Composable
fun SalaryEmployeeRow(employee: Employee, formatter: NumberFormat, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(12.dp), color = TealLight.copy(alpha = 0.2f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = Teal)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(employee.nombreCompleto, fontWeight = FontWeight.Bold, color = Navy)
                Text(employee.puesto, style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatter.format(employee.sueldo), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Navy)
                Text("${formatter.format(employee.pagoPorHora)} / h", style = MaterialTheme.typography.labelSmall, color = Teal, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun WorkLogItem(log: WorkLog, formatter: NumberFormat) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(log.employeeName, fontWeight = FontWeight.Bold, color = Navy)
                Text(sdf.format(Date(log.date)), style = MaterialTheme.typography.labelSmall, color = TextMuted)
                if (log.observations.isNotEmpty()) {
                    Text(log.observations, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${log.hoursWorked} hrs", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Navy)
                Text(formatter.format(log.totalPay), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = Teal)
            }
        }
    }
}

@Composable
fun ManageSalaryDialog(
    employee: Employee,
    onDismiss: () -> Unit,
    onConfirmAddHours: (Double, Double, String) -> Unit,
    onUpdateRates: (Double, Double) -> Unit
) {
    var tabIndex by remember { mutableIntStateOf(0) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                Text(employee.nombreCompleto, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Navy)
                Text(employee.puesto, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                
                TabRow(
                    selectedTabIndex = tabIndex,
                    containerColor = Color.Transparent,
                    contentColor = Teal,
                    indicator = { tabPositions ->
                        SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[tabIndex]), color = Teal)
                    },
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }) {
                        Text("Registrar Horas", modifier = Modifier.padding(8.dp))
                    }
                    Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }) {
                        Text("Ajustar Sueldo", modifier = Modifier.padding(8.dp))
                    }
                }

                if (tabIndex == 0) {
                    AddHoursContent(employee, onConfirmAddHours)
                } else {
                    UpdateSalaryContent(employee, onUpdateRates)
                }
                
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Cerrar", color = Navy)
                }
            }
        }
    }
}

@Composable
fun AddHoursContent(employee: Employee, onConfirm: (Double, Double, String) -> Unit) {
    var hours by remember { mutableStateOf("") }
    var obs by remember { mutableStateOf("") }
    val hourlyRate = employee.pagoPorHora
    val total = (hours.toDoubleOrNull() ?: 0.0) * hourlyRate

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Tarifa actual: ${NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(hourlyRate)} / hora", fontWeight = FontWeight.Bold, color = Teal)
        
        OutlinedTextField(
            value = hours,
            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) hours = it },
            label = { Text("Horas trabajadas") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )

        OutlinedTextField(
            value = obs,
            onValueChange = { obs = it },
            label = { Text("Observaciones (Ej. Proyecto X)") },
            modifier = Modifier.fillMaxWidth()
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = TealLight.copy(alpha = 0.2f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total a pagar:", fontWeight = FontWeight.Bold)
                Text(NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(total), fontWeight = FontWeight.ExtraBold, color = Teal, style = MaterialTheme.typography.titleMedium)
            }
        }

        Button(
            onClick = { onConfirm(hours.toDoubleOrNull() ?: 0.0, hourlyRate, obs) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Teal),
            enabled = hours.isNotEmpty() && (hours.toDoubleOrNull() ?: 0.0) > 0
        ) {
            Text("Registrar Pago por Horas")
        }
    }
}

@Composable
fun UpdateSalaryContent(employee: Employee, onConfirm: (Double, Double) -> Unit) {
    var sueldo by remember { mutableStateOf(employee.sueldo.toString()) }
    var hourly by remember { mutableStateOf(employee.pagoPorHora.toString()) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = sueldo,
            onValueChange = { sueldo = it },
            label = { Text("Sueldo Mensual Base") },
            modifier = Modifier.fillMaxWidth(),
            prefix = { Text("$ ") }
        )

        OutlinedTextField(
            value = hourly,
            onValueChange = { hourly = it },
            label = { Text("Pago por Hora") },
            modifier = Modifier.fillMaxWidth(),
            prefix = { Text("$ ") }
        )

        Button(
            onClick = { onConfirm(sueldo.toDoubleOrNull() ?: 0.0, hourly.toDoubleOrNull() ?: 0.0) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Navy)
        ) {
            Text("Actualizar Tarifas")
        }
    }
}
