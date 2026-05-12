package com.vgtech.backend.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.javatime.date

// ── Tabla: usuarios ───────────────────────────────────────────────────────────
object Usuarios : Table("usuarios") {
    val uid              = uuid("uid").clientDefault { java.util.UUID.randomUUID() }
    val nombreCompleto   = varchar("nombre_completo", 150)
    val email            = varchar("email", 150).uniqueIndex()
    val direccion        = text("direccion").default("")
    val telefono         = varchar("telefono", 30).default("")
    val puesto           = varchar("puesto", 50)           // enum como varchar
    val sueldo           = decimal("sueldo", 12, 2).default(java.math.BigDecimal.ZERO)
    val pagoPorHora      = decimal("pago_por_hora", 10, 2).default(java.math.BigDecimal.ZERO)
    val diasVacaciones   = decimal("dias_vacaciones", 6, 1).default(java.math.BigDecimal.ZERO)
    val passwordHash     = text("password_hash").default("")
    val fechaRegistro    = timestamp("fecha_registro")
    val activo           = bool("activo").default(true)
    val motivoInactivo   = text("motivo_inactivo").default("")
    val fotoBase64       = text("foto_base_64").nullable()
    val createdAt        = timestamp("created_at")
    val updatedAt        = timestamp("updated_at")
    override val primaryKey = PrimaryKey(uid)
}

// ── Tabla: usuario_categorias_trabajo ─────────────────────────────────────────
object UsuarioCategoriasWork : Table("usuario_categorias_trabajo") {
    val id          = uuid("id").clientDefault { java.util.UUID.randomUUID() }
    val usuarioUid  = uuid("usuario_uid").references(Usuarios.uid)
    val categoria   = varchar("categoria", 100)
    override val primaryKey = PrimaryKey(id)
}

// ── Tabla: proyectos ──────────────────────────────────────────────────────────
object Proyectos : Table("proyectos") {
    val id                    = uuid("id").clientDefault { java.util.UUID.randomUUID() }
    val titulo                = varchar("titulo", 200)
    val descripcion           = text("descripcion").default("")
    val proveedorUid          = uuid("proveedor_uid").references(Usuarios.uid).nullable()
    val supervisorUid         = uuid("supervisor_uid").references(Usuarios.uid).nullable()
    val consultorUid          = uuid("consultor_uid").references(Usuarios.uid).nullable()
    val clienteUid            = uuid("cliente_uid").references(Usuarios.uid).nullable()
    val progreso              = decimal("progreso", 5, 4).default(java.math.BigDecimal.ZERO)
    val estado                = varchar("estado", 30).default("Pendiente")
    val fechaInicio           = timestamp("fecha_inicio")
    val fechaFin              = timestamp("fecha_fin").nullable()
    val comentarios           = text("comentarios").default("")
    val tieneRetrasos         = bool("tiene_retrasos").default(false)
    val motivoRetraso         = text("motivo_retraso").default("")
    val responsableRetraso    = varchar("responsable_retraso", 20).default("")
    val calificacionProveedor = decimal("calificacion_proveedor", 3, 2).default(java.math.BigDecimal.ZERO)
    val calificacionConsultor = decimal("calificacion_consultor", 3, 2).default(java.math.BigDecimal.ZERO)
    val resultadoEvaluacion   = text("resultado_evaluacion").default("")
    val createdAt             = timestamp("created_at")
    val updatedAt             = timestamp("updated_at")
    override val primaryKey = PrimaryKey(id)
}

// ── Tabla: registro_horas ─────────────────────────────────────────────────────
object RegistroHoras : Table("registro_horas") {
    val id              = uuid("id").clientDefault { java.util.UUID.randomUUID() }
    val empleadoUid     = uuid("empleado_uid").references(Usuarios.uid)
    val fecha           = date("fecha")
    val horasTrabajadas = decimal("horas_trabajadas", 6, 2).default(java.math.BigDecimal.ZERO)
    val horasExtra      = decimal("horas_extra", 6, 2).default(java.math.BigDecimal.ZERO)
    val tarifaExtra     = decimal("tarifa_extra", 4, 2).default(java.math.BigDecimal("2.0"))
    val tarifaHora      = decimal("tarifa_hora", 10, 2).default(java.math.BigDecimal.ZERO)
    val pagoTotal       = decimal("pago_total", 12, 2).default(java.math.BigDecimal.ZERO)
    val observaciones   = text("observaciones").default("")
    val createdAt       = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}

// ── Tabla: solicitudes_vacaciones ─────────────────────────────────────────────
object SolicitudesVacaciones : Table("solicitudes_vacaciones") {
    val id               = uuid("id").clientDefault { java.util.UUID.randomUUID() }
    val empleadoUid      = uuid("empleado_uid").references(Usuarios.uid)
    val fechaInicio      = varchar("fecha_inicio", 10)  // "YYYY-MM-DD"
    val fechaFin         = varchar("fecha_fin", 10)
    val diasSolicitados  = decimal("dias_solicitados", 6, 1).default(java.math.BigDecimal.ZERO)
    val horasSolicitadas = decimal("horas_solicitadas", 6, 2).default(java.math.BigDecimal.ZERO)
    val tipo             = varchar("tipo", 30).default("VACACIONES")
    val estado           = varchar("estado", 20).default("PENDING")
    val fechaSolicitud   = timestamp("fecha_solicitud")
    val observaciones    = text("observaciones").default("")
    val createdAt        = timestamp("created_at")
    val updatedAt        = timestamp("updated_at")
    override val primaryKey = PrimaryKey(id)
}

// ── Tabla: progreso_proyecto ──────────────────────────────────────────────────
object ProgresoProyecto : Table("progreso_proyecto") {
    val id                    = uuid("id").clientDefault { java.util.UUID.randomUUID() }
    val proyectoId            = uuid("proyecto_id").references(Proyectos.id)
    val proveedorUid          = uuid("proveedor_uid").references(Usuarios.uid)
    val fecha                 = timestamp("fecha")
    val porcentajeAvance      = short("porcentaje_avance").default(0)
    val descripcion           = text("descripcion").default("")
    val tipoReporte           = varchar("tipo_reporte", 20).default("Diario")
    val aspectosPositivos     = text("aspectos_positivos").default("")
    val problemas             = text("problemas").default("")
    val motivoRetraso         = text("motivo_retraso").nullable()
    val urlImagen             = text("url_imagen").nullable()
    val evaluado              = bool("evaluado").default(false)
    val evaluacionConsultor   = varchar("evaluacion_consultor", 50).default("")
    val comentariosConsultor  = text("comentarios_consultor").default("")
    val calificacionEvaluacion= decimal("calificacion_evaluacion", 3, 2).default(java.math.BigDecimal.ZERO)
    val urlImagenEvaluacion   = text("url_imagen_evaluacion").nullable()
    val fueModificado         = bool("fue_modificado").default(false)
    val progresoOriginal      = short("progreso_original").default(0)
    val motivoModificacion    = text("motivo_modificacion").default("")
    val createdAt             = timestamp("created_at")
    val updatedAt             = timestamp("updated_at")
    override val primaryKey = PrimaryKey(id)
}

// ── Tabla: invitaciones_proveedor ─────────────────────────────────────────────
object InvitacionesProveedor : Table("invitaciones_proveedor") {
    val id            = uuid("id").clientDefault { java.util.UUID.randomUUID() }
    val proyectoId    = uuid("proyecto_id").references(Proyectos.id)
    val proveedorUid  = uuid("proveedor_uid").references(Usuarios.uid)
    val supervisorUid = uuid("supervisor_uid").references(Usuarios.uid)
    val mensaje       = text("mensaje").default("")
    val fecha         = timestamp("fecha")
    val estado        = varchar("estado", 20).default("Enviada")
    val createdAt     = timestamp("created_at")
    val updatedAt     = timestamp("updated_at")
    override val primaryKey = PrimaryKey(id)
}

// ── Tabla: cotizaciones ───────────────────────────────────────────────────────
object Cotizaciones : Table("cotizaciones") {
    val id                   = uuid("id").clientDefault { java.util.UUID.randomUUID() }
    val invitacionId         = uuid("invitacion_id").references(InvitacionesProveedor.id)
    val proyectoId           = uuid("proyecto_id").references(Proyectos.id)
    val proveedorUid         = uuid("proveedor_uid").references(Usuarios.uid)
    val monto                = decimal("monto", 14, 2).default(java.math.BigDecimal.ZERO)
    val diasEstimados        = short("dias_estimados").default(0)
    val descripcion          = text("descripcion").default("")
    val fecha                = timestamp("fecha")
    val enviadaACliente      = bool("enviada_a_cliente").default(false)
    val estadoCliente        = varchar("estado_cliente", 20).default("Pendiente")
    val confirmadoSupervisor = bool("confirmado_supervisor").default(false)
    val createdAt            = timestamp("created_at")
    val updatedAt            = timestamp("updated_at")
    override val primaryKey = PrimaryKey(id)
}

// ── Tabla: evaluaciones_desempeno ─────────────────────────────────────────────
object EvaluacionesDesempeno : Table("evaluaciones_desempeno") {
    val id                        = uuid("id").clientDefault { java.util.UUID.randomUUID() }
    val evaluadoUid               = uuid("evaluado_uid").references(Usuarios.uid)
    val rolEvaluado               = varchar("rol_evaluado", 30)
    val proyectoId                = uuid("proyecto_id").references(Proyectos.id).nullable()
    val calificacionCalidad       = decimal("calificacion_calidad", 3, 2).default(java.math.BigDecimal.ZERO)
    val calificacionPuntualidad   = decimal("calificacion_puntualidad", 3, 2).default(java.math.BigDecimal.ZERO)
    val calificacionComunicacion  = decimal("calificacion_comunicacion", 3, 2).default(java.math.BigDecimal.ZERO)
    val calificacionGeneral       = decimal("calificacion_general", 3, 2).default(java.math.BigDecimal.ZERO)
    val comentarios               = text("comentarios").default("")
    val fecha                     = timestamp("fecha")
    val createdAt                 = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}

// ── Tabla: fases_pago ─────────────────────────────────────────────────────────
object FasesPago : Table("fases_pago") {
    val id              = uuid("id").clientDefault { java.util.UUID.randomUUID() }
    val proveedorId     = uuid("proveedor_id").references(Usuarios.uid)
    val proyectoId      = uuid("proyecto_id").references(Proyectos.id)
    val numeroFase      = short("numero_fase")
    val totalFases      = short("total_fases")
    val montoAPagar     = decimal("monto_a_pagar", 14, 2)
    val fechaProgramada = timestamp("fecha_programada")
    val estado          = varchar("estado", 20).default("PENDIENTE")
    val fechaPago       = timestamp("fecha_pago").nullable()
    val createdAt       = timestamp("created_at")
    val updatedAt       = timestamp("updated_at")
    override val primaryKey = PrimaryKey(id)
}

// ── Tabla: transacciones_proveedor ────────────────────────────────────────────
object TransaccionesProveedor : Table("transacciones_proveedor") {
    val id             = uuid("id").clientDefault { java.util.UUID.randomUUID() }
    val proveedorId    = uuid("proveedor_id").references(Usuarios.uid)
    val proyectoId     = uuid("proyecto_id").references(Proyectos.id).nullable()
    val faseId         = uuid("fase_id").references(FasesPago.id).nullable()
    val timestamp      = timestamp("timestamp")
    val tipo           = varchar("tipo", 20)
    val montoBruto     = decimal("monto_bruto", 14, 2).default(java.math.BigDecimal.ZERO)
    val corteEmpresa   = decimal("corte_empresa", 14, 2).default(java.math.BigDecimal.ZERO)
    val corteProveedor = decimal("corte_proveedor", 14, 2).default(java.math.BigDecimal.ZERO)
    val descripcion    = text("descripcion").default("")
    val createdAt      = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}

// ── Tabla: resumen_cuenta_proveedor ───────────────────────────────────────────
object ResumenCuentaProveedor : Table("resumen_cuenta_proveedor") {
    val proveedorId          = uuid("proveedor_id").references(Usuarios.uid)
    val totalGanadoServicios = decimal("total_ganado_servicios", 14, 2).default(java.math.BigDecimal.ZERO)
    val totalPagado          = decimal("total_pagado", 14, 2).default(java.math.BigDecimal.ZERO)
    val saldoPendiente       = decimal("saldo_pendiente", 14, 2).default(java.math.BigDecimal.ZERO)
    val gananciaEmpresaTotal = decimal("ganancia_empresa_total", 14, 2).default(java.math.BigDecimal.ZERO)
    val estadoCuenta         = varchar("estado_cuenta", 20).default("PENDIENTE")
    val updatedAt            = timestamp("updated_at")
    override val primaryKey = PrimaryKey(proveedorId)
}

// ── Tabla: mensajes_chat ──────────────────────────────────────────────────────
object MensajesChat : Table("mensajes_chat") {
    val id              = uuid("id").clientDefault { java.util.UUID.randomUUID() }
    val remitenteUid    = uuid("remitente_uid").references(Usuarios.uid)
    val destinatarioUid = uuid("destinatario_uid").references(Usuarios.uid)
    val proyectoId      = uuid("proyecto_id").references(Proyectos.id).nullable()
    val mensaje         = text("mensaje")
    val timestamp       = timestamp("timestamp")
    val leido           = bool("leido").default(false)
    val createdAt       = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}

// ── Tabla: solicitudes_cancelacion ────────────────────────────────────────────
object SolicitudesCancelacion : Table("solicitudes_cancelacion") {
    val id           = uuid("id").clientDefault { java.util.UUID.randomUUID() }
    val proyectoId   = uuid("proyecto_id").references(Proyectos.id)
    val consultorUid = uuid("consultor_uid").references(Usuarios.uid)
    val motivo       = text("motivo")
    val detalles     = text("detalles").default("")
    val fecha        = timestamp("fecha")
    val estado       = varchar("estado", 20).default("Pendiente")
    val createdAt    = timestamp("created_at")
    val updatedAt    = timestamp("updated_at")
    override val primaryKey = PrimaryKey(id)
}
