package com.vgtech.mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vgtech.mobile.data.model.Employee
import com.vgtech.mobile.data.repository.AuthRepository
import com.vgtech.mobile.data.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * EmployeeViewModel — manages employee list, registration form, and CRUD.
 */
class EmployeeViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()
    private val employeeRepository = EmployeeRepository()

    // ── Employee List State ──────────────────────────────────────────

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _listError = MutableStateFlow<String?>(null)
    val listError: StateFlow<String?> = _listError.asStateFlow()

    // ── Registration State ───────────────────────────────────────────

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    // ── Stats ────────────────────────────────────────────────────────

    private val _employeeCount = MutableStateFlow(0)
    val employeeCount: StateFlow<Int> = _employeeCount.asStateFlow()

    // ── Init: start listening to Firestore ────────────────────────────

    init {
        loadEmployees()
    }

    fun loadEmployees() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                employeeRepository.getEmployees().collect { list ->
                    _employees.value = list
                    _employeeCount.value = list.size
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _listError.value = e.localizedMessage ?: "Error al cargar empleados"
                _isLoading.value = false
            }
        }
    }

    // ── Register Employee ────────────────────────────────────────────

    fun registerEmployee(employee: Employee) {
        // Validation
        if (employee.nombreCompleto.isBlank()) {
            _registrationState.value = RegistrationState.Error("El nombre es obligatorio")
            return
        }
        if (employee.email.isBlank() || !employee.email.contains("@")) {
            _registrationState.value = RegistrationState.Error("Ingresa un correo electrónico válido")
            return
        }
        if (employee.password.length < 6) {
            _registrationState.value = RegistrationState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }
        if (employee.direccion.isBlank()) {
            _registrationState.value = RegistrationState.Error("La dirección es obligatoria")
            return
        }
        if (employee.telefono.isBlank()) {
            _registrationState.value = RegistrationState.Error("El teléfono es obligatorio")
            return
        }
        if (employee.puesto.isBlank()) {
            _registrationState.value = RegistrationState.Error("Selecciona un puesto")
            return
        }
        
        // Proveedores don't strictly need sueldo/vacaciones
        if (employee.puesto.lowercase() != "proveedor") {
            if (employee.sueldo <= 0) {
                _registrationState.value = RegistrationState.Error("El sueldo debe ser mayor a 0")
                return
            }
        }
        
        if (employee.puesto.lowercase() == "proveedor" && employee.tipoTrabajo.isEmpty()) {
            _registrationState.value = RegistrationState.Error("Selecciona al menos un tipo de trabajo para el proveedor")
            return
        }

        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                val uid = authRepository.registerEmployee(
                    context = getApplication(),
                    employee = employee
                )
                _registrationState.value = RegistrationState.Success(uid)
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(
                    e.localizedMessage ?: "Error al registrar empleado"
                )
            }
        }
    }

    // ── Update Employee ──────────────────────────────────────────────

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                employeeRepository.updateEmployee(employee)
            } catch (e: Exception) {
                _listError.value = e.localizedMessage ?: "Error al actualizar"
            }
        }
    }

    // ── Deactivate (Fire) Employee ───────────────────────────────────

    fun deactivateEmployee(uid: String, motivo: String) {
        viewModelScope.launch {
            try {
                employeeRepository.deactivateEmployee(uid, motivo)
            } catch (e: Exception) {
                _listError.value = e.localizedMessage ?: "Error al despedir empleado"
            }
        }
    }

    // ── Reset Registration State ─────────────────────────────────────

    fun resetRegistration() {
        _registrationState.value = RegistrationState.Idle
    }

    fun clearListError() {
        _listError.value = null
    }
}

sealed class RegistrationState {
    data object Idle : RegistrationState()
    data object Loading : RegistrationState()
    data class Success(val uid: String) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}
