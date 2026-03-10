package com.vgtech.mobile.ui.screens.proveedor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProveedorDashboardScreen(onLogout: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Mis Proyectos", "Nueva Cotización", "Evaluaciones")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Proveedor") },
                actions = {
                    TextButton(onClick = onLogout) { Text("Salir", color = MaterialTheme.colorScheme.error) }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> ProyectosActivosView()
                1 -> FormularioCotizacionView()
                2 -> HistorialEvaluacionesView()
            }
        }
    }
}

@Composable
fun ProyectosActivosView() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Proyectos Activos", style = MaterialTheme.typography.titleLarge)
        Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Edificio Reforma 222", style = MaterialTheme.typography.titleMedium)
                Text("Avance Actual: 45%")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Abrir modal reporte avance */ }) {
                    Text("Reportar Avance")
                }
            }
        }
    }
}

@Composable
fun FormularioCotizacionView() {
    var costo by remember { mutableStateOf("") }
    var tiempo by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Cargar Nueva Cotización", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = costo, onValueChange = { costo = it }, label = { Text("Costo Propuesto ($)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = tiempo, onValueChange = { tiempo = it }, label = { Text("Tiempo Estimado (Días)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { /* Lógica para elegir PDF y FirebaseStorage */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Adjuntar Archivo (PDF)")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* save to Firestore */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Enviar Cotización")
        }
    }
}

@Composable
fun HistorialEvaluacionesView() {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item { Text("Evaluaciones Previas", style = MaterialTheme.typography.titleLarge) }
        items(3) { 
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Proyecto: Instalación Eléctrica Fase 1", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text("Estado: Finalizado")
                    Text("Retroalimentación: Excelente cumplimiento de tiempos.", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
