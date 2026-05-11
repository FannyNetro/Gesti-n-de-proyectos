package com.vgtech.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vgtech.mobile.data.model.Employee
import com.vgtech.mobile.data.model.WorkLog
import com.vgtech.mobile.data.repository.EmployeeRepository
import com.vgtech.mobile.data.repository.TimeTrackingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SalaryControlViewModel : ViewModel() {
    private val employeeRepository = EmployeeRepository()
    private val timeTrackingRepository = TimeTrackingRepository()

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    private val _rawWorkLogs = MutableStateFlow<List<WorkLog>>(emptyList())

    val workLogs: StateFlow<List<WorkLog>> = combine(_employees, _rawWorkLogs) { emps, logs ->
        logs.map { log ->
            val emp = emps.find { it.uid == log.employeeUid }
            log.copy(employeeName = emp?.nombreCompleto ?: "Desconocido")
        }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, emptyList())

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            employeeRepository.getEmployees().collect {
                _employees.value = it
            }
        }
        viewModelScope.launch {
            timeTrackingRepository.getWorkLogs().collect {
                _rawWorkLogs.value = it
            }
        }
    }

    fun addWorkLog(
        empleadoUid: String,
        fecha: String,
        horasTrabajadas: Double,
        horasExtra: Double,
        tarifaExtra: Double,
        tarifaHora: Double,
        observaciones: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = timeTrackingRepository.addWorkLog(
                empleadoUid, fecha, horasTrabajadas, horasExtra, tarifaExtra, tarifaHora, observaciones
            )
            if (result.isSuccess) {
                // Refresh work logs
                timeTrackingRepository.getWorkLogs().collect {
                    _rawWorkLogs.value = it
                }
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Error saving work log")
            }
        }
    }

    fun updateEmployeeRates(
        uid: String,
        sueldo: Double,
        hourlyRate: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val employee = _employees.value.find { it.uid == uid }
            if (employee != null) {
                try {
                    employeeRepository.updateEmployee(
                        employee.copy(
                            sueldo = sueldo,
                            pagoPorHora = hourlyRate
                        )
                    )
                    // Refresh employees
                    employeeRepository.getEmployees().collect {
                        _employees.value = it
                    }
                    onSuccess()
                } catch (e: Exception) {
                    onError(e.message ?: "Failed to update employee rates")
                }
            }
        }
    }
}
