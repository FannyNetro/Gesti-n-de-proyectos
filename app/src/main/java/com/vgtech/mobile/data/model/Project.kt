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
    val supervisorUid: String? = null,
    val progress: Float = 0f, // 0.0 to 1.0
    val status: String = "Pendiente", // "En Progreso", "Finalizado", "Pendiente"
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null
)
