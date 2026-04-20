package com.vgtech.mobile.data.model

import java.util.UUID

/**
 * Invitation sent by a supervisor to a provider for a project (HU7).
 */
data class ProviderInvitation(
    val id: String = UUID.randomUUID().toString(),
    val projectId: String = "",
    val projectTitle: String = "",
    val providerUid: String = "",
    val providerName: String = "",
    val supervisorUid: String = "",
    val message: String = "",
    val date: Long = System.currentTimeMillis(),
    val status: String = "Enviada" // "Enviada", "Aceptada", "Rechazada", "Cotizada"
)

/**
 * Quotation received from a provider in response to an invitation (HU7).
 */
data class Quotation(
    val id: String = UUID.randomUUID().toString(),
    val invitationId: String = "",
    val projectId: String = "",
    val projectTitle: String = "",
    val providerUid: String = "",
    val providerName: String = "",
    val amount: Double = 0.0,
    val estimatedDays: Int = 0,
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val sentToClient: Boolean = false,
    val clientStatus: String = "Pendiente" // "Pendiente", "Aprobada", "Rechazada"
)

/**
 * Performance evaluation for a provider or consultant (HU5).
 */
data class PerformanceEvaluation(
    val id: String = UUID.randomUUID().toString(),
    val evaluatedUid: String = "",
    val evaluatedName: String = "",
    val evaluatedRole: String = "",   // "Consultor" or "Proveedor"
    val projectId: String = "",
    val projectTitle: String = "",
    val qualityRating: Float = 0f,    // 1–5
    val timelinessRating: Float = 0f, // 1–5
    val communicationRating: Float = 0f, // 1–5
    val overallRating: Float = 0f,    // computed average
    val comments: String = "",
    val date: Long = System.currentTimeMillis()
)
