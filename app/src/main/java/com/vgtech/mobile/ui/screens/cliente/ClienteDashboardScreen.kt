package com.vgtech.mobile.ui.screens.cliente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteDashboardScreen(onLogout: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Inicio / VG Tech", "Trámites y Cotizaciones")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Portal de Clientes") },
                actions = { TextButton(onClick = onLogout) { Text("Salir", color = MaterialTheme.colorScheme.error) } }
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
                0 -> LandingPageView()
                1 -> BandejaCotizacionesView()
            }
        }
    }
}

@Composable
fun LandingPageView() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("VG Tech - Construcción y Gestión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Líderes en supervisión y ejecución de obras civiles e industriales. Ofrecemos consultoría especializada garantizando calidad y reducción de costos operativos en un 20%.")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Nuestros Servicios Profesionales:", style = MaterialTheme.typography.titleMedium)
        Text("• Supervisión de Obra")
        Text("• Consultoría de Ingeniería")
        Text("• Evaluaciones y Cotizaciones")
    }
}

@Composable
fun BandejaCotizacionesView() {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item { Text("Cotizaciones por Autorizar", style = MaterialTheme.typography.titleLarge) }
        items(2) { index ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Cotización: Materiales Eléctricos", style = MaterialTheme.typography.titleMedium)
                    Text("Costo Propuesto: $45,000 MXN")
                    Text("Tiempo Estimado: 14 Días")
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { /* Firestore Update Autorizada */ }, modifier = Modifier.weight(1f)) {
                            Text("Autorizar")
                        }
                        OutlinedButton(onClick = { /* Firestore Update Rechazada */ }, modifier = Modifier.weight(1f)) {
                            Text("Rechazar")
                        }
                    }
                }
            }
        }
    }
}
