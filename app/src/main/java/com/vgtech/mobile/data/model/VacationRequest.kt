package com.vgtech.mobile.data.model

import java.util.UUID

/**
 * VacationRequest data class to track employee vacation history.
 */
data class VacationRequest(
    val id: String = UUID.randomUUID().toString(),
    val employeeUid: String = "",
    val employeeName: String = "",
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis(),
    val daysRequested: Int = 0,
    val status: VacationStatus = VacationStatus.PENDING,
    val requestDate: Long = System.currentTimeMillis(),
    val observations: String = ""
)

enum class VacationStatus {
    PENDING, APPROVED, REJECTED
}
