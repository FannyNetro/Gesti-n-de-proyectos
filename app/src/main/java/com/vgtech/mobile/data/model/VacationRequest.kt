package com.vgtech.mobile.data.model

import java.util.UUID

/**
 * VacationRequest data class to track employee vacation and leave history.
 */
data class VacationRequest(
    val id: String = UUID.randomUUID().toString(),
    val employeeUid: String = "",
    val employeeName: String = "",
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis(),
    val daysRequested: Double = 0.0,
    val hoursRequested: Double = 0.0,
    val type: RequestType = RequestType.VACACIONES,
    val status: VacationStatus = VacationStatus.PENDING,
    val requestDate: Long = System.currentTimeMillis(),
    val observations: String = ""
) {
    /** Total effective days considering hours or full days */
    val effectiveDays: Double
        get() = if (hoursRequested > 0) {
            hoursRequested / 8.0
        } else {
            daysRequested
        }
}

enum class RequestType(val label: String) {
    VACACIONES("Vacaciones"),
    CON_GOCE("Días con goce de sueldo"),
    SIN_GOCE("Días sin goce de sueldo")
}

enum class VacationStatus {
    PENDING, APPROVED, REJECTED
}
