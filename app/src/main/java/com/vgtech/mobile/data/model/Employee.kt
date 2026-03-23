package com.vgtech.mobile.data.model

/**
 * Employee data class matching the VG Tech Firestore document structure.
 *
 * Firestore collection: employees/{uid}
 */
data class Employee(
    val uid: String              = "",
    val nombreCompleto: String   = "",
    val email: String            = "",
    val direccion: String        = "",
    val telefono: String         = "",
    val puesto: String           = "",      // "Consultor", "Supervisor", "Administrativo", "RH", "Proveedor", "Cliente"
    val sueldo: Double           = 0.0,     // Sueldo mensual base o referencia
    val pagoPorHora: Double      = 0.0,     // Pago por hora trabajada
    val diasVacaciones: Int      = 0,
    val password: String         = "",      // Stored for admin visibility (as per spec)
    val fechaRegistro: Long      = System.currentTimeMillis(),
    val activo: Boolean          = true,
    val tipoTrabajo: List<String> = emptyList(), // For Proveedor category tags
    val motivoInactivo: String   = ""
) {
    /** Map roles to navigation destinations */
    fun toRole(): UserRole = when (puesto.lowercase()) {
        "rh", "administrativo" -> UserRole.RH
        "supervisor"           -> UserRole.SUPERVISOR
        "consultor"            -> UserRole.CONSULTOR
        "proveedor"            -> UserRole.PROVEEDOR
        "cliente"              -> UserRole.CLIENTE
        else                   -> UserRole.CONSULTOR
    }

    /** Convert to Firestore-compatible map */
    fun toMap(): Map<String, Any> = mapOf(
        "uid"              to uid,
        "nombreCompleto"   to nombreCompleto,
        "email"            to email,
        "direccion"        to direccion,
        "telefono"         to telefono,
        "puesto"           to puesto,
        "sueldo"           to sueldo,
        "pagoPorHora"      to pagoPorHora,
        "diasVacaciones"   to diasVacaciones,
        "password"         to password,
        "fechaRegistro"    to fechaRegistro,
        "activo"           to activo,
        "tipoTrabajo"      to tipoTrabajo,
        "motivoInactivo"   to motivoInactivo
    )
}

enum class UserRole {
    RH, SUPERVISOR, CONSULTOR, PROVEEDOR, CLIENTE
}
