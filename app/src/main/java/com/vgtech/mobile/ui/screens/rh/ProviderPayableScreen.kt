package com.vgtech.mobile.ui.screens.rh

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.vgtech.mobile.data.model.ProviderAccountSummary
import com.vgtech.mobile.data.model.ProviderTransaction
import com.vgtech.mobile.data.model.TransactionType
import com.vgtech.mobile.ui.theme.*
import com.vgtech.mobile.ui.viewmodel.ProviderPayableViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// ROOT SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderPayableScreen(
    viewModel: ProviderPayableViewModel = viewModel()
) {
    val loading by viewModel.loading.collectAsState()
    val summaries by viewModel.providerSummaries.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val providerNames by viewModel.providerNames.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Por Pagar", "Pagadas", "Historial")

    var showDialog by remember { mutableStateOf(false) }
    var selectedProvider by remember { mutableStateOf<ProviderAccountSummary?>(null) }
    var isPayment by remember { mutableStateOf(false) }

    if (error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Aviso") },
            text = { Text(error ?: "") },
            confirmButton = { TextButton(onClick = { viewModel.clearError() }) { Text("OK") } }
        )
    }

    if (showDialog && selectedProvider != null) {
        TransactionDialog(
            providerName = selectedProvider!!.providerName,
            isPayment = isPayment,
            onDismiss = { showDialog = false; selectedProvider = null },
            onConfirm = { amount, desc ->
                if (isPayment) viewModel.registerPayment(selectedProvider!!.providerId, amount, desc)
                else viewModel.registerService(selectedProvider!!.providerId, amount, desc)
                showDialog = false; selectedProvider = null
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F6FB))) {

        // ── KPI Header Strip ──────────────────────────────────────────────
        KpiHeaderStrip(summaries = summaries)

        // ── Tab Row ───────────────────────────────────────────────────────
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

        // ── Tab Content ───────────────────────────────────────────────────
        when (selectedTab) {
            0 -> PendingTab(
                summaries = summaries.filter { it.pendingBalance > 0 },
                onAddService = { summary ->
                    selectedProvider = summary; isPayment = false; showDialog = true
                },
                onAddPayment = { summary ->
                    selectedProvider = summary; isPayment = true; showDialog = true
                }
            )
            1 -> PaidTab(summaries = summaries.filter { it.pendingBalance <= 0 })
            2 -> HistoryTab(
                transactions = allTransactions,
                providerNames = providerNames
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// KPI HEADER STRIP
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun KpiHeaderStrip(summaries: List<ProviderAccountSummary>) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val totalDebt    = summaries.sumOf { it.pendingBalance }
    val totalPaid    = summaries.sumOf { it.totalAmountPaid }
    val totalProfit  = summaries.sumOf { it.totalCompanyProfit }
    val countPending = summaries.count { it.pendingBalance > 0 }

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

// ─────────────────────────────────────────────────────────────────────────────
// TAB: POR PAGAR
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PendingTab(
    summaries: List<ProviderAccountSummary>,
    onAddService: (ProviderAccountSummary) -> Unit,
    onAddPayment: (ProviderAccountSummary) -> Unit
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
                isPending = true,
                onAddService = { onAddService(summary) },
                onAddPayment = { onAddPayment(summary) }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB: PAGADAS
// ─────────────────────────────────────────────────────────────────────────────
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
                isPending = false,
                onAddService = {},
                onAddPayment = {}
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB: HISTORIAL
// ─────────────────────────────────────────────────────────────────────────────
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
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(bgColor),
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
                Text(
                    text = providerName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
                Text(
                    text = tx.description.ifBlank { if (isService) "Servicio registrado" else "Abono registrado" },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dateFmt.format(Date(tx.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                val label = if (isService) "Cliente pagó" else "Abono"
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor
                )
                Text(
                    text = fmt.format(tx.rawAmount),
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = accentColor
                )
                if (isService) {
                    Text(
                        text = "Prov: ${fmt.format(tx.providerCut)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PROVIDER ACCOUNT CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ProviderAccountCard(
    summary: ProviderAccountSummary,
    isPending: Boolean,
    onAddService: () -> Unit,
    onAddPayment: () -> Unit
) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val balanceColor by animateColorAsState(
        targetValue = if (summary.pendingBalance > 0) Color(0xFFE53935) else Teal,
        animationSpec = tween(400), label = "balance_color"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Provider name + status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape)
                            .background(Navy.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = Navy, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = summary.providerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Navy
                    )
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isPending) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = if (isPending) "Con Deuda" else "Al Corriente",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isPending) Color(0xFFE53935) else Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(14.dp))

            // Financial info row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FinancialColumn(label = "Total Generado", amount = fmt.format(summary.totalServiceAmountEarned), color = Color(0xFF5C6BC0))
                FinancialColumn(label = "Total Pagado", amount = fmt.format(summary.totalAmountPaid), color = Teal)
                FinancialColumn(label = "Utilidad Empresa", amount = fmt.format(summary.totalCompanyProfit), color = Color(0xFF43A047))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Saldo pendiente highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(balanceColor.copy(alpha = 0.08f))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (summary.pendingBalance > 0) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = balanceColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Saldo Pendiente", fontWeight = FontWeight.SemiBold, color = balanceColor)
                    }
                    Text(
                        text = fmt.format(summary.pendingBalance),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium,
                        color = balanceColor
                    )
                }
            }

            // Action buttons (only when pending)
            if (isPending) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onAddService,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5C6BC0)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF5C6BC0))
                    ) {
                        Icon(Icons.Default.AddCard, null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cliente Pagó", style = MaterialTheme.typography.labelSmall)
                    }
                    Button(
                        onClick = onAddPayment,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Teal)
                    ) {
                        Icon(Icons.Default.AttachMoney, null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Abonar", style = MaterialTheme.typography.labelSmall)
                    }
                }
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

// ─────────────────────────────────────────────────────────────────────────────
// EMPTY STATE
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(64.dp), tint = TextMuted.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TRANSACTION DIALOG
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDialog(
    providerName: String,
    isPayment: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    val title = if (isPayment) "Abonar a $providerName" else "Ingreso de Cliente · $providerName"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                if (!isPayment) {
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF5C6BC0).copy(alpha = 0.08f)) {
                        Text(
                            "El monto ingresado se dividirá automáticamente 50% empresa / 50% proveedor.",
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF5C6BC0)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Monto ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal, focusedLabelColor = Teal)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text(if (isPayment) "Concepto del abono" else "Descripción del servicio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal, focusedLabelColor = Teal)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull()
                    if (amt != null && amt > 0) onConfirm(amt, desc)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextMuted) }
        }
    )
}
