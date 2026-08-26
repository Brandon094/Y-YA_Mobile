package com.bhplusplus.yaya.ui.screens.incoming_requests

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
import coil3.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.data.models.ServiceRequest

/**
 * PANTALLA DE SOLICITUDES RECIBIDAS (VISTA PRESTADOR)
 * Ahora incluye sistema de Contraoferta (Subasta) para negociar el precio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomingRequestsScreen(
    onBack: () -> Unit,
    onChatClick: (String, String) -> Unit, // receiverId, receiverName
    viewModel: IncomingRequestsViewModel = viewModel()
) {
    var showNegotiationDialog by remember { mutableStateOf<ServiceRequest?>(null) }
    var counterPrice by remember { mutableStateOf("") }

    // Dialogo de Contraoferta (Premium UX)
    if (showNegotiationDialog != null) {
        val currentRequest = showNegotiationDialog!!
        
        // Inicializamos con el precio actual de la solicitud
        LaunchedEffect(currentRequest) {
            counterPrice = currentRequest.final_price.toInt().toString()
        }

        AlertDialog(
            onDismissRequest = { showNegotiationDialog = null },
            title = { 
                Text(
                    "Proponer Contraoferta", 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ) 
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Precio propuesto por cliente: $${currentRequest.final_price.toInt()}",
                        fontSize = 13.sp,
                        color = Color.Gray
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
                                counterPrice = (current - 5000).coerceAtLeast(0.0).toInt().toString()
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Icon(Icons.Default.Remove, "Menos")
                        }

                        OutlinedTextField(
                            value = counterPrice,
                            onValueChange = { counterPrice = it.filter { char -> char.isDigit() } },
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
                        "Incrementos de $5,000",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray.copy(alpha = 0.6f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendCounterOffer(currentRequest, counterPrice)
                        showNegotiationDialog = null
                        counterPrice = ""
                    },
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Enviar Propuesta") }
            },
            dismissButton = {
                TextButton(onClick = { showNegotiationDialog = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.incoming_requests_title), fontWeight = FontWeight.Bold) },
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
            } else if (viewModel.requests.isEmpty()) {
                EmptyIncomingRequestsView()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewModel.requests) { request ->
                        IncomingRequestItem(
                            request = request,
                            onAccept = { viewModel.updateRequestStatus(request.id!!, "accepted") },
                            onReject = { viewModel.updateRequestStatus(request.id!!, "cancelled") },
                            onNegotiate = { showNegotiationDialog = request },
                            onChat = {
                                onChatClick(request.client_id, request.client?.full_name ?: "Cliente")
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Cada tarjeta de solicitud para el prestador.
 */
@Composable
fun IncomingRequestItem(
    request: ServiceRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onNegotiate: () -> Unit,
    onChat: () -> Unit
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (request.client?.avatar_url != null) {
                            AsyncImage(
                                model = request.client.avatar_url,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person, 
                                contentDescription = null, 
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = request.client?.full_name ?: "Cliente",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                
                IconButton(onClick = onChat) {
                    Icon(Icons.Default.Chat, contentDescription = "Chatear", tint = MaterialTheme.colorScheme.primary)
                }

                StatusBadge(request.status)
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))

            Text(text = request.services?.title ?: "Servicio", fontWeight = FontWeight.Bold)
            Text(text = request.service_address, fontSize = 14.sp, color = Color.Gray)
            
            Spacer(Modifier.height(12.dp))

            // MOSTRAR LA OFERTA ACTUAL
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Propuesta actual:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = request.request_description ?: "Sin detalles", fontSize = 14.sp)
                }
            }

            // ACCIONES
            if (request.status == "pending") {
                Spacer(Modifier.height(16.dp))
                
                // Botón de Subasta/Negociación
                OutlinedButton(
                    onClick = onNegotiate,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Hacer Contraoferta")
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(stringResource(R.string.incoming_requests_reject))
                    }
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.incoming_requests_accept))
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
        "accepted" -> Triple(Color(0xFFE8F5E9), Color(0xFF4CAF50), "Aceptada")
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
fun EmptyIncomingRequestsView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Spacer(Modifier.height(16.dp))
        Text(text = stringResource(R.string.incoming_requests_empty), color = Color.Gray)
    }
}
