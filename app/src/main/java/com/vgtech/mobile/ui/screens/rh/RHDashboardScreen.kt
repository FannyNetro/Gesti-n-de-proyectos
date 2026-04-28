package com.vgtech.mobile.ui.screens.rh

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.vgtech.mobile.ui.theme.*
import com.vgtech.mobile.ui.viewmodel.EmployeeViewModel

/**
 * RHDashboardScreen — Scaffold with BottomNavigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RHDashboardScreen(
    onLogout: () -> Unit,
    employeeViewModel: EmployeeViewModel = viewModel()
) {
    var selectedDrawerRoute by remember { mutableStateOf("empleados") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val drawerItems = listOf(
        DrawerItem("empleados", "Empleados", Icons.Filled.Group, Icons.Outlined.Group),
        DrawerItem("vacaciones", "Vacaciones", Icons.Filled.BeachAccess, Icons.Outlined.BeachAccess),
        DrawerItem("sueldos", "Sueldos y Pagos", Icons.Filled.Payments, Icons.Outlined.Payments),
        DrawerItem("supervisores", "Supervisores", Icons.Filled.Engineering, Icons.Outlined.Engineering),
        DrawerItem("proveedores", "Directorio Prov.", Icons.Filled.Handshake, Icons.Outlined.Handshake),
        DrawerItem("cuentas_proveedores", "Cuentas por Pagar", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
        DrawerItem("consultores", "Consultores", Icons.Filled.BusinessCenter, Icons.Outlined.BusinessCenter),
        DrawerItem("registrar", "Registrar", Icons.Filled.PersonAdd, Icons.Outlined.PersonAdd),
        DrawerItem("perfil", "Mi Perfil", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                Spacer(modifier = Modifier.height(8.dp))
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                if (selectedDrawerRoute == item.route) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = if (selectedDrawerRoute == item.route) Teal else TextMuted
                            )
                        },
                        label = {
                            Text(
                                item.label,
                                fontWeight = if (selectedDrawerRoute == item.route) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedDrawerRoute == item.route) Teal else TextPrimary
                            )
                        },
                        selected = selectedDrawerRoute == item.route,
                        onClick = {
                            selectedDrawerRoute = item.route
                            coroutineScope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = TealLight.copy(alpha = 0.5f),
                            unselectedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column {
                            Text(
                                "VG Tech",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = SurfaceWhite
                            )
                            Text(
                                "Recursos Humanos",
                                style = MaterialTheme.typography.labelSmall,
                                color = SurfaceWhite.copy(alpha = 0.6f)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Navy
                    ),
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = SurfaceWhite)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (selectedDrawerRoute) {
                    "empleados" -> EmployeeListScreen(employeeViewModel = employeeViewModel, filterRoles = listOf("RH", "Administrativo", "Empleado"))
                    "vacaciones" -> VacationHistoryScreen()
                    "sueldos" -> SalaryControlScreen()
                    "supervisores" -> EmployeeListScreen(employeeViewModel = employeeViewModel, filterRoles = listOf("Supervisor"))
                    "proveedores" -> EmployeeListScreen(employeeViewModel = employeeViewModel, filterRoles = listOf("Proveedor"))
                    "cuentas_proveedores" -> ProviderPayableScreen(canManagePhases = false, canRegisterPayments = true)
                    "consultores" -> EmployeeListScreen(employeeViewModel = employeeViewModel, filterRoles = listOf("Consultor"))
                    "registrar" -> EmployeeRegistrationScreen(employeeViewModel = employeeViewModel, onBack = { selectedDrawerRoute = "empleados" })
                    "perfil" -> ProfileTab(onLogout = onLogout)
                }
            }
        }
    }
}

private data class DrawerItem(
    val route: String,
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
private fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Navy, NavyLight)
                )
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(64.dp), tint = SurfaceWhite)
        Spacer(modifier = Modifier.height(16.dp))
        Text("VG Tech", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = SurfaceWhite)
        Text("Recursos Humanos", style = MaterialTheme.typography.bodyMedium, color = SurfaceWhite.copy(alpha = 0.8f))
    }
}

@Composable
private fun ProfileTab(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(80.dp), tint = TextMuted)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Administrador de RH", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Navy)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(onClick = onLogout, colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)) {
            Text("🔓  Cerrar Sesión", fontWeight = FontWeight.SemiBold)
        }
    }
}
