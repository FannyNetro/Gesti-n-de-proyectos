package com.vgtech.mobile.data.model

import java.util.UUID

/**
 * ProjectProgress data class to track progress reports from providers.
 */
data class ProjectProgress(
    val id: String = UUID.randomUUID().toString(),
    val projectId: String = "",
    val projectTitle: String = "",
    val providerUid: String = "",
    val providerName: String = "",
    val date: Long = System.currentTimeMillis(),
    val progressPercentage: Int = 0,
    val description: String = "", // General activity description
    val reportType: String = "Diario", // "Diario", "Semanal", "Mensual"
    val highlights: String = "", // Good things
    val issues: String = "", // Bad things / Obstacles
    val delayReason: String? = null,
    val imageUrl: String? = null,
    
    // Consultant Evaluation fields
    val evaluated: Boolean = false,
    val consultantEvaluation: String = "", // "Aprobado", "Con Observaciones", "Rechazado"
    val consultantComments: String = "",
    val evaluationRating: Float = 0f,
    val evaluationImageUrl: String? = null,
    
    // Progress Modification (Consultant Correction)
    val wasModified: Boolean = false,
    val originalProgress: Int = 0,
    val modificationReason: String = ""
)
