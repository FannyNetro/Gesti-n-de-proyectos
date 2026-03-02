package com.realestate.services;

import com.realestate.config.DatabaseConfig;
import com.realestate.models.Empleado;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  SERVICIO: EmpleadoService                                               │
 * │  Lógica de negocio para registrar y consultar empleados internos.        │
 * │  ACTIVO: INSERT / SELECT contra la tabla Empleados en SQL Server.        │
 * └─────────────────────────────────────────────────────────────────────────┘
 */
public class EmpleadoService {

    /**
     * Registra un nuevo empleado en SQL Server.
     * @return null si fue exitoso, o el mensaje de error si falló
     */
    public String registrarEmpleado(Empleado empleado) {
        String sql = "INSERT INTO dbo.Empleados "
                   + "(NombreCompleto, Direccion, Telefono, PuestoAsignado, Sueldo, Foto) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Limpiar sueldo: quitar símbolo $ y comas antes de parsear
            String sueldoLimpio = empleado.getSueldo()
                    .trim()
                    .replace("$", "")
                    .replace(",", "")
                    .replace(" ", "");
            BigDecimal sueldo = new BigDecimal(sueldoLimpio);

            stmt.setString(1, empleado.getNombreCompleto());
            stmt.setString(2, empleado.getDireccion());
            stmt.setString(3, empleado.getTelefono());
            stmt.setString(4, empleado.getPuesto());
            stmt.setBigDecimal(5, sueldo);
            stmt.setString(6, empleado.getNombreFoto());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        empleado.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("[EmpleadoService] Guardado: ID=" + empleado.getId() + " - " + empleado.getNombreCompleto());
            }
            return null; // null = éxito

        } catch (Exception e) {
            String msg = e.getClass().getSimpleName() + ": " + e.getMessage();
            System.err.println("[EmpleadoService] ERROR: " + msg);
            e.printStackTrace();
            return msg; // retorna el error para mostrarlo
        }
    }

    /**
     * Retorna todos los empleados desde SQL Server.
     *
     * @return Lista de empleados
     */
    public List<Empleado> obtenerTodos() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT "
                   + "    EmpleadoID      AS ID, "
                   + "    NombreCompleto  AS [Nombre Completo], "
                   + "    PuestoAsignado  AS Puesto, "
                   + "    FORMAT(Sueldo, 'C', 'es-MX') AS Sueldo, "
                   + "    Telefono        AS [Teléfono], "
                   + "    Direccion       AS [Dirección], "
                   + "    Foto            AS [Ruta Foto], "
                   + "    FechaRegistro   AS [Fecha de Registro] "
                   + "FROM dbo.Empleados "
                   + "ORDER BY FechaRegistro DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Empleado e = new Empleado();
                e.setId(rs.getInt("ID"));
                e.setNombreCompleto(rs.getString("Nombre Completo"));
                e.setDireccion(rs.getString("Dirección"));
                e.setTelefono(rs.getString("Teléfono"));
                e.setPuesto(rs.getString("Puesto"));
                e.setSueldo(rs.getString("Sueldo"));
                e.setNombreFoto(rs.getString("Ruta Foto"));
                lista.add(e);
            }
        } catch (Exception e) {
            System.err.println("[EmpleadoService] ERROR al obtener lista: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}
