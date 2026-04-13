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
    val overtimeHours: Double = 0.0,       // Horas extras
    val overtimeRate: Double = 2.0,        // Multiplicador (200% = 2.0)
    val hourlyRateAtTime: Double = 0.0,
    val totalPay: Double = (hoursWorked * hourlyRateAtTime) + (overtimeHours * hourlyRateAtTime * overtimeRate),
    val observations: String = ""
)
