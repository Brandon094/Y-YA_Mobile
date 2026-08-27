package com.bhplusplus.yaya.ui.screens.my_orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.bhplusplus.yaya.ui.components.molecules.YayaNegotiationDialog
import com.bhplusplus.yaya.ui.components.molecules.YayaRatingDialog
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
    var showRatingDialog by remember { mutableStateOf<MyOrderUiState?>(null) }
    val pullToRefreshState = rememberPullToRefreshState()

    if (showRatingDialog != null) {
        YayaRatingDialog(
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

        YayaNegotiationDialog(
            title = "Ajustar mi Oferta",
            initialPrice = current.domain.final_price,
            minPrice = basePrice,
            subtitle = "Última contraoferta: ${current.formattedPrice}",
            errorLabel = "No puedes ofertar menos del precio base",
            onDismiss = { showNegotiationDialog = null },
            onConfirm = { price ->
                viewModel.sendNewOffer(current.domain, price)
                showNegotiationDialog = null
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

@Preview(showBackground = true)
@Composable
fun MyOrdersPreview() {
    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Color.White)) {
            Text("Vista Previa: Mis Pedidos")
        }
    }
}
