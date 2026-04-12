package com.vgtech.mobile.data.model

import java.util.UUID

/**
 * WorkLog data class to track hours worked by an employee.
 */
data class WorkLog(
    val id: String = UUID.randomUUID().toString(),
    val employeeUid: String = "",
    val employeeName: String = "",
    val date: Long = System.currentTimeMillis(),
    val hoursWorked: Double = 0.0,
    val hourlyRateAtTime: Double = 0.0,
    val totalPay: Double = hoursWorked * hourlyRateAtTime,
    val observations: String = ""
)
