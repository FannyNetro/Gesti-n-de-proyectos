package com.vgtech.mobile.ui.screens.cliente

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vgtech.mobile.data.local.InternalDb
import com.vgtech.mobile.data.model.ChatMessage
import com.vgtech.mobile.data.model.Quotation
import com.vgtech.mobile.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ═══════════════════════════════════════════════════════════════════
//  ClienteDashboardScreen (HU1 - HU5)
// ═══════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteDashboardScreen(
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val quotations by InternalDb.quotations.collectAsState()
    val clientQuotations = quotations.filter { it.sentToClient }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "VG Tech",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = SurfaceWhite
                        )
                        Text(
                            "Portal de Clientes",
                            style = MaterialTheme.typography.labelSmall,
                            color = SurfaceWhite.copy(alpha = 0.6f)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Navy),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = SurfaceWhite.copy(alpha = 0.7f)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceWhite,
                tonalElevation = 8.dp
            ) {
                val tabs = listOf(
                    Triple("Nosotros", Icons.Default.Business, 0),
                    Triple("Cotizaciones", Icons.Default.RequestQuote, 1),
                    Triple("Chat", Icons.Default.Chat, 2)
                )
                tabs.forEach { (label, icon, index) ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp)) },
                        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Teal,
                            selectedTextColor = Teal,
                            indicatorColor = Teal.copy(alpha = 0.12f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundLight)
        ) {
            when (selectedTab) {
                0 -> LandingPageView() // HU1 y HU2
                1 -> CotizacionesView(clientQuotations) // HU3, HU4, HU5
                2 -> ClienteChatScreen()
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  TAB 0: Landing / Inicio (HU1 y HU2)
// ═══════════════════════════════════════════════════════════════════

@Composable
fun LandingPageView() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // HU1: Información institucional visible
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Navy),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Construyendo el futuro con excelencia",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = SurfaceWhite
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "En VG Tech somos líderes en supervisión y ejecución de obras civiles e industriales. Nuestro objetivo es garantizar calidad, seguridad y una reducción de hasta 20% en costos operativos mediante consultoría inteligente.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SurfaceWhite.copy(alpha = 0.8f),
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // HU2: Visualizar servicios y tipos de proyectos
        item {
            Text(
                "Nuestros Servicios",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Navy
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            ServiceCard(
                title = "Supervisión de Obra",
                description = "Garantizamos el cumplimiento normativo y los estándares de calidad en cada fase de su proyecto constructivo.",
                icon = Icons.Default.Engineering,
                iconColor = Teal
            )
        }
        item {
            ServiceCard(
                title = "Consultoría de Ingeniería",
                description = "Diseño estructural, cálculos y auditorías para optimizar materiales y mejorar la resistencia de infraestructuras.",
                icon = Icons.Default.Architecture,
                iconColor = MustardDark
            )
        }
        item {
            ServiceCard(
                title = "Gestión de Materiales y Acabados",
                description = "Evaluación de proveedores y control de presupuestos para que obtenga siempre el mejor valor del mercado.",
                icon = Icons.Default.Inventory,
                iconColor = SuccessGreen
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun ServiceCard(title: String, description: String, icon: ImageVector, iconColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Navy)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, style = MaterialTheme.typography.bodySmall, color = TextMuted, lineHeight = 18.sp)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  TAB 1: Cotizaciones (HU3, HU4, HU5)
// ═══════════════════════════════════════════════════════════════════

@Composable
fun CotizacionesView(quotations: List<Quotation>) {
    var selectedQuotation by remember { mutableStateOf<Quotation?>(null) }
    
    // HU3: Recibir y visualizar cotizaciones
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Cotizaciones Recibidas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Navy
            )
        }

        if (quotations.isEmpty()) {
            item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay cotizaciones para revisar.", color = TextMuted)
                }
            }
        } else {
            items(quotations.sortedByDescending { it.date }) { quote ->
                QuotationItemCard(quote) { selectedQuotation = quote }
            }
        }
    }

    // Modal de Detalles
    if (selectedQuotation != null) {
        QuotationDetailModal(
            quotation = selectedQuotation!!,
            onDismiss = { selectedQuotation = null }
        )
    }
}

@Composable
private fun QuotationItemCard(quote: Quotation, onClick: () -> Unit) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "MX")) }
    val sdf = remember { SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(quote.projectTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Navy, modifier = Modifier.weight(1f))
                Text(sdf.format(Date(quote.date)), style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Teal, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(currencyFormatter.format(quote.amount), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = Teal)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Status Chip
            val statusColor = when (quote.clientStatus) {
                "Pendiente" -> WarningAmber
                "Aprobada" -> SuccessGreen
                "Rechazada" -> ErrorRed
                else -> TextMuted
            }
            Surface(
                color = statusColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    quote.clientStatus.uppercase(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }
    }
}

// HU4 y HU5: Consultar detalles y Seleccionar/Autorizar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuotationDetailModal(
    quotation: Quotation,
    onDismiss: () -> Unit
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "MX")) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Detalles de la Cotización",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Navy
            )
            
            HorizontalDivider(color = BorderColor)

            DetailRow("Proyecto asociado", quotation.projectTitle)
            DetailRow("Proveedor propuesto", quotation.providerName)
            DetailRow("Tiempo estimado", "${quotation.estimatedDays} días hábiles")
            DetailRow("Inversión requerida", currencyFormatter.format(quotation.amount), isBold = true, color = Teal)
            
            Column {
                Text("Descripción y Condiciones", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextMuted)
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    color = BackgroundLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        quotation.description.ifEmpty { "Sin descripción adicional." },
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Navy
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // HU5: Seleccionar cotización (Solo si está pendiente)
            if (quotation.clientStatus == "Pendiente") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = {
                            InternalDb.updateQuotationClientStatus(quotation.id, "Rechazada")
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed)
                    ) {
                        Text("Rechazar")
                    }
                    Button(
                        onClick = {
                            InternalDb.updateQuotationClientStatus(quotation.id, "Aprobada")
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Text("Autorizar")
                    }
                }
            } else {
                Text(
                    "Esta cotización fue ${quotation.clientStatus.lowercase()}.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, isBold: Boolean = false, color: Color = Navy) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Text(value, style = if(isBold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium, fontWeight = if(isBold) FontWeight.ExtraBold else FontWeight.SemiBold, color = color)
    }
}

// ═══════════════════════════════════════════════════════════════════
//  TAB 2: Chat directo con el Supervisor
// ═══════════════════════════════════════════════════════════════════

@Composable
fun ClienteChatScreen() {
    val currentClientUid = "cliente-uid"
    val supervisorUid   = "sup-uid"

    val allMessages by InternalDb.chatMessages.collectAsState()
    val chatMessages = remember(allMessages) {
        allMessages
            .filter {
                (it.senderUid == currentClientUid && it.receiverUid == supervisorUid) ||
                (it.senderUid == supervisorUid   && it.receiverUid == currentClientUid)
            }
            .sortedBy { it.timestamp }
    }

    var messageText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {

        // ── Header del chat ───────────────────────────────────────────────
        Surface(
            color = SurfaceWhite,
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Navy.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Navy, modifier = Modifier.size(26.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        "Supervisor VG Tech",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Navy
                    )
                    Text(
                        "● En línea",
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // ── Mensajes ──────────────────────────────────────────────────────
        if (chatMessages.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TextMuted.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Línea directa con el supervisor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Consulta sobre tus proyectos, cotizaciones o cualquier duda.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatMessages) { msg ->
                    val isMine = msg.senderUid == currentClientUid
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
                    ) {
                        if (!isMine) {
                            Text(
                                "Supervisor",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                            )
                        }
                        Surface(
                            color = if (isMine) Navy else SurfaceWhite,
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isMine) 16.dp else 4.dp,
                                bottomEnd = if (isMine) 4.dp else 16.dp
                            ),
                            modifier = Modifier.widthIn(max = 280.dp),
                            shadowElevation = if (isMine) 0.dp else 1.dp
                        ) {
                            Text(
                                text = msg.message,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                color = if (isMine) Color.White else Navy,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        // ── Input ─────────────────────────────────────────────────────────
        Surface(color = SurfaceWhite, shadowElevation = 4.dp, modifier = Modifier.imePadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Escribe un mensaje...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Navy,
                        focusedLabelColor = Navy
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            InternalDb.addChatMessage(
                                ChatMessage(
                                    senderUid = currentClientUid,
                                    receiverUid = supervisorUid,
                                    projectId  = "CLI_SUP_DIRECT",
                                    message    = messageText.trim()
                                )
                            )
                            messageText = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Navy,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
                }
            }
        }
    }
}
