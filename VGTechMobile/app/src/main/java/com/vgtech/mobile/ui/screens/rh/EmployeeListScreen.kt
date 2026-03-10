package com.vgtech.mobile.ui.screens.rh

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
    employeeViewModel: EmployeeViewModel
) {
    val employees by employeeViewModel.employees.collectAsState()
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Empleados Registrados",
                style = MaterialTheme.typography.titleMedium,
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

        // Mustard accent bar
        Box(
            modifier = Modifier
                .padding(start = 20.dp, top = 8.dp, bottom = 12.dp)
                .width(36.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(Mustard)
        )

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
                items(employees, key = { it.uid }) { employee ->
                    EmployeeCard(
                        employee = employee,
                        onDeactivate = { employeeViewModel.deactivateEmployee(employee.uid) }
                    )
                }
                // Bottom spacer for BottomNav
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
    onDeactivate: () -> Unit
) {
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Top row: name + role badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

            // Password row with toggle
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔑", fontSize = 13.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Contraseña: ",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Text(
                    if (showPassword) employee.password else "••••••••",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (showPassword) Navy else TextMuted
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = { showPassword = !showPassword },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        if (showPassword) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        contentDescription = "Toggle password",
                        modifier = Modifier.size(16.dp),
                        tint = TextMuted
                    )
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
                    onClick = { showConfirmDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed)
                ) {
                    Icon(
                        Icons.Default.PersonRemove,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Despedir", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
    }

    // Confirm deactivation dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            icon = { Text("⚠️", fontSize = 28.sp) },
            title = { Text("¿Despedir Empleado?") },
            text = {
                Text("¿Estás seguro de querer despedir a ${employee.nombreCompleto}? " +
                        "Esta acción desactivará su cuenta.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeactivate()
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
