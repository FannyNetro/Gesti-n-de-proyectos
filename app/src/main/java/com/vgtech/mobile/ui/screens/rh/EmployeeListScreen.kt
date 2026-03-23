package com.vgtech.mobile.ui.screens.rh

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vgtech.mobile.data.model.Employee
import com.vgtech.mobile.ui.theme.*
import com.vgtech.mobile.ui.viewmodel.EmployeeViewModel

/**
 * EmployeeListScreen — LazyColumn of employee cards with
 * password toggle, edit, and fire actions.
 */
@Composable
fun EmployeeListScreen(
    employeeViewModel: EmployeeViewModel,
    filterRoles: List<String>? = null
) {
    val allEmployees by employeeViewModel.employees.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    val employees = remember(allEmployees, filterRoles, searchQuery, selectedCategory) {
        var filtered = if (filterRoles == null) {
            allEmployees
        } else {
            allEmployees.filter { it.puesto in filterRoles }
        }
        
        if (selectedCategory != null) {
            filtered = filtered.filter { it.tipoTrabajo.contains(selectedCategory) || (selectedCategory == "General" && it.tipoTrabajo.isEmpty()) }
        }
        
        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter { 
                it.nombreCompleto.contains(searchQuery, ignoreCase = true) || 
                it.email.contains(searchQuery, ignoreCase = true) 
            }
        }
        
        filtered
    }
    
    val groupedProviders = remember(employees, filterRoles) {
        if (filterRoles?.contains("Proveedor") == true) {
            val map = mutableMapOf<String, MutableList<Employee>>()
            employees.forEach { emp ->
                if (emp.tipoTrabajo.isEmpty()) {
                    map.getOrPut("General", { mutableListOf() }).add(emp)
                } else {
                    emp.tipoTrabajo.forEach { cat ->
                        map.getOrPut(cat, { mutableListOf() }).add(emp)
                    }
                }
            }
            map
        } else null
    }

    val isLoading by employeeViewModel.isLoading.collectAsState()
    val error by employeeViewModel.listError.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Header Stats ─────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = "👥",
                label = "Total Empleados",
                value = employees.size.toString(),
                accentColor = Teal
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = "📂",
                label = "Proyectos Activos",
                value = "—",
                accentColor = Mustard
            )
        }

        // ── Section title ────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Empleados Registrados",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy,
                    modifier = Modifier.weight(1f)
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Teal,
                        strokeWidth = 2.dp
                    )
                }
            }
            
            // Mustard accent bar moved under the title
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Mustard)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ── Search Bar ───────────────────────────────────────────
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar por nombre o correo...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            shape = RoundedCornerShape(50),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Teal,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFF1F5F9) // Slight light-gray for contrast
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Category Filters (Proveedores solo) ──────────────────
        if (filterRoles?.contains("Proveedor") == true) {
            val allCategories = remember(allEmployees) {
                val cats = mutableSetOf<String>()
                allEmployees.filter { it.puesto == "Proveedor" }.forEach { emp ->
                    if (emp.tipoTrabajo.isEmpty()) cats.add("General")
                    else cats.addAll(emp.tipoTrabajo)
                }
                cats.toList().sorted()
            }
            
            // Un panel contenedor con fondo gris que parece una "barra de tabs"
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE2E8F0)) // Un slate un poco más oscuro para que resalten las pestañas blancas
                    .padding(4.dp), // Espaciado interno de la barra
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    val isSelected = selectedCategory == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { selectedCategory = null }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Todos",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (isSelected) Navy else TextMuted
                        )
                    }
                }
                items(allCategories) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (isSelected) Navy else TextMuted
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // ── Error ────────────────────────────────────────────────
        if (error != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = ErrorBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Text("⚠️ ", fontSize = 14.sp)
                    Text(error ?: "", style = MaterialTheme.typography.bodySmall, color = ErrorRed)
                }
            }
        }

        // ── Employee List ────────────────────────────────────────
        if (employees.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No hay empleados registrados",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextMuted
                    )
                    Text(
                        "Usa la pestaña \"Registrar\" para dar de alta",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // If filtering by Proveedor, group by category
                if (groupedProviders != null) {
                    groupedProviders.forEach { (category, providers) ->
                        item(key = "header_$category") {
                            Text(
                                "Categoria: $category",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Navy,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(providers, key = { it.uid + "_$category" }) { employee ->
                            EmployeeCard(
                                employee = employee,
                                onDeactivate = { motivo -> employeeViewModel.deactivateEmployee(employee.uid, motivo) },
                                onEdit = { updatedEmp -> employeeViewModel.updateEmployee(updatedEmp) },
                                onResetPassword = { uid, newPass -> employeeViewModel.resetEmployeePassword(uid, newPass) }
                            )
                        }
                    }
                } else {
                    // Normal un-grouped list
                    items(employees, key = { it.uid }) { employee ->
                        EmployeeCard(
                            employee = employee,
                            onDeactivate = { motivo -> employeeViewModel.deactivateEmployee(employee.uid, motivo) },
                            onEdit = { updatedEmp -> employeeViewModel.updateEmployee(updatedEmp) },
                            onResetPassword = { uid, newPass -> employeeViewModel.resetEmployeePassword(uid, newPass) }
                        )
                    }
                }
                
                // Bottom spacer for BottomNav/Drawer
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

// ── Stat Card ────────────────────────────────────────────────────────

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    label: String,
    value: String,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Navy
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}

// ── Employee Card ────────────────────────────────────────────────────

@Composable
private fun EmployeeCard(
    employee: Employee,
    onDeactivate: (String) -> Unit,
    onEdit: (Employee) -> Unit,
    onResetPassword: (String, String) -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showConfirmResetDialog by remember { mutableStateOf(false) }
    var newGeneratedPassword by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Top row: Avatar + name + role badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(TealLight),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = employee.nombreCompleto.split(" ").take(2).mapNotNull { it.firstOrNull() }.joinToString("").uppercase()
                    Text(
                        text = initials,
                        color = TealDark,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        employee.nombreCompleto,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy
                    )
                    Text(
                        employee.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                PuestoBadge(puesto = employee.puesto)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Info rows
            InfoRow("📍", "Dirección", employee.direccion)
            Spacer(modifier = Modifier.height(6.dp))
            InfoRow("📞", "Teléfono", employee.telefono)
            Spacer(modifier = Modifier.height(6.dp))
            InfoRow("💰", "Sueldo", "$${String.format("%,.2f", employee.sueldo)} MXN")
            Spacer(modifier = Modifier.height(6.dp))
            InfoRow("🏖️", "Vacaciones", "${employee.diasVacaciones} días")

            if (employee.puesto.lowercase() == "proveedor" && employee.tipoTrabajo.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                InfoRow("🛠️", "Tipos de Trabajo", employee.tipoTrabajo.joinToString(", "))
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (employee.activo) {
                    Text("✅", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Estado: ", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    Text("Activo", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SuccessGreen)
                } else {
                    Text("⛔", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Estado: ", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    Text("Inactivo (${employee.motivoInactivo.ifEmpty { "Sin motivo" }})", 
                         style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = ErrorRed)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { showConfirmResetDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MustardDark)
                ) {
                    Icon(
                        Icons.Default.VpnKey,
                        contentDescription = "Restablecer contraseña",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clave", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                TextButton(
                    onClick = { showEditDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = Teal)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                
                if (employee.activo) {
                    TextButton(
                        onClick = { showConfirmDialog = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed)
                    ) {
                        Icon(
                            Icons.Default.PersonRemove,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Baja", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    // Confirm deactivation dialog
    if (showConfirmDialog) {
        var motivo by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            icon = { Text("⚠️", fontSize = 28.sp) },
            title = { Text("¿Despedir Empleado?") },
            text = {
                Column {
                    Text("¿Estás seguro de querer despedir a ${employee.nombreCompleto}? " +
                            "Esta acción desactivará su cuenta.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = motivo,
                        onValueChange = { motivo = it },
                        label = { Text("Motivo del despido / inactividad") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeactivate(motivo)
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Sí, Despedir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Confirm password reset dialog
    if (showConfirmResetDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmResetDialog = false },
            icon = { Text("🔑", fontSize = 28.sp) },
            title = { Text("Restablecer Contraseña") },
            text = { Text("¿Deseas generar una nueva contraseña para ${employee.nombreCompleto}? La antigua dejará de funcionar.") },
            confirmButton = {
                Button(
                    onClick = {
                        val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
                        val newPass = (1..8).map { chars.random() }.joinToString("")
                        onResetPassword(employee.uid, newPass)
                        newGeneratedPassword = newPass
                        showConfirmResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Navy)
                ) {
                    Text("Sí, Generar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmResetDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Show generated password dialog
    if (newGeneratedPassword != null) {
        AlertDialog(
            onDismissRequest = { newGeneratedPassword = null },
            icon = { Text("✅", fontSize = 28.sp) },
            title = { Text("Contraseña Actualizada") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("La nueva contraseña provisional es:")
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = Navy.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = newGeneratedPassword!!,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = Navy,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Por favor compártela de forma segura con el empleado.", color = TextMuted, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(onClick = { newGeneratedPassword = null }) {
                    Text("Entendido")
                }
            }
        )
    }

    if (showEditDialog) {
        EmployeeEditDialog(
            employee = employee,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                onEdit(updated)
                showEditDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmployeeEditDialog(
    employee: Employee,
    onDismiss: () -> Unit,
    onSave: (Employee) -> Unit
) {
    var nombreCompleto by remember { mutableStateOf(employee.nombreCompleto) }
    var telefono by remember { mutableStateOf(employee.telefono) }
    var direccion by remember { mutableStateOf(employee.direccion) }
    var sueldo by remember { mutableStateOf(employee.sueldo.toString()) }
    var activo by remember { mutableStateOf(employee.activo) }
    var motivoInactivo by remember { mutableStateOf(employee.motivoInactivo) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Empleado") },
        text = {
            Column(modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                OutlinedTextField(value = nombreCompleto, onValueChange = { nombreCompleto = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
                
                if (employee.puesto.lowercase() != "proveedor") {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = sueldo, onValueChange = { sueldo = it }, label = { Text("Sueldo") }, modifier = Modifier.fillMaxWidth())
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = activo, onCheckedChange = { activo = it })
                    Text("Empleado Activo", style = MaterialTheme.typography.bodyMedium)
                }
                
                if (!activo) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = motivoInactivo, onValueChange = { motivoInactivo = it }, label = { Text("Motivo de inactividad") }, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(employee.copy(
                    nombreCompleto = nombreCompleto.trim(),
                    telefono = telefono.trim(),
                    direccion = direccion.trim(),
                    sueldo = sueldo.toDoubleOrNull() ?: employee.sueldo,
                    activo = activo,
                    motivoInactivo = if (!activo) motivoInactivo else ""
                ))
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun PuestoBadge(puesto: String) {
    val (bgColor, textColor) = when (puesto.lowercase()) {
        "rh", "administrativo" -> Pair(Teal.copy(alpha = 0.12f), TealDark)
        "supervisor"           -> Pair(Mustard.copy(alpha = 0.15f), MustardDark)
        "consultor"            -> Pair(Navy.copy(alpha = 0.08f), Navy)
        else                   -> Pair(BackgroundLight, TextMuted)
    }
    Surface(
        shape = RoundedCornerShape(99.dp),
        color = bgColor
    ) {
        Text(
            text = puesto,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun InfoRow(icon: String, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(icon, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}
