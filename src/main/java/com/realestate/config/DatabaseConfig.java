package com.realestate.config;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                   CONFIGURACIÓN DE BASE DE DATOS                        │
 * │              SQL Server - Base de datos: Inboviliaria                   │
 * └─────────────────────────────────────────────────────────────────────────┘
 */
public class DatabaseConfig {

    // ─── Parámetros de conexión ────────────────────────────────────────────
    private static final String JDBC_URL  = "jdbc:sqlserver://localhost:1433;"
                                          + "databaseName=Inboviliaria;"
                                          + "encrypt=true;trustServerCertificate=true";
    private static final String JDBC_USER = "vgtechpro";
    private static final String JDBC_PASS = "vgtech";

    /**
     * Retorna una conexión activa a SQL Server (Inboviliaria).
     *
     * @return Connection objeto de conexión JDBC
     * @throws Exception si no se puede establecer conexión
     */
    public static Connection getConnection() throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
    }
}
