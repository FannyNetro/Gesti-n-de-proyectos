package com.realestate.services;

/**
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                         SERVICIO DE AUTENTICACIÓN                       │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * RESPONSABILIDAD: Verificar las credenciales de un usuario e identificar
 * si tiene el perfil de Administrador de Recursos Humanos (RH).
 *
 * IMPLEMENTACIÓN ACTUAL: Validación simulada en memoria (hardcoded).
 * IMPLEMENTACIÓN FUTURA:  Consulta SQL a la tabla Usuarios en SQL Server.
 */
public class AuthService {

    /**
     * Verifica si las credenciales corresponden a un Administrador de RH.
     *
     * @param usuario  Nombre de usuario ingresado en el login
     * @param password Contraseña ingresada en el login
     * @return true si las credenciales son válidas y el perfil es RH_ADMIN
     */
    public boolean esAdminRH(String usuario, String password) {

        // ─── SIMULACIÓN EN MEMORIA (PROTOTIPO) ─────────────────────────────
        // Credenciales hardcoded únicamente para demostración.
        // ELIMINAR este bloque al implementar la versión con BD real.
        boolean credencialesValidas = "admin_rh".equals(usuario) && "1234".equals(password);
        // ──────────────────────────────────────────────────────────────────

        /* ══════════════════════════════════════════════════════════════════
         * IMPLEMENTACIÓN FUTURA CON JDBC + SQL Server
         * ══════════════════════════════════════════════════════════════════
         *
         * String sql = "SELECT perfil FROM Usuarios WHERE usuario = ? AND password_hash = ? AND activo = 1";
         *
         * try (Connection conn = DatabaseConfig.getConnection();
         *      PreparedStatement stmt = conn.prepareStatement(sql)) {
         *
         *     stmt.setString(1, usuario);
         *     stmt.setString(2, hashPassword(password)); // usar BCrypt o SHA-256
         *
         *     ResultSet rs = stmt.executeQuery();
         *     if (rs.next()) {
         *         String perfil = rs.getString("perfil");
         *         return "RH_ADMIN".equals(perfil);
         *     }
         * } catch (Exception e) {
         *     // Manejar excepción (log + respuesta de error)
         *     e.printStackTrace();
         * }
         * return false;
         * ══════════════════════════════════════════════════════════════════ */

        return credencialesValidas;
    }
}
