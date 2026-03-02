package com.realestate.services;

import com.realestate.models.Proveedor;
import java.util.ArrayList;
import java.util.List;

/**
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                      SERVICIO DE PROVEEDORES                            │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * RESPONSABILIDAD: Lógica de negocio para crear y consultar proveedores
 * (empresas constructoras).
 *
 * IMPLEMENTACIÓN ACTUAL: Lista en memoria (los datos se pierden al reiniciar).
 * IMPLEMENTACIÓN FUTURA:  INSERT/SELECT a la tabla Proveedores en SQL Server.
 */
public class ProveedorService {

    // ─── Almacenamiento temporal en memoria (SOLO PROTOTIPO) ───────────────
    // ELIMINAR esta lista al implementar la versión con BD real.
    private static final List<Proveedor> proveedoresEnMemoria = new ArrayList<>();
    private static int autoIncrementoId = 1;
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Registra un nuevo proveedor en el sistema.
     *
     * @param proveedor Objeto Proveedor con los datos del formulario
     * @return true si el registro fue exitoso
     */
    public boolean registrarProveedor(Proveedor proveedor) {

        // ─── SIMULACIÓN EN MEMORIA (PROTOTIPO) ─────────────────────────────
        proveedor.setId(autoIncrementoId++);
        proveedoresEnMemoria.add(proveedor);
        System.out.println("Proveedor registrado (en memoria): " + proveedor);
        // ──────────────────────────────────────────────────────────────────

        /* ══════════════════════════════════════════════════════════════════
         * IMPLEMENTACIÓN FUTURA CON JDBC + SQL Server
         * ══════════════════════════════════════════════════════════════════
         *
         * String sql = "INSERT INTO Proveedores (nombre, rfc, nombreContacto, telefono) "
         *            + "VALUES (?, ?, ?, ?)";
         *
         * try (Connection conn = DatabaseConfig.getConnection();
         *      PreparedStatement stmt = conn.prepareStatement(sql)) {
         *
         *     stmt.setString(1, proveedor.getNombre());
         *     stmt.setString(2, proveedor.getRfc());
         *     stmt.setString(3, proveedor.getNombreContacto());
         *     stmt.setString(4, proveedor.getTelefono());
         *
         *     int filasAfectadas = stmt.executeUpdate();
         *     return filasAfectadas > 0;
         *
         * } catch (Exception e) {
         *     e.printStackTrace();
         *     return false;
         * }
         * ══════════════════════════════════════════════════════════════════ */

        return true;
    }

    /**
     * Retorna todos los proveedores registrados (para consultas futuras).
     *
     * @return Lista de proveedores
     */
    public List<Proveedor> obtenerTodos() {

        // ─── SIMULACIÓN EN MEMORIA (PROTOTIPO) ─────────────────────────────
        return new ArrayList<>(proveedoresEnMemoria);
        // ──────────────────────────────────────────────────────────────────

        /* ══════════════════════════════════════════════════════════════════
         * IMPLEMENTACIÓN FUTURA CON JDBC + SQL Server
         * ══════════════════════════════════════════════════════════════════
         *
         * List<Proveedor> lista = new ArrayList<>();
         * String sql = "SELECT id, nombre, rfc, nombreContacto, telefono FROM Proveedores ORDER BY nombre";
         *
         * try (Connection conn = DatabaseConfig.getConnection();
         *      PreparedStatement stmt = conn.prepareStatement(sql);
         *      ResultSet rs = stmt.executeQuery()) {
         *
         *     while (rs.next()) {
         *         Proveedor p = new Proveedor();
         *         p.setId(rs.getInt("id"));
         *         p.setNombre(rs.getString("nombre"));
         *         p.setRfc(rs.getString("rfc"));
         *         p.setNombreContacto(rs.getString("nombreContacto"));
         *         p.setTelefono(rs.getString("telefono"));
         *         lista.add(p);
         *     }
         * } catch (Exception e) {
         *     e.printStackTrace();
         * }
         * return lista;
         * ══════════════════════════════════════════════════════════════════ */
    }
}
