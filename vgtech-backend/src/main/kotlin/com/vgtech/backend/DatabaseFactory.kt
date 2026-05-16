package com.vgtech.backend

import com.vgtech.backend.models.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl         = "jdbc:postgresql://db.cilvugaqpjgvqxxhqyoj.supabase.co:5432/postgres"
            username        = "postgres"
            password        = "yRKRZECuGfWO0PXs"
            maximumPoolSize = 10
            isAutoCommit    = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        Database.connect(HikariDataSource(config))
        transaction {
            try {
                // Forzar que la columna puesto sea VARCHAR para evitar conflictos de tipo rol_usuario
                exec("ALTER TABLE usuarios ALTER COLUMN puesto TYPE VARCHAR(50) USING puesto::VARCHAR;")
            } catch (e: Exception) {
                println("Aviso: No se pudo alterar la columna puesto (puede que no exista aún)")
            }
            SchemaUtils.createMissingTablesAndColumns(
                Usuarios,
                UsuarioCategoriasWork,
                Proyectos,
                RegistroHoras,
                SolicitudesVacaciones,
                ProgresoProyecto,
                InvitacionesProveedor,
                Cotizaciones,
                EvaluacionesDesempeno,
                FasesPago,
                TransaccionesProveedor,
                ResumenCuentaProveedor,
                MensajesChat,
                SolicitudesCancelacion
            )
        }
        println("✅ Conexión a PostgreSQL establecida — Vg_Tech")
    }
}
