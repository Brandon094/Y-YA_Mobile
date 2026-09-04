package com.bhplusplus.yaya.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.ui.components.ServiceItemShimmer
import com.bhplusplus.yaya.ui.components.molecules.CategorySelector
import com.bhplusplus.yaya.ui.components.molecules.EmptyStateView
import com.bhplusplus.yaya.ui.components.molecules.YayaConfirmationDialog
import com.bhplusplus.yaya.ui.components.organisms.HomeTopBar
import com.bhplusplus.yaya.ui.components.organisms.SearchBarIntegrated
import com.bhplusplus.yaya.ui.components.organisms.ServiceCard
import com.bhplusplus.yaya.ui.components.organisms.TutorialStep
import com.bhplusplus.yaya.ui.components.organisms.YayaTutorialOverlay
import com.bhplusplus.yaya.utils.TutorialManager
import com.bhplusplus.yaya.utils.ValidationUtils

/**
 * PANTALLA PRINCIPAL (HOME)
 * Orquestada bajo Atomic Design.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onServiceClick: (Service) -> Unit,
    onProfileClick: () -> Unit,
    onMyOrders: () -> Unit,
    onIncomingRequestsClick: () -> Unit,
    onChatListClick: () -> Unit,
    onCreateServiceClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val userName = remember(viewModel.userProfile?.full_name) {
        viewModel.userProfile?.full_name?.substringBefore(" ") ?: ""
    }

    var showMunicipalityDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val municipalities = remember { ValidationUtils.HUILA_MUNICIPALITIES + "Todos" }

    if (showLogoutDialog) {
        YayaConfirmationDialog(
            title = "¿Cerrar sesión?",
            message = "¿Estás seguro de que deseas salir de tu cuenta en YÁYA?",
            confirmButtonText = "Cerrar sesión",
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showMunicipalityDialog) {
        AlertDialog(
            onDismissRequest = { showMunicipalityDialog = false },
            title = { Text("Selecciona tu Municipio", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                ) {
                    items(municipalities) { muni ->
                        val isSelected = viewModel.selectedMunicipality.equals(muni, ignoreCase = true) ||
                                (muni == "Todos" && (viewModel.selectedMunicipality == null || viewModel.selectedMunicipality == "Todos"))
                        Surface(
                            onClick = {
                                viewModel.onMunicipalitySelect(if (muni == "Todos") "Todos" else muni)
                                showMunicipalityDialog = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = if (muni == "Todos") "Todos los municipios" else muni,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMunicipalityDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            // Organismo: Barra superior
            HomeTopBar(
                userName = userName,
                avatarUrl = viewModel.userProfile?.avatar_url,
                unreadMessagesCount = viewModel.unreadMessagesCount,
                notificationCount = viewModel.notificationCount,
                selectedMunicipality = viewModel.selectedMunicipality,
                onMunicipalityClick = { showMunicipalityDialog = true },
                onProfileClick = onProfileClick,
                onChatListClick = onChatListClick,
                onNotificationsClick = {
                    if (viewModel.userRole == "provider" || viewModel.userRole == "admin") {
                        onIncomingRequestsClick()
                    } else {
                        onMyOrders()
                    }
                }
            )
        },
        floatingActionButton = {
            if (viewModel.userRole == "provider" || viewModel.userRole == "admin") {
                FloatingActionButton(
                    onClick = onCreateServiceClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Crear Servicio")
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showLogoutDialog = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = stringResource(R.string.home_logout_desc)
                        )
                    }
                }
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = viewModel.isLoading,
            onRefresh = { viewModel.loadData() },
            state = pullToRefreshState,
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Organismo: Barra de búsqueda integrada
                SearchBarIntegrated(
                    query = viewModel.searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChange(it) }
                )

                Spacer(Modifier.height(16.dp))

                // Molécula: Selector de categorías
                Text(
                    text = "¿Qué buscas hoy?",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(Modifier.height(12.dp))

                CategorySelector(
                    categories = viewModel.categories,
                    selectedCategoryId = viewModel.selectedCategoryId,
                    onCategorySelect = { viewModel.onCategorySelect(it) }
                )

                Spacer(Modifier.height(8.dp))

                if (viewModel.isLoading && viewModel.filteredServices.isEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(5) {
                            ServiceItemShimmer()
                        }
                    }
                } else if (viewModel.filteredServices.isEmpty()) {
                    // Molécula: Vista de estado vacío
                    EmptyStateView(
                        title = "No encontramos servicios",
                        description = if (viewModel.searchQuery.isNotEmpty()) 
                            "No hay resultados para \"${viewModel.searchQuery}\". Prueba con otra palabra." 
                            else "Aún no hay servicios disponibles en esta categoría.",
                        actionButton = if (viewModel.searchQuery.isNotEmpty()) {
                            {
                                Button(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Text("Limpiar búsqueda")
                                }
                            }
                        } else null
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(viewModel.filteredServices) { uiState ->
                            // Organismo: Tarjeta de servicio
                            ServiceCard(
                                state = uiState, 
                                onClick = { onServiceClick(uiState.domain) }
                            )
                        }
                    }
                }
            }
        }
    }

    // ORGANISMO ATÓMICO: Tutorial In-App (ShowOnce)
    YayaTutorialOverlay(
        tutorialKey = TutorialManager.TUTORIAL_HOME_MUNICIPIO,
        steps = listOf(
            TutorialStep(
                title = "Filtro Geográfico por Municipio",
                description = "Toca el selector de municipio en la barra superior para explorar servicios disponibles en La Plata, Nátaga, Neiva u otra localidad."
            ),
            TutorialStep(
                title = "Explora por Categorías y Búsqueda",
                description = "Usa el buscador o los botones de categorías para encontrar de forma rápida el talento o servicio que necesitas."
            )
        )
    )
}
