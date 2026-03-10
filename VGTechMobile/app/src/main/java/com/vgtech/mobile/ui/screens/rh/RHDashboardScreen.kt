package com.vgtech.mobile.ui.screens.rh

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        BottomNavItem("Empleados", Icons.Filled.Group, Icons.Outlined.Group),
        BottomNavItem("Registrar", Icons.Filled.PersonAdd, Icons.Outlined.PersonAdd),
        BottomNavItem("Perfil", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    )

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
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceWhite,
                tonalElevation = 8.dp
            ) {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                if (selectedTab == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(
                                item.label,
                                fontWeight = if (selectedTab == index) FontWeight.Bold
                                else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Teal,
                            selectedTextColor = Teal,
                            indicatorColor = TealLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> EmployeeListScreen(employeeViewModel = employeeViewModel)
                1 -> EmployeeRegistrationScreen(employeeViewModel = employeeViewModel)
                2 -> ProfileTab(onLogout = onLogout)
            }
        }
    }
}

private data class BottomNavItem(
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)

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
