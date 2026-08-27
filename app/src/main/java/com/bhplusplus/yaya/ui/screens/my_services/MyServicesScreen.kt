package com.bhplusplus.yaya.ui.screens.my_services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.ui.components.ServiceItemShimmer
import com.bhplusplus.yaya.ui.components.molecules.EmptyStateView
import com.bhplusplus.yaya.ui.components.organisms.MyServiceCard

/**
 * PANTALLA DE MIS SERVICIOS (VISTA PRESTADOR)
 * Arquitectura MVVM: La View solo renderiza el estado procesado del ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyServicesScreen(
    onBack: () -> Unit,
    onEditService: (String) -> Unit, // Navega a la pantalla de edición
    viewModel: MyServicesViewModel = viewModel()
) {
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Servicios Publicados", fontWeight = FontWeight.Bold) },
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
            onRefresh = { viewModel.fetchMyServices() },
            state = pullToRefreshState,
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            ) {
                if (viewModel.isLoading && viewModel.services.isEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(3) {
                            ServiceItemShimmer()
                        }
                    }
                } else if (viewModel.services.isEmpty()) {
                    EmptyStateView(
                        title = "Aún no has publicado servicios",
                        description = "¡Publica tu primer talento ahora!",
                        icon = Icons.Default.WorkOutline
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(viewModel.services) { uiState ->
                            MyServiceCard(
                                state = uiState,
                                onToggleStatus = { viewModel.toggleServiceStatus(uiState.domain.id!!, uiState.domain.status) },
                                onDelete = { viewModel.deleteService(uiState.domain.id!!) },
                                onEdit = { onEditService(uiState.domain.id!!) }
                            )
                        }
                    }
                }
            }
        }
    }
}
