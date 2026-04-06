package com.vgtech.mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vgtech.mobile.data.model.UserRole
import com.vgtech.mobile.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * AuthViewModel — manages login flow, session checks, and role routing.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()

    // ── UI State ─────────────────────────────────────────────────────

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // ── Session Check (Auth Guard) ───────────────────────────────────

    /**
     * Called from LaunchedEffect at app startup.
     * If there's an existing session, resolves the role and emits Success.
     */
    fun checkSession() {
        val user = authRepository.currentUser
        if (user == null) {
            _authState.value = AuthState.Unauthenticated
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val role = authRepository.getUserRole(user.uid)
                _authState.value = AuthState.Success(role)
            } catch (e: Exception) {
                // Session exists but profile missing — force logout
                authRepository.signOut()
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    // ── Sign In ──────────────────────────────────────────────────────

    fun signIn(email: String, password: String) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            _authState.value = AuthState.Error("Por favor ingresa correo y contraseña")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _authState.value = AuthState.Error("Formato de correo electrónico inválido")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val role = authRepository.signIn(trimmedEmail, trimmedPassword)
                _authState.value = AuthState.Success(role)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.localizedMessage ?: "Error al iniciar sesión"
                )
            }
        }
    }

    // ── Sign Up (First account / self-registration) ──────────────────

    fun signUp(nombre: String, email: String, password: String, puesto: String) {
        if (nombre.isBlank()) {
            _authState.value = AuthState.Error("El nombre es obligatorio")
            return
        }
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            _authState.value = AuthState.Error("Ingresa un correo electrónico válido")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }
        if (puesto.isBlank()) {
            _authState.value = AuthState.Error("Selecciona un puesto")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val employee = com.vgtech.mobile.data.model.Employee(
                    nombreCompleto = nombre,
                    email = trimmedEmail,
                    password = password,
                    puesto = puesto
                )
                val role = authRepository.signUp(employee)
                _authState.value = AuthState.Success(role)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.localizedMessage ?: "Error al crear la cuenta"
                )
            }
        }
    }

    // ── Sign Out ─────────────────────────────────────────────────────

    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    // ── Reset error to allow retry ───────────────────────────────────

    fun clearError() {
        _authState.value = AuthState.Idle
    }
}

/**
 * Sealed hierarchy for authentication UI states.
 */
sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Success(val role: UserRole) : AuthState()
    data class Error(val message: String) : AuthState()
}
