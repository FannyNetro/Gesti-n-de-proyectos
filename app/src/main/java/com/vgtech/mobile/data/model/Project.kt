package com.vgtech.mobile.data.model

import java.util.UUID

/**
 * Project data class to manage active constructions and assignments.
 */
data class Project(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val providerUid: String? = null,
    val providerName: String = "",       // Name of the assigned provider
    val supervisorUid: String? = null,
    val consultantUid: String? = null,   // Assigned consultant
    val progress: Float = 0f,            // 0.0 to 1.0
    val status: String = "Pendiente",    // "En Progreso", "Finalizado", "Pendiente"
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    
    // Evaluation and History data
    val comments: String = "",           // General comments or feedback
    val hasDelays: Boolean = false,
    val delayReason: String = "",
    val providerRating: Float = 0f,      // Rating for the provider (1-5)
    val consultantRating: Float = 0f,    // Rating received by the consultant
    val evaluationResult: String = "",   // Result/Outcome of the project evaluation

    // UI State
    val isMarked: Boolean = false        // Added for marking functionality
)

/**
 * Request for project withdrawal/cancellation by a consultant.
 */
data class ProjectCancellationRequest(
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val projectTitle: String,
    val consultantUid: String,
    val reason: String,
    val details: String,
    val date: Long = System.currentTimeMillis(),
    val status: String = "Pendiente" // "Pendiente", "Aprobada", "Rechazada"
)
