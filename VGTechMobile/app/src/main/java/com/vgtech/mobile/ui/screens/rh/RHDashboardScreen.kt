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
 * Tabs: Empleados (list), Registrar (form), Perfil (logout).
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
        DrawerItem("supervisores", "Supervisores", Icons.Filled.Engineering, Icons.Outlined.Engineering),
        DrawerItem("proveedores", "Proveedores", Icons.Filled.Handshake, Icons.Outlined.Handshake),
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
                    },
                    actions = {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = Teal.copy(alpha = 0.15f)
                        ) {
                            Text(
                                "👤 Admin RH",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Teal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (selectedDrawerRoute) {
                    "empleados" -> EmployeeListScreen(employeeViewModel = employeeViewModel, filterRoles = listOf("RH", "Administrativo", "Empleado"))
                    "supervisores" -> EmployeeListScreen(employeeViewModel = employeeViewModel, filterRoles = listOf("Supervisor"))
                    "proveedores" -> EmployeeListScreen(employeeViewModel = employeeViewModel, filterRoles = listOf("Proveedor"))
                    "consultores" -> EmployeeListScreen(employeeViewModel = employeeViewModel, filterRoles = listOf("Consultor"))
                    "registrar" -> EmployeeRegistrationScreen(employeeViewModel = employeeViewModel)
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

// ── Drawer Header ───────────────────────────────────────────────────

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
        Icon(
            Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = SurfaceWhite
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "VG Tech",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = SurfaceWhite
        )
        Text(
            "Recursos Humanos",
            style = MaterialTheme.typography.bodyMedium,
            color = SurfaceWhite.copy(alpha = 0.8f)
        )
    }
}

// ── Profile / Logout Tab ─────────────────────────────────────────────

@Composable
private fun ProfileTab(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = TextMuted
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Administrador de RH",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Navy
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Gestión de personal y altas de empleados",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(
            onClick = onLogout,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = ErrorRed
            )
        ) {
            Text("🔓  Cerrar Sesión", fontWeight = FontWeight.SemiBold)
        }
    }
}
