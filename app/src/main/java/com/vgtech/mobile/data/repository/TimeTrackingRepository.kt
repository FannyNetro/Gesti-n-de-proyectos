package com.vgtech.mobile.data.repository

import com.vgtech.mobile.data.model.WorkLog
import com.vgtech.mobile.network.RetrofitClient
import com.vgtech.mobile.network.dto.CreateRegistroHorasDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TimeTrackingRepository {
    private val api = RetrofitClient.api

    fun getWorkLogs(empleadoUid: String? = null): Flow<List<WorkLog>> = flow {
        try {
            val response = api.getRegistroHoras(empleadoUid)
            if (response.isSuccessful) {
                val dtos = response.body() ?: emptyList()
                val workLogs = dtos.map { dto ->
                    WorkLog(
                        id = dto.id,
                        employeeUid = dto.empleadoUid,
                        date = try { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(dto.fecha)?.time ?: System.currentTimeMillis() } catch (e: Exception) { System.currentTimeMillis() },
                        hoursWorked = dto.horasTrabajadas,
                        overtimeHours = dto.horasExtra,
                        overtimeRate = dto.tarifaExtra,
                        hourlyRateAtTime = dto.tarifaHora,
                        totalPay = dto.pagoTotal,
                        observations = dto.observaciones
                    )
                }
                emit(workLogs)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    suspend fun addWorkLog(
        empleadoUid: String,
        fecha: String,
        horasTrabajadas: Double,
        horasExtra: Double,
        tarifaExtra: Double,
        tarifaHora: Double,
        observaciones: String
    ): Result<Unit> {
        return try {
            val dto = CreateRegistroHorasDto(
                empleadoUid = empleadoUid,
                fecha = fecha,
                horasTrabajadas = horasTrabajadas,
                horasExtra = horasExtra,
                tarifaExtra = tarifaExtra,
                tarifaHora = tarifaHora,
                observaciones = observaciones
            )
            val response = api.createRegistroHoras(dto)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error saving work log: ${response.code()}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
