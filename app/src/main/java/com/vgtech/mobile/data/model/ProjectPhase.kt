package com.vgtech.mobile.data.model

import java.util.UUID

data class ProjectPhase(
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val name: String,
    val description: String = "",
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis(),
    val status: String = "Pendiente",
    val progressPercentage: Int = 0,
    val imageUrl: String? = null,
    val observations: String = ""
)
