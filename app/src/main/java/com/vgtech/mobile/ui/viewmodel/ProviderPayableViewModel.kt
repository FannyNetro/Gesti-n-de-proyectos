package com.vgtech.mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vgtech.mobile.data.model.Employee
import com.vgtech.mobile.data.model.ProviderAccountSummary
import com.vgtech.mobile.data.model.ProviderTransaction
import com.vgtech.mobile.data.model.TransactionType
import com.vgtech.mobile.data.repository.EmployeeRepository
import com.vgtech.mobile.data.repository.ProviderPayableRepository
import com.vgtech.mobile.data.repository.ProviderTxDb
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProviderPayableViewModel(application: Application) : AndroidViewModel(application) {

    private val employeeRepository = EmployeeRepository()
    private val transactionRepository = ProviderPayableRepository()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Resumen de las cuentas de todos los proveedores
    private val _providerSummaries = MutableStateFlow<List<ProviderAccountSummary>>(emptyList())
    val providerSummaries: StateFlow<List<ProviderAccountSummary>> = _providerSummaries.asStateFlow()

    // All transactions for the global history view
    private val _allTransactions = MutableStateFlow<List<ProviderTransaction>>(emptyList())
    val allTransactions: StateFlow<List<ProviderTransaction>> = _allTransactions.asStateFlow()

    // Map from providerId -> providerName for history display
    private val _providerNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val providerNames: StateFlow<Map<String, String>> = _providerNames.asStateFlow()

    init {
        loadProvidersAndComputeSummaries()
        observeAllTransactions()
    }

    private fun observeAllTransactions() {
        viewModelScope.launch {
            ProviderTxDb._transactions.collect { list ->
                _allTransactions.value = list.sortedByDescending { it.timestamp }
            }
        }
    }

    private fun loadProvidersAndComputeSummaries() {
        viewModelScope.launch {
            _loading.value = true
            try {
                employeeRepository.getEmployees().collect { allEmployees ->
                    val providers = allEmployees.filter { it.puesto.equals("Proveedor", ignoreCase = true) }

                    // Build name map for history lookups
                    _providerNames.value = providers.associate { it.uid to it.nombreCompleto }

                    calculateSummaries(providers)
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun calculateSummaries(providers: List<Employee>) {
        for (provider in providers) {
            transactionRepository.getTransactionsForProvider(provider.uid).collect { transactions ->
                val totalEarned = transactions.filter { it.type == TransactionType.SERVICE }.sumOf { it.providerCut }
                val totalPaid   = transactions.filter { it.type == TransactionType.PAYMENT }.sumOf { it.rawAmount }
                val totalProfit = transactions.filter { it.type == TransactionType.SERVICE }.sumOf { it.companyCut }
                val pending     = totalEarned - totalPaid

                val summary = ProviderAccountSummary(
                    providerId           = provider.uid,
                    providerName         = provider.nombreCompleto,
                    totalServiceAmountEarned = totalEarned,
                    totalAmountPaid      = totalPaid,
                    pendingBalance       = pending,
                    totalCompanyProfit   = totalProfit
                )

                val currentList = _providerSummaries.value.toMutableList()
                val index = currentList.indexOfFirst { it.providerId == provider.uid }
                if (index != -1) currentList[index] = summary else currentList.add(summary)
                currentList.sortBy { it.providerName }
                _providerSummaries.value = currentList
            }
        }
    }

    fun registerService(providerId: String, clientPayment: Double, description: String) {
        viewModelScope.launch {
            try { transactionRepository.addServiceTransaction(providerId, clientPayment, description) }
            catch (e: Exception) { _error.value = "Error al registrar servicio: ${e.message}" }
        }
    }

    fun registerPayment(providerId: String, amountPaid: Double, description: String) {
        viewModelScope.launch {
            try { transactionRepository.addPaymentTransaction(providerId, amountPaid, description) }
            catch (e: Exception) { _error.value = "Error al registrar abono: ${e.message}" }
        }
    }

    fun clearError() { _error.value = null }
}
