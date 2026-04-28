package com.vgtech.mobile.data.repository

import com.vgtech.mobile.data.local.InternalDb
import com.vgtech.mobile.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProviderPayableRepository {

    // Default business rule 50/50 split
    private val defaultCompanyShare = 0.5
    private val defaultProviderShare = 0.5

    /**
     * Registra el ingreso de un servicio facturado a un cliente.
     * Divide automáticamente la ganancia.
     */
    suspend fun addServiceTransaction(
        providerId: String, 
        clientPayment: Double, 
        description: String,
        projectId: String? = null
    ): String {
        val companyCut = clientPayment * defaultCompanyShare
        val providerCut = clientPayment * defaultProviderShare

        val transaction = ProviderTransaction(
            providerId = providerId,
            projectId = projectId,
            type = TransactionType.SERVICE,
            rawAmount = clientPayment,
            companyCut = companyCut,
            providerCut = providerCut,
            description = description
        )

        InternalDb.addProviderTransaction(transaction)
        return transaction.id
    }

    /**
     * Registra un abono/pago de la empresa al proveedor.
     */
    suspend fun addPaymentTransaction(
        providerId: String, 
        amountPaid: Double, 
        description: String,
        projectId: String? = null,
        phaseId: String? = null
    ): String {
        val transaction = ProviderTransaction(
            providerId = providerId,
            projectId = projectId,
            phaseId = phaseId,
            type = TransactionType.PAYMENT,
            rawAmount = amountPaid,
            companyCut = 0.0,
            providerCut = 0.0,
            description = description
        )

        InternalDb.addProviderTransaction(transaction)

        // Update phase status if applicable
        if (phaseId != null) {
            val phase = InternalDb.paymentPhases.value.find { it.id == phaseId }
            if (phase != null) {
                InternalDb.updatePaymentPhase(phase.copy(status = PaymentPhaseStatus.PAGADO, paidDate = System.currentTimeMillis()))
            }
        }
        
        return transaction.id
    }

    /**
     * Obtiene el flujo de transacciones para calcular saldos en tiempo real.
     */
    fun getTransactionsForProvider(providerId: String): Flow<List<ProviderTransaction>> {
        return InternalDb.providerTransactions.map { list ->
            list.filter { it.providerId == providerId }.sortedByDescending { it.timestamp }
        }
    }

    fun getPhasesForProvider(providerId: String): Flow<List<PaymentPhase>> {
        return InternalDb.paymentPhases.map { list ->
            list.filter { it.providerId == providerId }.sortedBy { it.scheduledDate }
        }
    }

    suspend fun createPaymentPhase(phase: PaymentPhase) {
        InternalDb.addPaymentPhase(phase)
    }

    suspend fun deletePaymentPhase(phaseId: String) {
        InternalDb.deletePaymentPhase(phaseId)
    }
}
