package com.vgtech.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vgtech.mobile.data.model.UserRole
import com.vgtech.mobile.ui.screens.auth.LoginScreen
import com.vgtech.mobile.ui.screens.auth.RegisterScreen
import com.vgtech.mobile.ui.screens.consultor.ConsultorDashboardScreen
import com.vgtech.mobile.ui.screens.rh.RHDashboardScreen
import com.vgtech.mobile.ui.screens.supervisor.SupervisorDashboardScreen
import com.vgtech.mobile.ui.screens.proveedor.ProveedorDashboardScreen
import com.vgtech.mobile.ui.screens.cliente.ClienteDashboardScreen
import com.vgtech.mobile.ui.viewmodel.AuthState
import com.vgtech.mobile.ui.viewmodel.AuthViewModel

/**
 * NavGraph — role-based routing with auth guard.
 *
 * On startup, checks if there's an active internal session.
 * If so, resolves the user's role and navigates directly to the
 * appropriate dashboard. Otherwise, shows LoginScreen.
 */
@Composable
fun NavGraph(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    // ── Auth Guard: Check session on first composition ────────────
    LaunchedEffect(Unit) {
        authViewModel.checkSession()
    }

    // ── React to auth state changes ──────────────────────────────
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                val destination = when (state.role) {
                    UserRole.RH         -> Screen.RHDashboard.route
                    UserRole.SUPERVISOR -> Screen.SupervisorDash.route
                    UserRole.CONSULTOR  -> Screen.ConsultorDash.route
                    UserRole.PROVEEDOR  -> Screen.ProveedorDash.route
                    UserRole.CLIENTE    -> Screen.ClienteDash.route
                }
                navController.navigate(destination) {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Unauthenticated -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
            else -> { /* Loading, Idle, Error — handled in screens */ }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // ── Login ────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // ── Register ─────────────────────────────────────────────
        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── RH / Administrativo Dashboard ────────────────────────
        composable(Screen.RHDashboard.route) {
            RHDashboardScreen(
                onLogout = {
                    authViewModel.signOut()
                }
            )
        }

        // ── Supervisor Dashboard ─────────────────────────────────
        composable(Screen.SupervisorDash.route) {
            SupervisorDashboardScreen(
                onLogout = {
                    authViewModel.signOut()
                }
            )
        }

        // ── Consultor Dashboard ──────────────────────────────────
        composable(Screen.ConsultorDash.route) {
            ConsultorDashboardScreen(
                onLogout = {
                    authViewModel.signOut()
                }
            )
        }

        // ── Proveedor Dashboard ──────────────────────────────────
        composable(Screen.ProveedorDash.route) {
            ProveedorDashboardScreen(
                onLogout = {
                    authViewModel.signOut()
                }
            )
        }

        // ── Cliente Dashboard ────────────────────────────────────
        composable(Screen.ClienteDash.route) {
            ClienteDashboardScreen(
                onLogout = {
                    authViewModel.signOut()
                }
            )
        }
    }
}
