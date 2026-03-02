package com.realestate.services;

import com.realestate.models.Empleado;
import java.util.ArrayList;
import java.util.List;

/**
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  SERVICIO: EmpleadoService                                               │
 * │  Lógica de negocio para registrar y consultar empleados internos.        │
 * │                                                                          │
 * │  AHORA:   Almacenamiento en memoria (prototipo).                         │
 * │  FUTURO:  INSERT / SELECT contra la tabla Empleados en SQL Server.       │
 * └─────────────────────────────────────────────────────────────────────────┘
 */
public class EmpleadoService {

    // ── Almacenamiento temporal (SOLO PROTOTIPO) ───────────────────────────
    // ELIMINAR esta lista al implementar la versión con BD real.
    private static final List<Empleado> empleadosEnMemoria = new ArrayList<>();
    private static int autoId = 1;
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Registra un nuevo empleado en el sistema.
     *
     * @param empleado Objeto Empleado con los datos del formulario de RH
     * @return true si el registro fue exitoso
     */
    public boolean registrarEmpleado(Empleado empleado) {

        // ── SIMULACIÓN EN MEMORIA (PROTOTIPO) ──────────────────────────────
        empleado.setId(autoId++);
        empleadosEnMemoria.add(empleado);
        System.out.println("[EmpleadoService] Empleado registrado en memoria: " + empleado);
        // ──────────────────────────────────────────────────────────────────

        /* ══════════════════════════════════════════════════════════════════
         *  IMPLEMENTACIÓN FUTURA CON JDBC + SQL Server
         * ══════════════════════════════════════════════════════════════════
         *
         *  String sql = "INSERT INTO Empleados "
         *             + "(nombreCompleto, direccion, telefono, puesto, sueldo, rutaFoto) "
         *             + "VALUES (?, ?, ?, ?, ?, ?)";
         *
         *  try (Connection conn = DatabaseConfig.getConnection();
         *       PreparedStatement stmt = conn.prepareStatement(sql)) {
         *
         *      stmt.setString(1, empleado.getNombreCompleto());
         *      stmt.setString(2, empleado.getDireccion());
         *      stmt.setString(3, empleado.getTelefono());
         *      stmt.setString(4, empleado.getPuesto());
         *      stmt.setBigDecimal(5, new BigDecimal(empleado.getSueldo()));
         *      stmt.setString(6, empleado.getNombreFoto());
         *
         *      return stmt.executeUpdate() > 0;
         *  } catch (Exception e) {
         *      e.printStackTrace();
         *      return false;
         *  }
         * ══════════════════════════════════════════════════════════════════ */

        return true;
    }

    /**
     * Retorna todos los empleados registrados.
     *
     * @return Lista de empleados
     */
    public List<Empleado> obtenerTodos() {

        // ── SIMULACIÓN EN MEMORIA (PROTOTIPO) ──────────────────────────────
        return new ArrayList<>(empleadosEnMemoria);
        // ──────────────────────────────────────────────────────────────────

        /* ══════════════════════════════════════════════════════════════════
         *  IMPLEMENTACIÓN FUTURA CON JDBC + SQL Server
         * ══════════════════════════════════════════════════════════════════
         *
         *  List<Empleado> lista = new ArrayList<>();
         *  String sql = "SELECT id, nombreCompleto, direccion, telefono, "
         *             + "       puesto, sueldo, rutaFoto "
         *             + "FROM Empleados ORDER BY nombreCompleto";
         *
         *  try (Connection conn = DatabaseConfig.getConnection();
         *       PreparedStatement stmt = conn.prepareStatement(sql);
         *       ResultSet rs = stmt.executeQuery()) {
         *
         *      while (rs.next()) {
         *          Empleado e = new Empleado();
         *          e.setId(rs.getInt("id"));
         *          e.setNombreCompleto(rs.getString("nombreCompleto"));
         *          e.setDireccion(rs.getString("direccion"));
         *          e.setTelefono(rs.getString("telefono"));
         *          e.setPuesto(rs.getString("puesto"));
         *          e.setSueldo(rs.getString("sueldo"));
         *          e.setNombreFoto(rs.getString("rutaFoto"));
         *          lista.add(e);
         *      }
         *  } catch (Exception e) { e.printStackTrace(); }
         *  return lista;
         * ══════════════════════════════════════════════════════════════════ */
    }
}
