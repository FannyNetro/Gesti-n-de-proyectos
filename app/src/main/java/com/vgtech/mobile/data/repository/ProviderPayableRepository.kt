package com.vgtech.mobile.data.repository

import com.vgtech.mobile.data.model.ProviderTransaction
import com.vgtech.mobile.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory Mock DB for transactions — pre-loaded with seed data
 */
object ProviderTxDb {
    val _transactions = MutableStateFlow<List<ProviderTransaction>>(buildSeedData())

    private fun buildSeedData(): List<ProviderTransaction> {
        val oneDay = 86_400_000L
        val now = System.currentTimeMillis()
        return listOf(
            // ── Constructora Pérez S.A. (prov-uid) ──────────────────────
            // Proyecto: Hospital General – Fase 1 | Cliente pagó $360,000 → Prov. $180,000
            // Proyecto: Hospital General – Fase 2 | Cliente pagó $240,000 → Prov. $120,000 (anticipo $60k recibido)
            ProviderTransaction(id = "tx-01", providerId = "prov-uid", type = TransactionType.SERVICE,
                rawAmount = 360000.0, companyCut = 180000.0, providerCut = 180000.0,
                description = "Hospital General – Fase 1: Obra civil e instalaciones", timestamp = now - 90 * oneDay),
            ProviderTransaction(id = "tx-02", providerId = "prov-uid", type = TransactionType.PAYMENT,
                rawAmount = 180000.0, companyCut = 0.0, providerCut = 0.0,
                description = "Liquidación total – Hospital Fase 1", timestamp = now - 75 * oneDay),
            ProviderTransaction(id = "tx-03", providerId = "prov-uid", type = TransactionType.SERVICE,
                rawAmount = 240000.0, companyCut = 120000.0, providerCut = 120000.0,
                description = "Hospital General – Fase 2: Ampliación y oxígeno", timestamp = now - 30 * oneDay),
            ProviderTransaction(id = "tx-04", providerId = "prov-uid", type = TransactionType.PAYMENT,
                rawAmount = 60000.0, companyCut = 0.0, providerCut = 0.0,
                description = "Anticipo 50% – Hospital Fase 2", timestamp = now - 15 * oneDay),
            // Saldo pendiente Constructora Pérez: $120,000 - $60,000 = $60,000

            // ── Materiales del Norte (prov-2) ────────────────────────────
            // Proyecto: Residencial Las Lomas   | Cliente $500,000 → Prov. $250,000
            // Proyecto: Torre Corporativa Alfa  | Cliente $420,000 → Prov. $210,000 (anticipo $100k)
            ProviderTransaction(id = "tx-05", providerId = "prov-2", type = TransactionType.SERVICE,
                rawAmount = 500000.0, companyCut = 250000.0, providerCut = 250000.0,
                description = "Residencial Las Lomas – Materiales y estructura", timestamp = now - 60 * oneDay),
            ProviderTransaction(id = "tx-06", providerId = "prov-2", type = TransactionType.PAYMENT,
                rawAmount = 100000.0, companyCut = 0.0, providerCut = 0.0,
                description = "Anticipo 40% – Las Lomas", timestamp = now - 45 * oneDay),
            ProviderTransaction(id = "tx-07", providerId = "prov-2", type = TransactionType.SERVICE,
                rawAmount = 420000.0, companyCut = 210000.0, providerCut = 210000.0,
                description = "Torre Corporativa Alfa – Suministro materiales", timestamp = now - 40 * oneDay),
            ProviderTransaction(id = "tx-08", providerId = "prov-2", type = TransactionType.PAYMENT,
                rawAmount = 100000.0, companyCut = 0.0, providerCut = 0.0,
                description = "Anticipo inicial – Torre Alfa", timestamp = now - 20 * oneDay),
            // Saldo pendiente Materiales del Norte: ($250k + $210k) - ($100k + $100k) = $260,000

            // ── Electro Servicios MX (prov-3) ────────────────────────────
            // Proyecto: Plaza Comercial Sur     | Cliente $300,000 → Prov. $150,000 (LIQUIDADO)
            // Proyecto: Parque Industrial       | Cliente $280,000 → Prov. $140,000 (anticipo $60k)
            ProviderTransaction(id = "tx-09", providerId = "prov-3", type = TransactionType.SERVICE,
                rawAmount = 300000.0, companyCut = 150000.0, providerCut = 150000.0,
                description = "Plaza Comercial Sur – Instalaciones eléctricas", timestamp = now - 80 * oneDay),
            ProviderTransaction(id = "tx-10", providerId = "prov-3", type = TransactionType.PAYMENT,
                rawAmount = 150000.0, companyCut = 0.0, providerCut = 0.0,
                description = "Liquidación total – Plaza Comercial Sur", timestamp = now - 60 * oneDay),
            ProviderTransaction(id = "tx-11", providerId = "prov-3", type = TransactionType.SERVICE,
                rawAmount = 280000.0, companyCut = 140000.0, providerCut = 140000.0,
                description = "Parque Industrial Oriente – Eléctrico e iluminación", timestamp = now - 35 * oneDay),
            ProviderTransaction(id = "tx-12", providerId = "prov-3", type = TransactionType.PAYMENT,
                rawAmount = 60000.0, companyCut = 0.0, providerCut = 0.0,
                description = "Anticipo 43% – Parque Industrial", timestamp = now - 10 * oneDay),
            // Saldo pendiente Electro Servicios: $140,000 - $60,000 = $80,000

            // ── Hidráulica Integral (prov-4) ─────────────────────────────
            // Proyecto: Clínica Privada Norte   | Cliente $180,000 → Prov. $90,000 (sin abonos aún)
            ProviderTransaction(id = "tx-13", providerId = "prov-4", type = TransactionType.SERVICE,
                rawAmount = 180000.0, companyCut = 90000.0, providerCut = 90000.0,
                description = "Clínica Privada Norte – Sistema hidráulico y plomería", timestamp = now - 20 * oneDay),
            // Saldo pendiente Hidráulica: $90,000

            // ── Arquitectura & Diseño MX (prov-5) ───────────────────────
            // Proyecto: Centro Educativo Federal | Cliente $220,000 → Prov. $110,000 (LIQUIDADO)
            ProviderTransaction(id = "tx-14", providerId = "prov-5", type = TransactionType.SERVICE,
                rawAmount = 220000.0, companyCut = 110000.0, providerCut = 110000.0,
                description = "Centro Educativo Federal – Diseño y acabados", timestamp = now - 70 * oneDay),
            ProviderTransaction(id = "tx-15", providerId = "prov-5", type = TransactionType.PAYMENT,
                rawAmount = 110000.0, companyCut = 0.0, providerCut = 0.0,
                description = "Liquidación total – Centro Educativo Federal", timestamp = now - 50 * oneDay)
            // Saldo pendiente Arquitectura: $0 (AL CORRIENTE)
        )
    }
}


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
        description: String
    ): String {
        val companyCut = clientPayment * defaultCompanyShare
        val providerCut = clientPayment * defaultProviderShare

        val transaction = ProviderTransaction(
            providerId = providerId,
            type = TransactionType.SERVICE,
            rawAmount = clientPayment,
            companyCut = companyCut,
            providerCut = providerCut,
            description = description
        )

        val updatedList = ProviderTxDb._transactions.value.toMutableList()
        updatedList.add(transaction)
        ProviderTxDb._transactions.value = updatedList
        
        return transaction.id
    }

    /**
     * Registra un abono/pago de la empresa al proveedor.
     */
    suspend fun addPaymentTransaction(
        providerId: String, 
        amountPaid: Double, 
        description: String
    ): String {
        val transaction = ProviderTransaction(
            providerId = providerId,
            type = TransactionType.PAYMENT,
            rawAmount = amountPaid,
            companyCut = 0.0,
            providerCut = 0.0,
            description = description
        )

        val updatedList = ProviderTxDb._transactions.value.toMutableList()
        updatedList.add(transaction)
        ProviderTxDb._transactions.value = updatedList
        
        return transaction.id
    }

    /**
     * Obtiene el flujo de transacciones para calcular saldos en tiempo real.
     */
    fun getTransactionsForProvider(providerId: String): Flow<List<ProviderTransaction>> {
        return ProviderTxDb._transactions.map { list ->
            list.filter { it.providerId == providerId }.sortedByDescending { it.timestamp }
        }
    }
}
