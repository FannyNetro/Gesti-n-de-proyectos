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
    val hoursRequested: Double = 0.0, // Used for "Horas trabajadas" (taking hours off)
    val halfDays: Int = 0,           // Number of half-days included
    val isHalfDayStart: Boolean = false,  // Start date is half day
    val isHalfDayEnd: Boolean = false,    // End date is half day
    val status: VacationStatus = VacationStatus.PENDING,
    val requestDate: Long = System.currentTimeMillis(),
    val observations: String = ""
) {
    /** Total effective days considering half-days and specific hours */
    val effectiveDays: Double
        get() = if (hoursRequested > 0) {
            hoursRequested / 8.0
        } else {
            daysRequested - (halfDays * 0.5)
        }
}

enum class VacationStatus {
    PENDING, APPROVED, REJECTED
}
