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
import com.bhplusplus.yaya.ui.components.RequestItemShimmer

/**
 * PANTALLA DE MIS PEDIDOS (VISTA CLIENTE)
 * Arquitectura MVVM: La View solo renderiza el estado procesado del ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    onBack: () -> Unit,
    onChatClick: (String, String) -> Unit, // receiverId, receiverName
    viewModel: MyOrdersViewModel = viewModel()
) {
    var showNegotiationDialog by remember { mutableStateOf<MyOrderUiState?>(null) }
    var counterPrice by remember { mutableStateOf("") }
    var showRatingDialog by remember { mutableStateOf<MyOrderUiState?>(null) }
    val pullToRefreshState = rememberPullToRefreshState()

    if (showRatingDialog != null) {
        RatingDialog(
            onDismiss = { showRatingDialog = null },
            onConfirm = { score, comment ->
                viewModel.submitRating(showRatingDialog!!.domain, score, comment) {
                    showRatingDialog = null
                }
            },
            isSubmitting = viewModel.isSubmittingRating
        )
    }

    if (showNegotiationDialog != null) {
        val current = showNegotiationDialog!!
        val basePrice = current.domain.services?.price ?: 0.0

        LaunchedEffect(current) {
            counterPrice = current.domain.final_price.toInt().toString()
        }

        AlertDialog(
            onDismissRequest = { showNegotiationDialog = null },
            title = { Text("Ajustar mi Oferta", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Precio base del servicio: $${basePrice.toInt()}", fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    Text("Última contraoferta: ${current.formattedPrice}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(20.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        FilledIconButton(
                            onClick = {
                                val price = counterPrice.toDoubleOrNull() ?: 0.0
                                if (price > basePrice) counterPrice = (price - 5000).coerceAtLeast(basePrice).toInt().toString()
                            },
                            enabled = (counterPrice.toDoubleOrNull() ?: 0.0) > basePrice,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) { Icon(Icons.Default.Remove, "Menos") }

                        OutlinedTextField(
                            value = counterPrice,
                            onValueChange = { newValue ->
                                val numeric = newValue.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                                counterPrice = if (numeric < basePrice) basePrice.toInt().toString() else numeric.toInt().toString()
                            },
                            modifier = Modifier.width(130.dp).padding(horizontal = 8.dp),
                            textStyle = LocalTextStyle.current.copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 18.sp),
                            prefix = { Text("$") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )

                        FilledIconButton(
                            onClick = {
                                val price = counterPrice.toDoubleOrNull() ?: 0.0
                                counterPrice = (price + 5000).toInt().toString()
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) { Icon(Icons.Default.Add, "Más") }
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    Text("No puedes ofertar menos del precio base", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendNewOffer(current.domain, counterPrice)
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
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            ) {
                if (viewModel.isLoading && viewModel.orders.isEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(4) {
                            RequestItemShimmer()
                        }
                    }
                } else if (viewModel.orders.isEmpty()) {
                    EmptyOrdersView()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(viewModel.orders) { uiState ->
                            OrderItem(
                                state = uiState,
                                onAccept = { 
                                    if (uiState.status == "accepted") {
                                        viewModel.confirmWorkStart(uiState.domain.id!!)
                                    } else {
                                        viewModel.acceptProposal(uiState.domain.id!!)
                                    }
                                },
                                onReject = { viewModel.cancelRequest(uiState.domain.id!!) },
                                onNegotiate = { showNegotiationDialog = uiState },
                                onRate = { showRatingDialog = uiState },
                                onChat = {
                                    val providerId = uiState.domain.services?.provider_id ?: ""
                                    onChatClick(providerId, uiState.providerName)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Representa un pedido individual. Recibe un [MyOrderUiState].
 */
@Composable
fun OrderItem(
    state: MyOrderUiState,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onNegotiate: () -> Unit,
    onRate: () -> Unit,
    onChat: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.providerAvatarUrl != null) {
                            AsyncImage(
                                model = state.providerAvatarUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(text = state.serviceTitle, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = state.providerName, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                StatusBadge(state.status)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DetailRow(Icons.Default.LocationOn, state.address)
                DetailRow(Icons.Default.Payments, "Oferta actual: ${state.formattedPrice}")
                DetailRow(Icons.Default.Event, state.formattedDate)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "ESTADO DE LA NEGOCIACIÓN", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), letterSpacing = 0.5.sp)
            Spacer(Modifier.height(8.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp).padding(top = 2.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Box(modifier = Modifier.heightIn(max = 200.dp).verticalScroll(rememberScrollState())) {
                        Text(text = state.description, fontSize = 13.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (state.status == "pending" || state.status == "accepted" || state.status == "in_progress") {
                Spacer(modifier = Modifier.height(16.dp))

                if (state.isCounterOfferActive) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(Modifier.width(10.dp))
                            Text("Nueva oferta recibida. ¿Aceptas?", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(
                        onClick = onChat,
                        modifier = Modifier.size(52.dp).background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
                    ) { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat", tint = MaterialTheme.colorScheme.onSecondaryContainer) }

                    when (state.status) {
                        "in_progress" -> {
                            // INDICADOR DE TRABAJO EN CURSO
                            Surface(
                                modifier = Modifier.weight(1f).height(52.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("Trabajo en Curso...", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                        "accepted" -> {
                            // BOTÓN PARA INICIAR TRABAJO (Doble Confirmación)
                            Button(
                                onClick = onAccept, // Reutilizamos el callback para confirmWorkStart
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                            ) {
                                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("CONFIRMAR Y EMPEZAR", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            }
                        }
                        else -> { // Estado 'pending'
                            OutlinedButton(onClick = onNegotiate, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)) {
                                Text("Negociar", fontWeight = FontWeight.Bold)
                            }

                            if (state.isCounterOfferActive) {
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
                                TextButton(onClick = onReject, modifier = Modifier.height(52.dp)) {
                                    Text("Cancelar", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            // ACCIÓN DE CALIFICAR (Solo si está completado y no calificado)
            if (state.status == "completed") {
                Spacer(modifier = Modifier.height(20.dp))
                if (state.isRated) {
                    // MOSTRAR LA CALIFICACIÓN YA REALIZADA (Feedback Pro)
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB800).copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "TU CALIFICACIÓN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFB800)
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                for (i in 1..5) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = if (i <= (state.ratingScore ?: 0)) Color(0xFFFFB800) else Color.Gray.copy(alpha = 0.3f)
                                    )
                                }
                            }
                            if (!state.ratingComment.isNullOrBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "\"${state.ratingComment}\"",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
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
}

@Composable
fun RatingDialog(onDismiss: () -> Unit, onConfirm: (Int, String) -> Unit, isSubmitting: Boolean) {
    var score by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Calificar Servicio", fontWeight = FontWeight.Bold) }, text = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("¿Cómo calificarías el trabajo recibido?", fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
            Row {
                for (i in 1..5) {
                    IconButton(onClick = { score = i }) {
                        Icon(imageVector = if (i <= score) Icons.Default.Star else Icons.Default.StarOutline, contentDescription = null, tint = if (i <= score) Color(0xFFFFB800) else Color.Gray)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = comment, onValueChange = { comment = it }, label = { Text("Escribe un comentario (opcional)") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        }
    }, confirmButton = {
        Button(onClick = { onConfirm(score, comment) }, enabled = !isSubmitting, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB800))) {
            if (isSubmitting) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            else Text("Enviar Calificación", color = Color.White)
        }
    }, dismissButton = { TextButton(onClick = onDismiss, enabled = !isSubmitting) { Text("Cancelar") } })
}

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, textColor, label) = when (status) {
        "pending" -> Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer, "Pendiente")
        "accepted" -> Triple(Color(0xFFE8F5E9), Color(0xFF4CAF50), "Confirmada")
        "completed" -> Triple(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer, "Completada")
        "cancelled" -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, "Cancelada")
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, status)
    }
    Surface(color = backgroundColor, shape = RoundedCornerShape(8.dp)) {
        Text(text = label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

@Composable
fun EmptyOrdersView() {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Aún no tienes pedidos", color = Color.Gray)
    }
}

@Composable
fun DetailRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
        Spacer(Modifier.width(12.dp))
        Text(text = text, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Preview(showBackground = true)
@Composable
fun MyOrdersPreview() {
    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Color.White)) {
            Text("Vista Previa: Mis Pedidos")
        }
    }
}
