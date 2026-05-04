package com.vgtech.mobile.ui.screens.rh

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vgtech.mobile.data.model.*
import com.vgtech.mobile.ui.theme.*
import com.vgtech.mobile.ui.viewmodel.ProviderPayableViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderPayableScreen(
    viewModel: ProviderPayableViewModel = viewModel(),
    canManagePhases: Boolean = true,
    canRegisterPayments: Boolean = true
) {
    val loading by viewModel.loading.collectAsState()
    val summaries by viewModel.providerSummaries.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val providerPhases by viewModel.providerPhases.collectAsState()
    val providerNames by viewModel.providerNames.collectAsState()
    val error by viewModel.error.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Por Pagar", "Pagadas", "Historial", "Inmobiliaria")

    var showServiceDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showPhaseManagement by remember { mutableStateOf(false) }
    var selectedProvider by remember { mutableStateOf<ProviderAccountSummary?>(null) }
    var targetPhase by remember { mutableStateOf<PaymentPhase?>(null) }

    if (error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Aviso") },
            text = { Text(error ?: "") },
            confirmButton = { TextButton(onClick = { viewModel.clearError() }) { Text("OK") } }
        )
    }

    // Dialog for "Cliente Pagó" (Service Revenue)
    if (showServiceDialog && selectedProvider != null) {
        TransactionDialog(
            providerName = selectedProvider!!.providerName,
            isPayment = false,
            onDismiss = { showServiceDialog = false; selectedProvider = null },
            onConfirm = { amount, desc ->
                viewModel.registerService(selectedProvider!!.providerId, amount, desc)
                showServiceDialog = false; selectedProvider = null
            }
        )
    }

    // Dialog for "Abonar" (Provider Payment)
    if (showPaymentDialog && selectedProvider != null) {
        val nextPhase = providerPhases[selectedProvider!!.providerId]?.find { it.status == PaymentPhaseStatus.PENDIENTE }
        PaymentDialog(
            summary = selectedProvider!!,
            suggestedPhase = nextPhase,
            onDismiss = { showPaymentDialog = false; selectedProvider = null },
            onConfirm = { amount, desc, phaseId ->
                viewModel.registerPayment(
                    providerId = selectedProvider!!.providerId,
                    amountPaid = amount,
                    description = desc,
                    phaseId = phaseId
                )
                showPaymentDialog = false; selectedProvider = null
            }
        )
    }

    if (showPhaseManagement && selectedProvider != null) {
        ManagePhasesDialog(
            provider = selectedProvider!!,
            phases = providerPhases[selectedProvider!!.providerId] ?: emptyList(),
            availableProjects = allProjects.filter { it.providerUid == selectedProvider!!.providerId },
            onDismiss = { showPhaseManagement = false; selectedProvider = null },
            onAddPhase = { phase -> viewModel.addPaymentPhase(phase) },
            onDeletePhase = { phaseId -> viewModel.removePaymentPhase(phaseId) }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F6FB))) {
        KpiHeaderStrip(summaries = summaries)

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = Navy,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 3.dp,
                    color = Teal
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        if (loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Teal)
        }

        when (selectedTab) {
            0 -> PendingTab(
                summaries = summaries.filter { it.accountStatus != AccountStatus.LIBERADO },
                phases = providerPhases,
                canManagePhases = canManagePhases,
                canRegisterPayments = canRegisterPayments,
                onAddService = { summary -> selectedProvider = summary; showServiceDialog = true },
                onAddPayment = { summary -> selectedProvider = summary; showPaymentDialog = true },
                onManagePhases = { summary -> selectedProvider = summary; showPhaseManagement = true },
                onPayPhase = { summary, phase ->
                    viewModel.registerPayment(
                        providerId = summary.providerId,
                        amountPaid = phase.amountToPay,
                        description = "Pago de Fase ${phase.phaseNumber}/${phase.totalPhases}",
                        projectId = phase.projectId,
                        phaseId = phase.id
                    )
                }
            )
            1 -> PaidTab(summaries = summaries.filter { it.accountStatus == AccountStatus.LIBERADO })
            2 -> HistoryTab(transactions = allTransactions, providerNames = providerNames)
            3 -> InmobiliariaTab(summaries = summaries)
        }
    }
}

@Composable
private fun KpiHeaderStrip(summaries: List<ProviderAccountSummary>) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val totalDebt    = summaries.sumOf { it.pendingBalance }
    val totalPaid    = summaries.sumOf { it.totalAmountPaid }
    val totalProfit  = summaries.sumOf { it.totalCompanyProfit }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Navy, Color(0xFF1A3558))))
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            KpiChip(label = "Deuda Total", value = fmt.format(totalDebt), isAlert = totalDebt > 0)
            KpiDivider()
            KpiChip(label = "Total Pagado", value = fmt.format(totalPaid), isAlert = false)
            KpiDivider()
            KpiChip(label = "Utilidad Empresa", value = fmt.format(totalProfit), isAlert = false)
        }
    }
}

@Composable
private fun KpiChip(label: String, value: String, isAlert: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.65f))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            color = if (isAlert) Color(0xFFFF7676) else Color(0xFF5FFFCA),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun KpiDivider() {
    Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color.White.copy(alpha = 0.15f)))
}

@Composable
private fun PendingTab(
    summaries: List<ProviderAccountSummary>,
    phases: Map<String, List<PaymentPhase>>,
    canManagePhases: Boolean,
    canRegisterPayments: Boolean,
    onAddService: (ProviderAccountSummary) -> Unit,
    onAddPayment: (ProviderAccountSummary) -> Unit,
    onManagePhases: (ProviderAccountSummary) -> Unit,
    onPayPhase: (ProviderAccountSummary, PaymentPhase) -> Unit
) {
    if (summaries.isEmpty()) {
        EmptyState(icon = Icons.Default.CheckCircle, message = "¡Sin deudas pendientes! 🎉")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(summaries, key = { it.providerId }) { summary ->
            ProviderAccountCard(
                summary = summary,
                phases = phases[summary.providerId] ?: emptyList(),
                canManagePhases = canManagePhases,
                canRegisterPayments = canRegisterPayments,
                onAddService = { onAddService(summary) },
                onAddPayment = { onAddPayment(summary) },
                onManagePhases = { onManagePhases(summary) },
                onPayPhase = { onPayPhase(summary, it) }
            )
        }
    }
}

@Composable
private fun PaidTab(summaries: List<ProviderAccountSummary>) {
    if (summaries.isEmpty()) {
        EmptyState(icon = Icons.Default.HourglassEmpty, message = "Aún no hay cuentas liquidadas")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(summaries, key = { it.providerId }) { summary ->
            ProviderAccountCard(
                summary = summary,
                phases = emptyList(),
                canManagePhases = false,
                canRegisterPayments = false,
                onAddService = {},
                onAddPayment = {},
                onManagePhases = {},
                onPayPhase = { _ -> }
            )
        }
    }
}

@Composable
private fun HistoryTab(
    transactions: List<ProviderTransaction>,
    providerNames: Map<String, String>
) {
    if (transactions.isEmpty()) {
        EmptyState(icon = Icons.Default.History, message = "Sin movimientos registrados")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(transactions, key = { it.id }) { tx ->
            HistoryRow(tx = tx, providerName = providerNames[tx.providerId] ?: tx.providerId)
        }
    }
}

@Composable
private fun HistoryRow(tx: ProviderTransaction, providerName: String) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val dateFmt = SimpleDateFormat("dd MMM yyyy", Locale("es", "MX"))
    val isService = tx.type == TransactionType.SERVICE
    val accentColor = if (isService) Color(0xFF5C6BC0) else Teal
    val bgColor = if (isService) Color(0xFF5C6BC0).copy(alpha = 0.08f) else Teal.copy(alpha = 0.08f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isService) Icons.Default.Receipt else Icons.Default.Payments,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = providerName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Navy)
                Text(
                    text = tx.description.ifBlank { if (isService) "Servicio registrado" else "Abono registrado" },
                    style = MaterialTheme.typography.bodySmall, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(text = dateFmt.format(Date(tx.timestamp)), style = MaterialTheme.typography.labelSmall, color = TextMuted.copy(alpha = 0.7f))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                val label = if (isService) "Cliente pagó" else "Abono"
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = accentColor)
                Text(text = fmt.format(tx.rawAmount), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.bodyMedium, color = accentColor)
                if (isService) {
                    Text(text = "Prov: ${fmt.format(tx.providerCut)}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun ProviderAccountCard(
    summary: ProviderAccountSummary,
    phases: List<PaymentPhase>,
    canManagePhases: Boolean,
    canRegisterPayments: Boolean,
    onAddService: () -> Unit,
    onAddPayment: () -> Unit,
    onManagePhases: () -> Unit,
    onPayPhase: (PaymentPhase) -> Unit
) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val balanceColor by animateColorAsState(
        targetValue = when(summary.accountStatus) {
            AccountStatus.LIBERADO -> Teal
            AccountStatus.EN_PROCESO -> WarningAmber
            else -> Color(0xFFE53935)
        },
        animationSpec = tween(400), label = "balance_color"
    )

    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Navy.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = Navy, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = summary.providerName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = Navy, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                StatusBadge(status = summary.accountStatus)
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FinancialColumn(label = "Total Generado", amount = fmt.format(summary.totalServiceAmountEarned), color = Color(0xFF5C6BC0))
                FinancialColumn(label = "Total Pagado", amount = fmt.format(summary.totalAmountPaid), color = Teal)
                FinancialColumn(label = "Utilidad Empresa", amount = fmt.format(summary.totalCompanyProfit), color = Color(0xFF43A047))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(balanceColor.copy(alpha = 0.08f)).padding(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (summary.pendingBalance > 0) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null, tint = balanceColor, modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Saldo Pendiente", fontWeight = FontWeight.SemiBold, color = balanceColor)
                    }
                    Text(text = fmt.format(summary.pendingBalance), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium, color = balanceColor)
                }
            }

            if (phases.isNotEmpty() || canManagePhases) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Fases de Pago Programadas", style = MaterialTheme.typography.labelMedium, color = Navy, fontWeight = FontWeight.Bold)
                    
                    if (canManagePhases) {
                        TextButton(onClick = onManagePhases, contentPadding = PaddingValues(horizontal = 8.dp)) {
                            Icon(Icons.Default.Settings, null, modifier = Modifier.size(14.dp), tint = Navy)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gestionar Fases", style = MaterialTheme.typography.labelSmall, color = Navy)
                        }
                    }
                }

                if (phases.isEmpty()) {
                    Text("No hay fases configuradas", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    phases.forEach { phase ->
                        PhaseItem(phase = phase, canPay = canRegisterPayments, onPay = { onPayPhase(phase) })
                    }
                }
            }

            if (summary.accountStatus != AccountStatus.LIBERADO && canRegisterPayments) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onAddService, modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5C6BC0)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF5C6BC0)),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.AddCard, null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Abono Cliente", style = MaterialTheme.typography.labelSmall)
                    }
                    Button(
                        onClick = onAddPayment, modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Teal),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.AttachMoney, null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pagar Proveedor", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: AccountStatus) {
    val (text, color, bg) = when(status) {
        AccountStatus.PENDIENTE -> Triple("Pendiente", Color(0xFFE53935), Color(0xFFFFEBEE))
        AccountStatus.EN_PROCESO -> Triple("En Proceso", WarningAmber, WarningAmber.copy(alpha = 0.1f))
        AccountStatus.LIBERADO -> Triple("Liberado", Color(0xFF2E7D32), Color(0xFFE8F5E9))
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Text(text = text, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun PhaseItem(phase: PaymentPhase, canPay: Boolean, onPay: () -> Unit) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))
    val isPaid = phase.status == PaymentPhaseStatus.PAGADO

    val now = System.currentTimeMillis()
    val daysUntil = (phase.scheduledDate - now) / 86400000L
    val isNear = daysUntil <= 5

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceGray.copy(alpha = 0.5f)).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Fase ${phase.phaseNumber} de ${phase.totalPhases}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Navy)
            Text("Programado: ${dateFmt.format(Date(phase.scheduledDate))}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
        Text(fmt.format(phase.amountToPay), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.ExtraBold, color = if (isPaid) Teal else Navy, modifier = Modifier.padding(horizontal = 8.dp))
        
        if (isPaid) {
            Icon(Icons.Default.CheckCircle, null, tint = Teal, modifier = Modifier.size(20.dp))
        } else if (canPay) {
            if (isNear || daysUntil < 0) {
                IconButton(onClick = onPay, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Payments, null, tint = Teal)
                }
            } else {
                Text("En $daysUntil días", style = MaterialTheme.typography.labelSmall, color = WarningAmber, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FinancialColumn(label: String, amount: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 10.sp)
        Text(text = amount, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun InmobiliariaTab(summaries: List<ProviderAccountSummary>) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val totalClientRevenue  = summaries.sumOf { it.totalServiceAmountEarned + it.totalCompanyProfit }
    val totalProviderPaid   = summaries.sumOf { it.totalServiceAmountEarned }
    val totalCompanyEarned  = summaries.sumOf { it.totalCompanyProfit }
    val totalPendingProv    = summaries.sumOf { it.pendingBalance }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF4F6FB)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A)), elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF00D4AA).copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AccountBalance, null, tint = Color(0xFF00D4AA), modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("VG Tech · Ganancias Totales", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text("50% de cada pago del cliente", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Ingresos Totales de Clientes", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                        Text(fmt.format(totalClientRevenue), fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Parte de Proveedores (50%)", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFF7676).copy(alpha = 0.85f))
                        Text(fmt.format(totalProviderPaid), fontWeight = FontWeight.Bold, color = Color(0xFFFF7676))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Ganancia Neta Inmobiliaria", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = Color(0xFF00D4AA))
                        Text(fmt.format(totalCompanyEarned), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF00D4AA))
                    }
                    if (totalPendingProv > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFF7676).copy(alpha = 0.10f), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, null, tint = Color(0xFFFF7676), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Deuda pendiente con proveedores: ${fmt.format(totalPendingProv)}", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFF7676))
                            }
                        }
                    }
                }
            }
        }

        items(summaries.filter { it.totalCompanyProfit > 0 || it.totalServiceAmountEarned > 0 }, key = { it.providerId }) { summary ->
            InmobiliariaProviderCard(summary = summary, fmt = fmt)
        }
    }
}

@Composable
private fun InmobiliariaProviderCard(summary: ProviderAccountSummary, fmt: NumberFormat) {
    val totalClientForProvider = summary.totalServiceAmountEarned + summary.totalCompanyProfit
    val pctEmpresa = if (totalClientForProvider > 0) (summary.totalCompanyProfit / totalClientForProvider).toFloat().coerceIn(0f, 1f) else 0f

    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF43A047).copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Store, null, tint = Color(0xFF43A047), modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(summary.providerName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = Navy)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Ganancia", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(fmt.format(summary.totalCompanyProfit), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = Color(0xFF43A047))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFFFF7676).copy(alpha = 0.25f))) {
                Box(modifier = Modifier.fillMaxWidth(pctEmpresa).fillMaxHeight().clip(RoundedCornerShape(5.dp)).background(Color(0xFF43A047)))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FinancialColumn(label = "Total Cliente", amount = fmt.format(totalClientForProvider), color = Color(0xFF5C6BC0))
                FinancialColumn(label = "Parte Prov", amount = fmt.format(summary.totalServiceAmountEarned), color = Color(0xFFE53935))
                FinancialColumn(label = "Ganancia", amount = fmt.format(summary.totalCompanyProfit), color = Color(0xFF43A047))
            }
        }
    }
}

@Composable
private fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, modifier = Modifier.size(64.dp), tint = TextMuted.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDialog(providerName: String, isPayment: Boolean, onDismiss: () -> Unit, onConfirm: (Double, String) -> Unit) {
    var amountStr by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    
    val parsedAmount = amountStr.replace(",", ".").toDoubleOrNull()
    val isValid = parsedAmount != null && parsedAmount > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isPayment) "Abonar a $providerName" else "Ingreso de Cliente · $providerName", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = amountStr, 
                    onValueChange = { amountStr = it }, 
                    label = { Text("Monto ($)") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), 
                    modifier = Modifier.fillMaxWidth(),
                    isError = amountStr.isNotEmpty() && !isValid
                )
                if (amountStr.isNotEmpty() && !isValid) {
                    Text("Ingresa un monto válido", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
                
                if (!isPayment && isValid && parsedAmount != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = Navy.copy(alpha = 0.05f), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                            Text("División Automática (50/50):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Navy)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Para VG Tech:", style = MaterialTheme.typography.bodySmall, color = Teal)
                                Text(fmt.format(parsedAmount * 0.5), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Teal)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Para Proveedor:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF5C6BC0))
                                Text(fmt.format(parsedAmount * 0.5), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF5C6BC0))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { 
            Button(
                onClick = { parsedAmount?.let { onConfirm(it, desc) } }, 
                colors = ButtonDefaults.buttonColors(containerColor = Teal),
                enabled = isValid
            ) { 
                Text("Guardar") 
            } 
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDialog(
    summary: ProviderAccountSummary,
    suggestedPhase: PaymentPhase?,
    onDismiss: () -> Unit,
    onConfirm: (Double, String, String?) -> Unit
) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    var amountStr by remember { mutableStateOf(suggestedPhase?.amountToPay?.toString() ?: "") }
    var desc by remember { mutableStateOf(suggestedPhase?.let { "Pago de Fase ${it.phaseNumber}/${it.totalPhases}" } ?: "") }
    
    val parsedAmount = amountStr.replace(",", ".").toDoubleOrNull()
    val isValid = parsedAmount != null && parsedAmount > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Abonar a ${summary.providerName}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = WarningAmber.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Saldo pendiente total:", style = MaterialTheme.typography.labelSmall, color = Navy)
                        Text(fmt.format(summary.pendingBalance), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = Color(0xFFE53935))
                    }
                }

                if (suggestedPhase != null) {
                    Text(
                        "Fase pendiente detectada: Fase ${suggestedPhase.phaseNumber}/${suggestedPhase.totalPhases}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Teal
                    )
                }

                OutlinedTextField(
                    value = amountStr, 
                    onValueChange = { amountStr = it }, 
                    label = { Text("Monto a Abonar ($)") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), 
                    modifier = Modifier.fillMaxWidth(),
                    isError = amountStr.isNotEmpty() && !isValid
                )
                
                OutlinedTextField(
                    value = desc, 
                    onValueChange = { desc = it }, 
                    label = { Text("Descripción") }, 
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(
                    "Lo que queda de restante para el proveedor: ${fmt.format(summary.pendingBalance - (parsedAmount ?: 0.0))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        },
        confirmButton = { 
            Button(
                onClick = { parsedAmount?.let { onConfirm(it, desc, suggestedPhase?.id) } }, 
                colors = ButtonDefaults.buttonColors(containerColor = Teal),
                enabled = isValid
            ) { 
                Text("Confirmar Abono")
            } 
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePhasesDialog(
    provider: ProviderAccountSummary,
    phases: List<PaymentPhase>,
    availableProjects: List<Project>,
    onDismiss: () -> Unit,
    onAddPhase: (PaymentPhase) -> Unit,
    onDeletePhase: (String) -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }
    val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    
    val totalProjectAmount = remember(availableProjects) {
        if (availableProjects.size == 1) {
            val projId = availableProjects.first().id
            com.vgtech.mobile.data.local.InternalDb.providerTransactions.value
                .filter { it.projectId == projId && it.type == com.vgtech.mobile.data.model.TransactionType.SERVICE }
                .sumOf { it.providerCut }
        } else 0.0
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Fases de Pago: ${provider.providerName}", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 450.dp)) {
                if (showAddForm) {
                    AddPhaseForm(
                        providerId = provider.providerId,
                        projects = availableProjects,
                        totalProjectAmount = totalProjectAmount,
                        onCancel = { showAddForm = false },
                        onSave = { list -> 
                            list.forEach { onAddPhase(it) }
                            showAddForm = false 
                        }
                    )
                } else {
                    Button(
                        onClick = { showAddForm = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Navy)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Configuración de Fases")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(phases) { phase ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color.Gray.copy(alpha = 0.1f)).padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Fase ${phase.phaseNumber}/${phase.totalPhases}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                    Text(fmt.format(phase.amountToPay), color = Navy, style = MaterialTheme.typography.labelSmall)
                                    Text("Programado: ${SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX")).format(Date(phase.scheduledDate))}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                }
                                IconButton(onClick = { onDeletePhase(phase.id) }) {
                                    Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}

private data class PhaseInputData(
    val amount: String,
    val date: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPhaseForm(
    providerId: String,
    projects: List<Project>,
    totalProjectAmount: Double,
    onCancel: () -> Unit,
    onSave: (List<PaymentPhase>) -> Unit
) {
    var numPhasesStr by remember { mutableStateOf("") }
    var selectedProject by remember { mutableStateOf(projects.firstOrNull()) }
    
    // State for each phase
    var phaseInputs by remember { mutableStateOf(listOf<PhaseInputData>()) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX")) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Configuración Dinámica de Fases", fontWeight = FontWeight.Bold, color = Teal)

        if (totalProjectAmount > 0) {
            val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
            Text("Monto por cubrir: ${fmt.format(totalProjectAmount)}", style = MaterialTheme.typography.labelSmall, color = Navy, fontWeight = FontWeight.Bold)
        }

        OutlinedTextField(
            value = numPhasesStr,
            onValueChange = { input ->
                val filtered = input.filter { it.isDigit() }
                numPhasesStr = filtered
                val n = filtered.toIntOrNull() ?: 0
                if (n > 0) {
                    val newList = List(n) { i ->
                        phaseInputs.getOrNull(i) ?: PhaseInputData(amount = "", date = System.currentTimeMillis())
                    }
                    phaseInputs = newList
                } else {
                    phaseInputs = emptyList()
                }
            },
            label = { Text("¿Cuántas fases de pago tendrá?") },
            placeholder = { Text("Ej: 3") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        if (phaseInputs.isNotEmpty()) {
            phaseInputs.forEachIndexed { index, phase ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4F8)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Fase ${index + 1}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Navy)
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = phase.amount,
                                onValueChange = { newVal ->
                                    val updated = phaseInputs.toMutableList()
                                    updated[index] = updated[index].copy(amount = newVal.filter { it.isDigit() || it == '.' })
                                    phaseInputs = updated
                                },
                                label = { Text("Monto") },
                                placeholder = { Text("0.00") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Box(modifier = Modifier.weight(1f).clickable {
                                val cal = Calendar.getInstance()
                                cal.timeInMillis = phase.date
                                android.app.DatePickerDialog(
                                    context,
                                    { _, y, m, d ->
                                        val c = Calendar.getInstance()
                                        c.set(y, m, d)
                                        val updated = phaseInputs.toMutableList()
                                        updated[index] = updated[index].copy(date = c.timeInMillis)
                                        phaseInputs = updated
                                    },
                                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }) {
                                OutlinedTextField(
                                    value = sdf.format(Date(phase.date)),
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = false,
                                    label = { Text("Fecha") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onCancel) { Text("Cancelar") }
            Button(
                onClick = {
                    val n = numPhasesStr.toIntOrNull() ?: 0
                    if (n > 0 && selectedProject != null) {
                        val result = phaseInputs.mapIndexed { i, input ->
                            PaymentPhase(
                                providerId = providerId,
                                projectId = selectedProject!!.id,
                                phaseNumber = i + 1,
                                totalPhases = n,
                                amountToPay = input.amount.toDoubleOrNull() ?: 0.0,
                                scheduledDate = input.date
                            )
                        }
                        onSave(result)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Teal),
                enabled = phaseInputs.isNotEmpty() && phaseInputs.all { it.amount.isNotBlank() }
            ) {
                Text("Generar Todo")
            }
        }
    }
}
