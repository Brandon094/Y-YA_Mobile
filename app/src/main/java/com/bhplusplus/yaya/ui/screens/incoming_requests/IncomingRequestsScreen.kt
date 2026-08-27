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
import com.bhplusplus.yaya.ui.components.organisms.IncomingRequestCard
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
                            // Organismo: Tarjeta de solicitud recibida
                            IncomingRequestCard(
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
