package com.bhplusplus.yaya.ui.screens.my_orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.ui.components.RequestItemShimmer
import com.bhplusplus.yaya.ui.components.molecules.EmptyStateView
import com.bhplusplus.yaya.ui.components.organisms.MyOrderCard

/**
 * PANTALLA DE MIS PEDIDOS (VISTA CLIENTE)
 * Orquestada mediante Atomic Design.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    onBack: () -> Unit,
    onChatClick: (String, String) -> Unit,
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
                    EmptyStateView(
                        title = "Aún no tienes pedidos",
                        description = "Explora talentos y realiza tu primera solicitud.",
                        icon = Icons.Default.History
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(viewModel.orders) { uiState ->
                            // Organismo: Tarjeta de pedido cliente
                            MyOrderCard(
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
            if (isSubmitting) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            else Text("Enviar Calificación", color = Color.White)
        }
    }, dismissButton = { TextButton(onClick = onDismiss, enabled = !isSubmitting) { Text("Cancelar") } })
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
