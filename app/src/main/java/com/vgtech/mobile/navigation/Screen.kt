package com.vgtech.mobile.navigation

/**
 * Sealed class defining all navigation routes for the app.
 */
sealed class Screen(val route: String) {
    data object Splash           : Screen("splash")
    data object Login            : Screen("login")
    data object Register         : Screen("register")
    data object RHDashboard      : Screen("rh_dashboard")
    data object SupervisorDash   : Screen("supervisor_dashboard")
    data object ConsultorDash    : Screen("consultor_dashboard")
    data object ProveedorDash    : Screen("proveedor_dashboard")
    data object ClienteDash      : Screen("cliente_dashboard")
}
