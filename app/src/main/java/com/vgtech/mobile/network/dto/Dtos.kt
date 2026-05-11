package com.vgtech.mobile.network.dto

import com.google.gson.annotations.SerializedName

// ── Auth ─────────────────────────────────────────────────────────────────────

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class LoginResponseDto(
    val uid: String,
    val nombreCompleto: String,
    val email: String,
    val puesto: String,
    val activo: Boolean
)

// ── Usuario ───────────────────────────────────────────────────────────────────

data class UsuarioDto(
    val uid: String = "",
    val nombreCompleto: String = "",
    val email: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val puesto: String = "",
    val sueldo: Double = 0.0,
    val pagoPorHora: Double = 0.0,
    val diasVacaciones: Double = 0.0,
    val activo: Boolean = true,
    val motivoInactivo: String = "",
    val fotoBase64: String? = null,
    val tipoTrabajo: List<String> = emptyList()
)

data class CreateUsuarioDto(
    val nombreCompleto: String,
    val email: String,
    val password: String,
    val direccion: String = "",
    val telefono: String = "",
    val puesto: String,
    val sueldo: Double = 0.0,
    val pagoPorHora: Double = 0.0,
    val diasVacaciones: Double = 0.0,
    val fotoBase64: String? = null,
    val tipoTrabajo: List<String> = emptyList()
)

data class UpdateUsuarioDto(
    val nombreCompleto: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    val puesto: String? = null,
    val sueldo: Double? = null,
    val pagoPorHora: Double? = null,
    val diasVacaciones: Double? = null,
    val activo: Boolean? = null,
    val motivoInactivo: String? = null,
    val password: String? = null,
    val fotoBase64: String? = null
)

// ── Proyecto ──────────────────────────────────────────────────────────────────

data class ProyectoDto(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val proveedorUid: String? = null,
    val supervisorUid: String? = null,
    val consultorUid: String? = null,
    val clienteUid: String? = null,
    val progreso: Double = 0.0,
    val estado: String = "Pendiente",
    val fechaInicio: Long = 0L,
    val fechaFin: Long? = null,
    val comentarios: String = "",
    val tieneRetrasos: Boolean = false,
    val motivoRetraso: String = "",
    val responsableRetraso: String = "",
    val calificacionProveedor: Double = 0.0,
    val calificacionConsultor: Double = 0.0,
    val resultadoEvaluacion: String = ""
)

data class CreateProyectoDto(
    val titulo: String,
    val descripcion: String = "",
    val proveedorUid: String? = null,
    val supervisorUid: String? = null,
    val consultorUid: String? = null,
    val clienteUid: String? = null
)

data class UpdateProyectoDto(
    val titulo: String? = null,
    val descripcion: String? = null,
    val proveedorUid: String? = null,
    val supervisorUid: String? = null,
    val consultorUid: String? = null,
    val progreso: Double? = null,
    val estado: String? = null,
    val fechaFin: Long? = null,
    val comentarios: String? = null,
    val tieneRetrasos: Boolean? = null,
    val motivoRetraso: String? = null,
    val responsableRetraso: String? = null,
    val calificacionProveedor: Double? = null,
    val calificacionConsultor: Double? = null,
    val resultadoEvaluacion: String? = null
)

// ── Vacaciones ────────────────────────────────────────────────────────────────

data class VacacionDto(
    val id: String = "",
    val empleadoUid: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val diasSolicitados: Double = 0.0,
    val horasSolicitadas: Double = 0.0,
    val tipo: String = "VACACIONES",
    val estado: String = "PENDING",
    val fechaSolicitud: Long = 0L,
    val observaciones: String = ""
)

data class CreateVacacionDto(
    val empleadoUid: String,
    val fechaInicio: String,
    val fechaFin: String,
    val diasSolicitados: Double = 0.0,
    val horasSolicitadas: Double = 0.0,
    val tipo: String = "VACACIONES",
    val observaciones: String = ""
)

// ── Registro de Horas ─────────────────────────────────────────────────────────

data class RegistroHorasDto(
    val id: String = "",
    val empleadoUid: String = "",
    val fecha: String = "",
    val horasTrabajadas: Double = 0.0,
    val horasExtra: Double = 0.0,
    val tarifaExtra: Double = 2.0,
    val tarifaHora: Double = 0.0,
    val pagoTotal: Double = 0.0,
    val observaciones: String = ""
)

data class CreateRegistroHorasDto(
    val empleadoUid: String,
    val fecha: String,
    val horasTrabajadas: Double = 0.0,
    val horasExtra: Double = 0.0,
    val tarifaExtra: Double = 2.0,
    val tarifaHora: Double = 0.0,
    val observaciones: String = ""
)

// ── Progreso Proyecto ─────────────────────────────────────────────────────────

data class ProgresoDto(
    val id: String = "",
    val proyectoId: String = "",
    val proveedorUid: String = "",
    val fecha: Long = 0L,
    val porcentajeAvance: Int = 0,
    val descripcion: String = "",
    val tipoReporte: String = "Diario",
    val aspectosPositivos: String = "",
    val problemas: String = "",
    val motivoRetraso: String? = null,
    val urlImagen: String? = null,
    val evaluado: Boolean = false,
    val evaluacionConsultor: String = "",
    val comentariosConsultor: String = "",
    val calificacionEvaluacion: Double = 0.0,
    val fueModificado: Boolean = false,
    val progresoOriginal: Int = 0,
    val motivoModificacion: String = ""
)

data class CreateProgresoDto(
    val proyectoId: String,
    val proveedorUid: String,
    val porcentajeAvance: Int = 0,
    val descripcion: String = "",
    val tipoReporte: String = "Diario",
    val aspectosPositivos: String = "",
    val problemas: String = "",
    val motivoRetraso: String? = null,
    val urlImagen: String? = null
)

data class EvaluarProgresoDto(
    val evaluacionConsultor: String,
    val comentariosConsultor: String = "",
    val calificacionEvaluacion: Double = 0.0,
    val urlImagenEvaluacion: String? = null,
    val progresoModificado: Int? = null,
    val motivoModificacion: String = ""
)

// ── Invitaciones ──────────────────────────────────────────────────────────────

data class InvitacionDto(
    val id: String = "",
    val proyectoId: String = "",
    val proveedorUid: String = "",
    val supervisorUid: String = "",
    val mensaje: String = "",
    val fecha: Long = 0L,
    val estado: String = "Enviada"
)

data class CreateInvitacionDto(
    val proyectoId: String,
    val proveedorUid: String,
    val supervisorUid: String,
    val mensaje: String = ""
)

// ── Cotizaciones ──────────────────────────────────────────────────────────────

data class CotizacionDto(
    val id: String = "",
    val invitacionId: String = "",
    val proyectoId: String = "",
    val proveedorUid: String = "",
    val monto: Double = 0.0,
    val diasEstimados: Int = 0,
    val descripcion: String = "",
    val fecha: Long = 0L,
    val enviadaACliente: Boolean = false,
    val estadoCliente: String = "Pendiente",
    val confirmadoSupervisor: Boolean = false
)

data class CreateCotizacionDto(
    val invitacionId: String,
    val proyectoId: String,
    val proveedorUid: String,
    val monto: Double,
    val diasEstimados: Int = 0,
    val descripcion: String = ""
)

// ── Evaluaciones ──────────────────────────────────────────────────────────────

data class EvaluacionDto(
    val id: String = "",
    val evaluadoUid: String = "",
    val rolEvaluado: String = "",
    val proyectoId: String? = null,
    val calificacionCalidad: Double = 0.0,
    val calificacionPuntualidad: Double = 0.0,
    val calificacionComunicacion: Double = 0.0,
    val calificacionGeneral: Double = 0.0,
    val comentarios: String = "",
    val fecha: Long = 0L
)

data class CreateEvaluacionDto(
    val evaluadoUid: String,
    val rolEvaluado: String,
    val proyectoId: String? = null,
    val calificacionCalidad: Double,
    val calificacionPuntualidad: Double,
    val calificacionComunicacion: Double,
    val comentarios: String = ""
)

// ── Fases de Pago ─────────────────────────────────────────────────────────────

data class FasePagoDto(
    val id: String = "",
    val proveedorId: String = "",
    val proyectoId: String = "",
    val numeroFase: Int = 0,
    val totalFases: Int = 0,
    val montoAPagar: Double = 0.0,
    val fechaProgramada: Long = 0L,
    val estado: String = "PENDIENTE",
    val fechaPago: Long? = null
)

data class CreateFasePagoDto(
    val proveedorId: String,
    val proyectoId: String,
    val numeroFase: Int,
    val totalFases: Int,
    val montoAPagar: Double,
    val fechaProgramada: Long
)

// ── Transacciones ─────────────────────────────────────────────────────────────

data class TransaccionDto(
    val id: String = "",
    val proveedorId: String = "",
    val proyectoId: String? = null,
    val faseId: String? = null,
    val timestamp: Long = 0L,
    val tipo: String = "",
    val montoBruto: Double = 0.0,
    val corteEmpresa: Double = 0.0,
    val corteProveedor: Double = 0.0,
    val descripcion: String = ""
)

data class CreateTransaccionDto(
    val proveedorId: String,
    val proyectoId: String? = null,
    val faseId: String? = null,
    val tipo: String,
    val montoBruto: Double,
    val corteEmpresa: Double = 0.0,
    val corteProveedor: Double = 0.0,
    val descripcion: String = ""
)

// ── Chat ──────────────────────────────────────────────────────────────────────

data class MensajeChatDto(
    val id: String = "",
    val remitenteUid: String = "",
    val destinatarioUid: String = "",
    val proyectoId: String? = null,
    val mensaje: String = "",
    val timestamp: Long = 0L,
    val leido: Boolean = false
)

data class CreateMensajeChatDto(
    val remitenteUid: String,
    val destinatarioUid: String,
    val proyectoId: String? = null,
    val mensaje: String
)

// ── Cancelaciones ─────────────────────────────────────────────────────────────

data class CancelacionDto(
    val id: String = "",
    val proyectoId: String = "",
    val consultorUid: String = "",
    val motivo: String = "",
    val detalles: String = "",
    val fecha: Long = 0L,
    val estado: String = "Pendiente"
)

data class CreateCancelacionDto(
    val proyectoId: String,
    val consultorUid: String,
    val motivo: String,
    val detalles: String = ""
)
