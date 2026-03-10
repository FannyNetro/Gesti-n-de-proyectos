package com.vgtech.mobile.ui.screens.rh

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vgtech.mobile.data.model.Employee
import com.vgtech.mobile.ui.theme.*
import com.vgtech.mobile.ui.viewmodel.EmployeeViewModel
import com.vgtech.mobile.ui.viewmodel.RegistrationState

/**
 * EmployeeRegistrationScreen — Full form with validation,
 * role dropdown, and financial fields.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeRegistrationScreen(
    employeeViewModel: EmployeeViewModel
) {
    val registrationState by employeeViewModel.registrationState.collectAsState()

    var nombreCompleto by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var puesto by remember { mutableStateOf("") }
    var sueldo by remember { mutableStateOf("") }
    var diasVacaciones by remember { mutableStateOf("") }

    var puestoExpanded by remember { mutableStateOf(false) }
    val puestos = listOf("Consultor", "Supervisor", "Administrativo", "RH")

    val isLoading = registrationState is RegistrationState.Loading
    val errorMessage = (registrationState as? RegistrationState.Error)?.message
    val isSuccess = registrationState is RegistrationState.Success

    // Reset form on success
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            nombreCompleto = ""
            email = ""
            password = ""
            direccion = ""
            telefono = ""
            puesto = ""
            sueldo = ""
            diasVacaciones = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // ── Header ───────────────────────────────────────────────
        Text(
            "Registro de Empleados",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Navy
        )
        Text(
            "Alta de personal interno — Consultores, Supervisores y Administrativos",
            style = MaterialTheme.typography.bodyMedium,
            color = Mustard,
            fontWeight = FontWeight.SemiBold
        )
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .width(36.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(Mustard)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Success message ──────────────────────────────────────
        if (isSuccess) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SuccessGreen.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✅", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "Empleado registrado exitosamente",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                        Text(
                            "La cuenta fue creada y el perfil guardado internamente",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { employeeViewModel.resetRegistration() }) {
                Text("Registrar otro empleado")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Form Card ────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Card header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(TealLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Nuevo Empleado",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Navy
                        )
                        Text(
                            "Completa todos los campos para registrar",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Error Alert ──────────────────────────────
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = ErrorBg)
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Text("⚠️ ", fontSize = 14.sp)
                            Text(
                                errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = ErrorRed
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ── Name ─────────────────────────────────────
                FormField(
                    value = nombreCompleto,
                    onValueChange = { nombreCompleto = it },
                    label = "NOMBRE COMPLETO",
                    placeholder = "Ej. María González Pérez",
                    icon = Icons.Default.Person,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Email ────────────────────────────────────
                FormField(
                    value = email,
                    onValueChange = { email = it },
                    label = "CORREO ELECTRÓNICO",
                    placeholder = "nombre@vgtech.com",
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Password ─────────────────────────────────
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("CONTRASEÑA", style = MaterialTheme.typography.labelSmall) },
                    placeholder = { Text("Mínimo 6 caracteres") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, null, tint = TextMuted)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = "Toggle",
                                tint = TextMuted
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = formFieldColors()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Address ──────────────────────────────────
                FormField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = "DIRECCIÓN",
                    placeholder = "Ej. Av. Reforma 123, Col. Juárez, CDMX",
                    icon = Icons.Default.LocationOn,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Phone ────────────────────────────────────
                FormField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = "TELÉFONO",
                    placeholder = "Ej. (55) 1234-5678",
                    icon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Role Dropdown ────────────────────────────
                ExposedDropdownMenuBox(
                    expanded = puestoExpanded,
                    onExpandedChange = { if (!isLoading) puestoExpanded = !puestoExpanded }
                ) {
                    OutlinedTextField(
                        value = puesto,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("PUESTO ASIGNADO", style = MaterialTheme.typography.labelSmall) },
                        placeholder = { Text("— Selecciona un puesto —") },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, null, tint = TextMuted)
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = puestoExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading,
                        colors = formFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = puestoExpanded,
                        onDismissRequest = { puestoExpanded = false }
                    ) {
                        puestos.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    puesto = option
                                    puestoExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ── Financial fields row ─────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Sueldo
                    OutlinedTextField(
                        value = sueldo,
                        onValueChange = { sueldo = it },
                        label = { Text("SUELDO (MXN)", style = MaterialTheme.typography.labelSmall) },
                        placeholder = { Text("25000") },
                        leadingIcon = {
                            Text(
                                "$",
                                modifier = Modifier.padding(start = 12.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = formFieldColors()
                    )

                    // Vacaciones
                    OutlinedTextField(
                        value = diasVacaciones,
                        onValueChange = { diasVacaciones = it },
                        label = { Text("VACACIONES", style = MaterialTheme.typography.labelSmall) },
                        placeholder = { Text("Días") },
                        leadingIcon = {
                            Icon(Icons.Default.BeachAccess, null, tint = TextMuted)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = formFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(modifier = Modifier.height(18.dp))

                // ── Buttons ──────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Save
                    Button(
                        onClick = {
                            val employee = Employee(
                                nombreCompleto  = nombreCompleto.trim(),
                                email           = email.trim(),
                                password        = password,
                                direccion       = direccion.trim(),
                                telefono        = telefono.trim(),
                                puesto          = puesto,
                                sueldo          = sueldo.toDoubleOrNull() ?: 0.0,
                                diasVacaciones  = diasVacaciones.toIntOrNull() ?: 0
                            )
                            employeeViewModel.registerEmployee(employee)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Teal,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardando...", fontWeight = FontWeight.Bold)
                        } else {
                            Text("✓  Guardar Empleado", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Clear
                    OutlinedButton(
                        onClick = {
                            nombreCompleto = ""
                            email = ""
                            password = ""
                            direccion = ""
                            telefono = ""
                            puesto = ""
                            sueldo = ""
                            diasVacaciones = ""
                            employeeViewModel.resetRegistration()
                        },
                        modifier = Modifier.height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        Text("Limpiar", fontWeight = FontWeight.SemiBold)
                    }
                }

                // ── Info note ────────────────────────────────
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Teal.copy(alpha = 0.07f)
                    )
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Text("ℹ️ ", fontSize = 14.sp)
                        Text(
                            "La cuenta se crea en la base de datos interna. " +
                                    "El perfil se almacena en memoria automáticamente.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TealDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ── Reusable form field ──────────────────────────────────────────────

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = TextMuted) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        colors = formFieldColors()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Teal,
    unfocusedBorderColor = BorderColor,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = BackgroundLight
)
