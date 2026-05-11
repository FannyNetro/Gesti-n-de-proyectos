package com.vgtech.mobile.data.repository

import com.vgtech.mobile.data.model.*
import com.vgtech.mobile.network.RetrofitClient
import com.vgtech.mobile.network.dto.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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

        val req = CreateTransaccionDto(
            proveedorId = providerId,
            proyectoId = projectId,
            tipo = "Servicio",
            montoBruto = clientPayment,
            corteEmpresa = companyCut,
            corteProveedor = providerCut,
            descripcion = description
        )
        val response = RetrofitClient.api.createTransaccion(req)
        if (!response.isSuccessful) throw Exception("Error al crear transacción")
        return response.body()?.get("id") ?: ""
    }

    suspend fun addPaymentTransaction(
        providerId: String, 
        amountPaid: Double, 
        description: String,
        projectId: String? = null,
        phaseId: String? = null
    ): String {
        val req = CreateTransaccionDto(
            proveedorId = providerId,
            proyectoId = projectId,
            faseId = phaseId,
            tipo = "Pago",
            montoBruto = amountPaid,
            corteEmpresa = 0.0,
            corteProveedor = 0.0,
            descripcion = description
        )
        val response = RetrofitClient.api.createTransaccion(req)
        if (!response.isSuccessful) throw Exception("Error al crear transacción")
        
        if (phaseId != null) {
            RetrofitClient.api.marcarFasePagada(phaseId)
        }
        return response.body()?.get("id") ?: ""
    }

    fun getAllTransactions(): Flow<List<ProviderTransaction>> = flow {
        val response = RetrofitClient.api.getTransacciones()
        if (response.isSuccessful) {
            val list = response.body()?.map { dto ->
                ProviderTransaction(
                    id = dto.id,
                    providerId = dto.proveedorId,
                    projectId = dto.proyectoId,
                    phaseId = dto.faseId,
                    type = if (dto.tipo == "Servicio") TransactionType.SERVICE else TransactionType.PAYMENT,
                    rawAmount = dto.montoBruto,
                    companyCut = dto.corteEmpresa,
                    providerCut = dto.corteProveedor,
                    description = dto.descripcion,
                    timestamp = dto.timestamp
                )
            } ?: emptyList()
            emit(list.sortedByDescending { it.timestamp })
        } else {
            emit(emptyList())
        }
    }

    fun getAllPhases(): Flow<List<PaymentPhase>> = flow {
        val response = RetrofitClient.api.getFasesPago()
        if (response.isSuccessful) {
            val list = response.body()?.map { dto ->
                PaymentPhase(
                    id = dto.id,
                    providerId = dto.proveedorId,
                    projectId = dto.proyectoId,
                    phaseNumber = dto.numeroFase,
                    totalPhases = dto.totalFases,
                    amountToPay = dto.montoAPagar,
                    scheduledDate = dto.fechaProgramada,
                    status = if (dto.estado == "PAGADO") PaymentPhaseStatus.PAGADO else PaymentPhaseStatus.PENDIENTE,
                    paidDate = dto.fechaPago
                )
            } ?: emptyList()
            emit(list.sortedBy { it.scheduledDate })
        } else {
            emit(emptyList())
        }
    }

    suspend fun createPaymentPhase(phase: PaymentPhase) {
        val req = CreateFasePagoDto(
            proveedorId = phase.providerId,
            proyectoId = phase.projectId,
            numeroFase = phase.phaseNumber,
            totalFases = phase.totalPhases,
            montoAPagar = phase.amountToPay,
            fechaProgramada = phase.scheduledDate
        )
        val res = RetrofitClient.api.createFasePago(req)
        if (!res.isSuccessful) throw Exception("Error al crear fase de pago")
    }

    suspend fun deletePaymentPhase(phaseId: String) {
        val res = RetrofitClient.api.deleteFasePago(phaseId)
        if (!res.isSuccessful) throw Exception("Error al eliminar fase de pago")
    }
}
