package com.bhplusplus.yaya.ui.screens.my_orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.data.models.ServiceRequest

/**
 * PANTALLA DE MIS PEDIDOS (VISTA CLIENTE)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    onBack: () -> Unit,
    onChatClick: (String, String) -> Unit, // receiverId, receiverName
    viewModel: MyOrdersViewModel = viewModel()
) {
    var showNegotiationDialog by remember { mutableStateOf<ServiceRequest?>(null) }
    var counterPrice by remember { mutableStateOf("") }
    
    var showRatingDialog by remember { mutableStateOf<ServiceRequest?>(null) }
    val pullToRefreshState = rememberPullToRefreshState()

    // Dialogo para Calificación (Hito 2)
    if (showRatingDialog != null) {
        RatingDialog(
            onDismiss = { showRatingDialog = null },
            onConfirm = { score, comment ->
                viewModel.submitRating(showRatingDialog!!, score, comment) {
                    showRatingDialog = null
                }
            },
            isSubmitting = viewModel.isSubmittingRating
        )
    }

    // Dialogo para que el cliente haga una nueva oferta (Premium UX)
    if (showNegotiationDialog != null) {
        val currentOrder = showNegotiationDialog!!
        val basePrice = currentOrder.services?.price ?: 0.0

        // Inicializamos con el precio actual de la solicitud
        LaunchedEffect(currentOrder) {
            counterPrice = currentOrder.final_price.toInt().toString()
        }

        AlertDialog(
            onDismissRequest = { showNegotiationDialog = null },
            title = { 
                Text(
                    "Ajustar mi Oferta", 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ) 
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Precio base del servicio: $${basePrice.toInt()}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Última contraoferta: $${currentOrder.final_price.toInt()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(20.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilledIconButton(
                            onClick = {
                                val current = counterPrice.toDoubleOrNull() ?: 0.0
                                // No permitimos bajar del precio base
                                if (current > basePrice) {
                                    counterPrice = (current - 5000).coerceAtLeast(basePrice).toInt().toString()
                                }
                            },
                            enabled = (counterPrice.toDoubleOrNull() ?: 0.0) > basePrice,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Icon(Icons.Default.Remove, "Menos")
                        }

                        OutlinedTextField(
                            value = counterPrice,
                            onValueChange = { newValue ->
                                val numeric = newValue.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                                counterPrice = if (numeric < basePrice) basePrice.toInt().toString() else numeric.toInt().toString()
                            },
                            modifier = Modifier.width(130.dp).padding(horizontal = 8.dp),
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            prefix = { Text("$") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )

                        FilledIconButton(
                            onClick = {
                                val current = counterPrice.toDoubleOrNull() ?: 0.0
                                counterPrice = (current + 5000).toInt().toString()
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Icon(Icons.Default.Add, "Más")
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No puedes ofertar menos del precio base",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendNewOffer(currentOrder, counterPrice)
                        showNegotiationDialog = null
                        counterPrice = ""
                    },
                    shape = RoundedCornerShape(12.dp),
                    enabled = (counterPrice.toDoubleOrNull() ?: 0.0) >= basePrice
                ) { Text("Enviar Oferta") }
            },
            dismissButton = {
                TextButton(onClick = { showNegotiationDialog = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_orders_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = viewModel.isLoading,
            onRefresh = { viewModel.fetchMyOrders() },
            state = pullToRefreshState,
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (viewModel.isLoading && viewModel.orders.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (viewModel.orders.isEmpty()) {
                    EmptyOrdersView()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(viewModel.orders) { order ->
                            OrderItem(
                                order = order,
                                onAccept = { viewModel.acceptProposal(order.id!!) },
                                onReject = { viewModel.cancelRequest(order.id!!) },
                                onNegotiate = { showNegotiationDialog = order },
                                onRate = { showRatingDialog = order },
                                onChat = {
                                    val providerId = order.services?.provider_id ?: ""
                                    onChatClick(providerId, "Prestador")
                                },
                                isCounterOfferActive = viewModel.isCounterOfferActive(order),
                                formattedDate = viewModel.formatDate(order.scheduled_date)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItem(
    order: ServiceRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onNegotiate: () -> Unit,
    onRate: () -> Unit,
    onChat: () -> Unit,
    isCounterOfferActive: Boolean,
    formattedDate: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // HEADER: Avatar + Nombre + Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (order.services?.provider?.avatar_url != null) {
                            AsyncImage(
                                model = order.services.provider.avatar_url,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = order.services?.title ?: "Servicio",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = order.services?.provider?.full_name ?: "Prestador Independiente",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                StatusBadge(order.status)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // DETALLES: Ubicación, Precio y Fecha
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DetailRow(Icons.Default.LocationOn, order.service_address)
                DetailRow(Icons.Default.Payments, "Oferta actual: $${order.final_price.toInt()}")
                DetailRow(Icons.Default.Event, formattedDate)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECCIÓN DE NEGOCIACIÓN / HISTORIAL
            Text(
                text = "ESTADO DE LA NEGOCIACIÓN",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(8.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info, 
                        contentDescription = null, 
                        modifier = Modifier.size(16.dp).padding(top = 2.dp), 
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .heightIn(max = 120.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = order.request_description ?: "Sin historial de negociación",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ACCIONES
            if (order.status == "pending") {
                Spacer(modifier = Modifier.height(16.dp))

                if (isCounterOfferActive) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Nueva oferta recibida. ¿Aceptas?",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón CHAT (Importante en negociación)
                    IconButton(
                        onClick = onChat,
                        modifier = Modifier
                            .size(52.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    }

                    // Botón para contraofertar
                    OutlinedButton(
                        onClick = onNegotiate,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Negociar", fontWeight = FontWeight.Bold)
                    }

                    // Botón Aceptar (Solo si hay contraoferta)
                    if (isCounterOfferActive) {
                        Button(
                            onClick = onAccept,
                            modifier = Modifier.weight(1.2f).height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(4.dp)
                        ) {
                            Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Aceptar", fontWeight = FontWeight.ExtraBold)
                        }
                    } else {
                        // Botón Cancelar si no hay contraoferta pendiente
                        TextButton(
                            onClick = onReject,
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text("Cancelar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            // ACCIÓN DE CALIFICAR (Solo si está completado)
            if (order.status == "completed") {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onRate,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB800))
                ) {
                    Icon(Icons.Default.Star, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("CALIFICAR SERVICIO", fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun RatingDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit,
    isSubmitting: Boolean
) {
    var score by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Calificar Servicio", fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("¿Cómo calificarías el trabajo recibido?", fontSize = 14.sp)
                Spacer(Modifier.height(16.dp))
                
                // Selector de estrellas
                Row {
                    for (i in 1..5) {
                        IconButton(onClick = { score = i }) {
                            Icon(
                                imageVector = if (i <= score) Icons.Default.Star else Icons.Default.StarOutline,
                                contentDescription = null,
                                tint = if (i <= score) Color(0xFFFFB800) else Color.Gray
                            )
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Escribe un comentario (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(score, comment) },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB800))
            ) {
                if (isSubmitting) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                else Text("Enviar Calificación", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSubmitting) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, textColor, label) = when (status) {
        "pending" -> Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer, "Pendiente")
        "accepted" -> Triple(Color(0xFFE8F5E9), Color(0xFF4CAF50), "Confirmada") // Mantener verde éxito
        "completed" -> Triple(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer, "Completada")
        "cancelled" -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, "Cancelada")
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, status)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun EmptyOrdersView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Aún no tienes pedidos", color = Color.Gray)
    }
}

@Composable
fun DetailRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text, 
            fontSize = 14.sp, 
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyOrdersPreview() {
    val dummyService = com.bhplusplus.yaya.data.models.Service(
        title = "Limpieza de Hogar",
        price = 50000.0,
        provider_id = "1"
    )
    
    val dummyOrder = ServiceRequest(
        id = "1",
        client_id = "2",
        service_id = "1",
        final_price = 55000.0,
        request_description = "🚩 Contraoferta Prestador: $55000",
        service_address = "Calle 123 #45-67",
        scheduled_date = "2026-08-30T10:00:00Z",
        status = "pending",
        services = dummyService
    )

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(16.dp)
        ) {
            Text(
                "Vista Previa: Mis Pedidos",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            OrderItem(
                order = dummyOrder,
                onAccept = {},
                onReject = {},
                onNegotiate = {},
                onRate = {},
                onChat = {},
                isCounterOfferActive = true,
                formattedDate = "2026-08-30"
            )
        }
    }
}
