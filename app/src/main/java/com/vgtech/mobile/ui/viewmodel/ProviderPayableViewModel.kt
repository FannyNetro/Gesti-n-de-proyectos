package com.vgtech.mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vgtech.mobile.data.local.InternalDb
import com.vgtech.mobile.data.model.*
import com.vgtech.mobile.data.repository.EmployeeRepository
import com.vgtech.mobile.data.repository.ProviderPayableRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProviderPayableViewModel(application: Application) : AndroidViewModel(application) {

    private val employeeRepository = EmployeeRepository()
    private val transactionRepository = ProviderPayableRepository()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _providerSummaries = MutableStateFlow<List<ProviderAccountSummary>>(emptyList())
    val providerSummaries: StateFlow<List<ProviderAccountSummary>> = _providerSummaries.asStateFlow()

    private val _allTransactions = MutableStateFlow<List<ProviderTransaction>>(emptyList())
    val allTransactions: StateFlow<List<ProviderTransaction>> = _allTransactions.asStateFlow()

    private val _providerPhases = MutableStateFlow<Map<String, List<PaymentPhase>>>(emptyMap())
    val providerPhases: StateFlow<Map<String, List<PaymentPhase>>> = _providerPhases.asStateFlow()

    private val _providerNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val providerNames: StateFlow<Map<String, String>> = _providerNames.asStateFlow()

    private val _allProjects = MutableStateFlow<List<Project>>(emptyList())
    val allProjects: StateFlow<List<Project>> = _allProjects.asStateFlow()

    init {
        loadData()
        observeAllTransactions()
        observeProjects()
    }

    private fun observeProjects() {
        viewModelScope.launch {
            InternalDb.projects.collect { _allProjects.value = it }
        }
    }

    private fun observeAllTransactions() {
        viewModelScope.launch {
            InternalDb.providerTransactions.collect { list ->
                _allTransactions.value = list.sortedByDescending { it.timestamp }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _loading.value = true
            try {
                combine(
                    employeeRepository.getEmployees(),
                    InternalDb.providerTransactions,
                    InternalDb.paymentPhases
                ) { employees, transactions, phases ->
                    Triple(employees, transactions, phases)
                }.collect { (allEmployees, allTransactions, allPhases) ->
                    val providers = allEmployees.filter { it.puesto.equals("Proveedor", ignoreCase = true) }
                    _providerNames.value = providers.associate { it.uid to it.nombreCompleto }
                    
                    val phaseMap = allPhases.groupBy { it.providerId }
                    _providerPhases.value = phaseMap

                    val summaries = providers.map { provider ->
                        val providerTxs = allTransactions.filter { it.providerId == provider.uid }
                        val providerPhases = phaseMap[provider.uid] ?: emptyList()

                        val totalEarned = providerTxs.filter { it.type == TransactionType.SERVICE }.sumOf { it.providerCut }
                        val totalPaid   = providerTxs.filter { it.type == TransactionType.PAYMENT }.sumOf { it.rawAmount }
                        val totalProfit = providerTxs.filter { it.type == TransactionType.SERVICE }.sumOf { it.companyCut }
                        val pending     = totalEarned - totalPaid

                        val status = when {
                            pending <= 0.01 && (providerPhases.isEmpty() || providerPhases.all { it.status == PaymentPhaseStatus.PAGADO }) -> AccountStatus.LIBERADO
                            totalPaid > 0 || providerPhases.any { it.status == PaymentPhaseStatus.PAGADO } -> AccountStatus.EN_PROCESO
                            else -> AccountStatus.PENDIENTE
                        }

                        ProviderAccountSummary(
                            providerId           = provider.uid,
                            providerName         = provider.nombreCompleto,
                            totalServiceAmountEarned = totalEarned,
                            totalAmountPaid      = totalPaid,
                            pendingBalance       = pending,
                            totalCompanyProfit   = totalProfit,
                            accountStatus        = status
                        )
                    }
                    _providerSummaries.value = summaries.sortedBy { it.providerName }
                    _loading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _loading.value = false
            }
        }
    }

    fun registerService(providerId: String, clientPayment: Double, description: String, projectId: String? = null) {
        viewModelScope.launch {
            try { transactionRepository.addServiceTransaction(providerId, clientPayment, description, projectId) }
            catch (e: Exception) { _error.value = "Error al registrar servicio: ${e.message}" }
        }
    }

    fun registerPayment(providerId: String, amountPaid: Double, description: String, projectId: String? = null, phaseId: String? = null) {
        viewModelScope.launch {
            try { transactionRepository.addPaymentTransaction(providerId, amountPaid, description, projectId, phaseId) }
            catch (e: Exception) { _error.value = "Error al registrar abono: ${e.message}" }
        }
    }

    fun addPaymentPhase(phase: PaymentPhase) {
        viewModelScope.launch {
            try { transactionRepository.createPaymentPhase(phase) }
            catch (e: Exception) { _error.value = "Error al crear fase: ${e.message}" }
        }
    }

    fun removePaymentPhase(phaseId: String) {
        viewModelScope.launch {
            try { transactionRepository.deletePaymentPhase(phaseId) }
            catch (e: Exception) { _error.value = "Error al eliminar fase: ${e.message}" }
        }
    }

    fun clearError() { _error.value = null }
}
