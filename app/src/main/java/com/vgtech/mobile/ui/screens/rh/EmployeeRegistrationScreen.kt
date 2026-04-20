package com.vgtech.mobile.ui.screens.rh

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vgtech.mobile.data.model.Employee
import com.vgtech.mobile.ui.theme.DarkBlue
import com.vgtech.mobile.ui.theme.Teal
import com.vgtech.mobile.ui.viewmodel.EmployeeViewModel
import com.vgtech.mobile.ui.viewmodel.RegistrationState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EmployeeRegistrationScreen(
    employeeViewModel: EmployeeViewModel,
    onBack: () -> Unit
) {
    var nombreCompleto by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var puesto by remember { mutableStateOf("Consultor") }
    var sueldo by remember { mutableStateOf("") }
    var diasVacaciones by remember { mutableStateOf("") }
    val tipoTrabajo = remember { mutableStateListOf<String>() }

    val isLoading by employeeViewModel.isLoading.collectAsState()
    val registrationState by employeeViewModel.registrationState.collectAsState()

    val puestos = listOf("Consultor", "Supervisor", "Administrativo", "RH", "Proveedor", "Cliente")
    var expandedPuesto by remember { mutableStateOf(false) }

    LaunchedEffect(registrationState) {
        if (registrationState is RegistrationState.Success) {
            onBack()
            employeeViewModel.resetRegistration()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Empleado", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Información Personal",
                style = MaterialTheme.typography.titleMedium,
                color = DarkBlue,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = nombreCompleto,
                onValueChange = { nombreCompleto = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Teal) },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Teal) },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Teal) },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = Teal) },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Teal) },
                shape = RoundedCornerShape(12.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                "Detalles Laborales",
                style = MaterialTheme.typography.titleMedium,
                color = DarkBlue,
                fontWeight = FontWeight.Bold
            )

            // Puesto Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedPuesto,
                onExpandedChange = { expandedPuesto = !expandedPuesto },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = puesto,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Puesto") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPuesto) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Work, contentDescription = null, tint = Teal) },
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedPuesto,
                    onDismissRequest = { expandedPuesto = false }
                ) {
                    puestos.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                puesto = selectionOption
                                expandedPuesto = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = sueldo,
                onValueChange = { sueldo = it },
                label = { Text("Sueldo Mensual") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Teal) },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = diasVacaciones,
                onValueChange = { diasVacaciones = it },
                label = { Text("Días de Vacaciones") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Teal) },
                shape = RoundedCornerShape(12.dp)
            )

            if (puesto == "Proveedor") {
                Text("Especialidades", fontWeight = FontWeight.SemiBold, color = DarkBlue)
                val specialties = listOf("Instalaciones", "Obra Civil", "Eléctrico", "Acabados", "Materiales")
                androidx.compose.foundation.layout.FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    specialties.forEach { specialty ->
                        FilterChip(
                            selected = tipoTrabajo.contains(specialty),
                            onClick = {
                                if (tipoTrabajo.contains(specialty)) tipoTrabajo.remove(specialty)
                                else tipoTrabajo.add(specialty)
                            },
                            label = { Text(text = specialty) }
                        )
                    }
                }
            }

            if (registrationState is RegistrationState.Error) {
                Text(
                    text = (registrationState as RegistrationState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                            diasVacaciones  = diasVacaciones.toDoubleOrNull() ?: 0.0,
                            tipoTrabajo     = tipoTrabajo.toList()
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
                    if (isLoading || registrationState is RegistrationState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Registrar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
