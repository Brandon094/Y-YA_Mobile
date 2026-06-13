package com.bhplusplus.yaya.ui.screens.my_orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.data.models.ServiceRequest

/**
 * PANTALLA DE MIS PEDIDOS (VISTA CLIENTE)
 * Ahora permite responder a contraofertas del prestador.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    onBack: () -> Unit,
    viewModel: MyOrdersViewModel = viewModel()
) {
    var showNegotiationDialog by remember { mutableStateOf<ServiceRequest?>(null) }
    var counterPrice by remember { mutableStateOf("") }

    // Dialogo para que el cliente haga una nueva oferta
    if (showNegotiationDialog != null) {
        AlertDialog(
            onDismissRequest = { showNegotiationDialog = null },
            title = { Text("Nueva Propuesta") },
            text = {
                Column {
                    Text("Ingresa tu nueva oferta para el prestador:", fontSize = 14.sp)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = counterPrice,
                        onValueChange = { counterPrice = it },
                        label = { Text("Tu Oferta") },
                        prefix = { Text("$") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.sendNewOffer(showNegotiationDialog!!, counterPrice)
                    showNegotiationDialog = null
                    counterPrice = ""
                }) { Text("Enviar") }
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (viewModel.orders.isEmpty()) {
                EmptyOrdersView()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewModel.orders) { order ->
                        OrderItem(
                            order = order,
                            onAccept = { viewModel.acceptProposal(order.id!!) },
                            onReject = { viewModel.cancelRequest(order.id!!) },
                            onNegotiate = { showNegotiationDialog = order }
                        )
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
    onNegotiate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.services?.title ?: "Servicio",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                StatusBadge(order.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(text = "Ubicación: ${order.service_address}", fontSize = 14.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Historial de negociación
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Historial / Detalles:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = order.request_description ?: "Sin detalles", fontSize = 14.sp)
                }
            }

            // ACCIONES DE NEGOCIACIÓN (Solo si está pendiente)
            if (order.status == "pending") {
                val isCounterOffer = order.request_description?.contains("Contraoferta Prestador") == true
                
                Spacer(modifier = Modifier.height(16.dp))

                if (isCounterOffer) {
                    Text("¡El prestador propuso otro precio!", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón para contraofertar de nuevo o negociar
                    OutlinedButton(
                        onClick = onNegotiate,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Negociar", fontSize = 12.sp)
                    }

                    // Botón para aceptar (si el cliente está de acuerdo con el precio)
                    if (isCounterOffer) {
                        Button(
                            onClick = onAccept,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Aceptar", fontSize = 12.sp)
                        }
                    }

                    // Botón para cancelar/rechazar
                    TextButton(
                        onClick = onReject,
                        modifier = Modifier.weight(0.8f),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancelar", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, textColor, label) = when (status) {
        "pending" -> Triple(Color(0xFFFFF4E5), Color(0xFFFF9800), "Pendiente")
        "accepted" -> Triple(Color(0xFFE8F5E9), Color(0xFF4CAF50), "Confirmada")
        "completed" -> Triple(Color(0xFFE3F2FD), Color(0xFF2196F3), "Completada")
        "cancelled" -> Triple(Color(0xFFFFEBEE), Color(0xFFF44336), "Cancelada")
        else -> Triple(Color.LightGray, Color.DarkGray, status)
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
