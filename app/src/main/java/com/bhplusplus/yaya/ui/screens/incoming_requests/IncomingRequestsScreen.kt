package com.bhplusplus.yaya.ui.screens.incoming_requests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.ui.components.RequestItemShimmer
import com.bhplusplus.yaya.ui.components.atoms.YayaAvatar
import com.bhplusplus.yaya.ui.components.atoms.YayaStatusBadge
import com.bhplusplus.yaya.ui.components.molecules.DetailRow
import com.bhplusplus.yaya.ui.theme.YYATheme
import androidx.compose.ui.tooling.preview.Preview
import com.bhplusplus.yaya.data.models.ServiceRequest

/**
 * PANTALLA DE SOLICITUDES RECIBIDAS (VISTA PRESTADOR)
 * Arquitectura MVVM: La View solo renderiza el estado procesado del ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomingRequestsScreen(
    onBack: () -> Unit,
    onChatClick: (String, String) -> Unit, // receiverId, receiverName
    viewModel: IncomingRequestsViewModel = viewModel()
) {
    var showNegotiationDialog by remember { mutableStateOf<IncomingRequestUiState?>(null) }
    var counterPrice by remember { mutableStateOf("") }
    val pullToRefreshState = rememberPullToRefreshState()

    if (showNegotiationDialog != null) {
        val current = showNegotiationDialog!!
        
        LaunchedEffect(current) {
            counterPrice = current.domain.final_price.toInt().toString()
        }

        AlertDialog(
            onDismissRequest = { showNegotiationDialog = null },
            title = { Text("Proponer Contraoferta", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Precio propuesto por cliente: ${current.formattedPrice}", fontSize = 13.sp, color = Color.Gray)
                    Spacer(Modifier.height(20.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilledIconButton(
                            onClick = {
                                val price = counterPrice.toDoubleOrNull() ?: 0.0
                                counterPrice = (price - 5000).coerceAtLeast(0.0).toInt().toString()
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) { Icon(Icons.Default.Remove, "Menos") }

                        OutlinedTextField(
                            value = counterPrice,
                            onValueChange = { counterPrice = it.filter { char -> char.isDigit() } },
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
                    Text("Incrementos de $5,000", style = MaterialTheme.typography.labelSmall, color = Color.Gray.copy(alpha = 0.6f))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendCounterOffer(current.domain, counterPrice)
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
        PullToRefreshBox(
            isRefreshing = viewModel.isLoading,
            onRefresh = { viewModel.fetchIncomingRequests() },
            state = pullToRefreshState,
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            ) {
                if (viewModel.isLoading && viewModel.requests.isEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(5) { // Aumentado a 5 para mayor densidad visual
                            RequestItemShimmer()
                        }
                    }
                } else if (viewModel.requests.isEmpty()) {
                    EmptyIncomingRequestsView()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(viewModel.requests) { uiState ->
                            IncomingRequestItem(
                                state = uiState,
                                onAccept = { 
                                    when (uiState.status) {
                                        "in_progress" -> viewModel.updateRequestStatus(uiState.domain.id!!, "completed")
                                        "pending" -> viewModel.updateRequestStatus(uiState.domain.id!!, "accepted")
                                        else -> { /* Nada */ }
                                    }
                                },
                                onReject = { viewModel.updateRequestStatus(uiState.domain.id!!, "cancelled") },
                                onNegotiate = { showNegotiationDialog = uiState },
                                onChat = {
                                    onChatClick(uiState.domain.client_id, uiState.clientName)
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
 * Cada tarjeta de solicitud para el prestador. Recibe un [IncomingRequestUiState].
 */
@Composable
fun IncomingRequestItem(
    state: IncomingRequestUiState,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onNegotiate: () -> Unit,
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
                    YayaAvatar(
                        imageUrl = state.clientAvatarUrl,
                        size = 52.dp
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(text = state.clientName, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = state.serviceTitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                YayaStatusBadge(state.status)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DetailRow(Icons.Default.LocationOn, state.address)
                DetailRow(Icons.Default.Payments, "Propuesta actual: ${state.formattedPrice}")
                DetailRow(Icons.Default.Event, state.formattedDate)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "ESTADO DE LA PROPUESTA", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), letterSpacing = 0.5.sp)
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

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // BOTÓN CHAT (Siempre visible en estos estados)
                    IconButton(
                        onClick = onChat,
                        modifier = Modifier.size(52.dp).background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
                    ) { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chatear", tint = MaterialTheme.colorScheme.onSecondaryContainer) }

                    when (state.status) {
                        "in_progress" -> {
                            // BOTÓN FINALIZAR (Solo en progreso)
                            Button(
                                onClick = onAccept, // Llama a 'completed'
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                            ) {
                                Icon(Icons.Default.DoneAll, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Finalizar Trabajo", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        "accepted" -> {
                            // ESPERA DE CONFIRMACIÓN DEL CLIENTE
                            Surface(
                                modifier = Modifier.weight(1f).height(52.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("Esperando al Cliente...", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                }
                            }
                        }
                        else -> { // Estado 'pending'
                            // BOTÓN NEGOCIAR
                            OutlinedButton(
                                onClick = onNegotiate,
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Gavel, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Negociar", fontWeight = FontWeight.Bold)
                            }

                            // BOTÓN ACEPTAR OFERTA
                            if (state.isClientOfferPending) {
                                Button(
                                    onClick = onAccept, // Llama a 'accepted'
                                    modifier = Modifier.weight(1.1f).height(52.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = ButtonDefaults.buttonElevation(4.dp)
                                ) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Aceptar", fontWeight = FontWeight.ExtraBold)
                                }
                            } else {
                                TextButton(onClick = onReject, modifier = Modifier.height(52.dp)) {
                                    Text(stringResource(R.string.incoming_requests_reject), color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
                
                // Opción de cancelar (abajo pequeño)
                if (state.status == "accepted" || state.status == "in_progress" || state.isClientOfferPending) {
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onReject, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                        Text(
                            text = when (state.status) {
                                "accepted" -> "Cancelar Servicio"
                                "in_progress" -> "Cancelar Trabajo en Curso"
                                else -> stringResource(R.string.incoming_requests_reject)
                            }, 
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyIncomingRequestsView() {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Spacer(Modifier.height(16.dp))
        Text(text = stringResource(R.string.incoming_requests_empty), color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun IncomingRequestsPreview() {
    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Color.White)) {
            Text("Vista Previa: Solicitudes Recibidas")
        }
    }
}
