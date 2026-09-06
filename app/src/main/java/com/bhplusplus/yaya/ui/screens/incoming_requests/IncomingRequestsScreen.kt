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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.ui.components.RequestItemShimmer
import com.bhplusplus.yaya.ui.components.molecules.YayaNegotiationDialog
import com.bhplusplus.yaya.ui.components.organisms.IncomingRequestCard
import com.bhplusplus.yaya.ui.components.organisms.TutorialStep
import com.bhplusplus.yaya.ui.components.organisms.YayaTutorialOverlay
import com.bhplusplus.yaya.utils.TutorialManager
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
    var requestsListBounds by remember { mutableStateOf<Rect?>(null) }
    val pullToRefreshState = rememberPullToRefreshState()

    if (showNegotiationDialog != null) {
        val current = showNegotiationDialog!!
        
        YayaNegotiationDialog(
            initialPrice = current.domain.final_price,
            subtitle = "Precio propuesto por cliente: ${current.formattedPrice}",
            onDismiss = { showNegotiationDialog = null },
            onConfirm = { price ->
                viewModel.sendCounterOffer(current.domain, price)
                showNegotiationDialog = null
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
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { coords ->
                                requestsListBounds = coords.boundsInWindow()
                            },
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

    // ORGANISMO ATÓMICO: Tutorial In-App (ShowOnce + Spotlight Cutout)
    YayaTutorialOverlay(
        tutorialKey = TutorialManager.TUTORIAL_INCOMING_REQUESTS,
        steps = listOf(
            TutorialStep(
                title = "Solicitudes de Clientes",
                description = "Revisa los detalles de cada oferta recibida: cliente, ubicación, fecha, hora programada y la propuesta económica ofrecida.",
                targetBounds = requestsListBounds,
                targetCornerRadius = 24.dp,
                targetPadding = 4.dp
            ),
            TutorialStep(
                title = "Aceptar, Negociar o Rechazar",
                description = "Presiona 'Aceptar' para confirmar la cita, 'Negociar' para proponer un nuevo valor, 'Rechazar' si no puedes atenderla, o usa el icono de chat para hablar con el cliente.",
                targetBounds = requestsListBounds,
                targetCornerRadius = 24.dp,
                targetPadding = 4.dp
            )
        )
    )
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
