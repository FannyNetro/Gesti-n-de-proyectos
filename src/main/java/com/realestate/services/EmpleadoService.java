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
     *
     * @param empleado Objeto Empleado con los datos del formulario de RH
     * @return true si el registro fue exitoso
     */
    public boolean registrarEmpleado(Empleado empleado) {
        String sql = "INSERT INTO dbo.Empleados "
                   + "(NombreCompleto, Direccion, Telefono, PuestoAsignado, Sueldo, Foto) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empleado.getNombreCompleto());
            stmt.setString(2, empleado.getDireccion());
            stmt.setString(3, empleado.getTelefono());
            stmt.setString(4, empleado.getPuesto());
            stmt.setBigDecimal(5, new BigDecimal(empleado.getSueldo()));
            stmt.setString(6, empleado.getNombreFoto());

            int rows = stmt.executeUpdate();
            System.out.println("[EmpleadoService] Empleado guardado en SQL Server: " + empleado.getNombreCompleto());
            return rows > 0;

        } catch (Exception e) {
            System.err.println("[EmpleadoService] ERROR al guardar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retorna todos los empleados desde SQL Server.
     *
     * @return Lista de empleados
     */
    public List<Empleado> obtenerTodos() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT EmpleadoID, NombreCompleto, Direccion, Telefono, "
                   + "PuestoAsignado, Sueldo, Foto FROM dbo.Empleados ORDER BY NombreCompleto";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Empleado e = new Empleado();
                e.setId(rs.getInt("EmpleadoID"));
                e.setNombreCompleto(rs.getString("NombreCompleto"));
                e.setDireccion(rs.getString("Direccion"));
                e.setTelefono(rs.getString("Telefono"));
                e.setPuesto(rs.getString("PuestoAsignado"));
                e.setSueldo(rs.getString("Sueldo"));
                e.setNombreFoto(rs.getString("Foto"));
                lista.add(e);
            }
        } catch (Exception e) {
            System.err.println("[EmpleadoService] ERROR al obtener lista: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}
