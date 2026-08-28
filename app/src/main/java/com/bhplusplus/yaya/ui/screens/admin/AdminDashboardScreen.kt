package com.bhplusplus.yaya.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.models.UserProfile
import com.bhplusplus.yaya.ui.components.AdminPendingItemShimmer
import com.bhplusplus.yaya.ui.components.ReportItemShimmer
import com.bhplusplus.yaya.ui.components.UserItemShimmer
import com.bhplusplus.yaya.ui.components.molecules.EmptyStateView
import com.bhplusplus.yaya.ui.components.molecules.UserListItem
import com.bhplusplus.yaya.ui.components.organisms.AdminServiceCard
import com.bhplusplus.yaya.ui.components.organisms.AdminTopBar
import com.bhplusplus.yaya.ui.components.organisms.ReportSummaryCard

/**
 * PANTALLA DE DASHBOARD ADMINISTRATIVO (Atomic Design Refactor)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    AdminDashboardContent(
        isLoading = viewModel.isLoading,
        pendingServices = viewModel.pendingServices,
        allProfiles = viewModel.allProfiles,
        reportsSummary = viewModel.reportedUsersSummaries,
        onApproveService = { viewModel.approveService(it) },
        onRejectService = { viewModel.rejectService(it) },
        onSuspendUser = { viewModel.suspendUser(it) },
        onDeleteUser = { viewModel.deleteUserAccount(it) },
        onWarnUser = { id, count -> viewModel.warnUser(id, count) },
        onBack = onBack,
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContent(
    isLoading: Boolean,
    pendingServices: List<Service>,
    allProfiles: List<UserProfile>,
    reportsSummary: List<ReportedUserSummary>,
    onApproveService: (String) -> Unit,
    onRejectService: (String) -> Unit,
    onSuspendUser: (String) -> Unit,
    onDeleteUser: (String) -> Unit,
    onWarnUser: (String, Int) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pendientes", "Usuarios", "Reportes")

    Scaffold(
        topBar = {
            // Organismo: Barra superior admin
            AdminTopBar(onBack = onBack, onLogout = onLogout)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tabs de Navegación Interna
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            if (isLoading) {
                when (selectedTab) {
                    0 -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(3) { AdminPendingItemShimmer() }
                    }
                    1 -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(5) { UserItemShimmer() }
                    }
                    2 -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(3) { ReportItemShimmer() }
                    }
                }
            } else {
                when (selectedTab) {
                    0 -> PendingServicesList(
                        services = pendingServices,
                        onApprove = onApproveService,
                        onReject = onRejectService
                    )
                    1 -> UsersList(profiles = allProfiles)
                    2 -> ReportsList(
                        summaries = reportsSummary,
                        onSuspend = onSuspendUser,
                        onDelete = onDeleteUser,
                        onWarn = onWarnUser
                    )
                }
            }
        }
    }
}

@Composable
fun PendingServicesList(
    services: List<Service>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {
    if (services.isEmpty()) {
        EmptyStateView(
            title = "Sin servicios pendientes",
            description = "No hay solicitudes de aprobación por ahora."
        )
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(services) { service ->
                // Organismo: Tarjeta de servicio admin
                AdminServiceCard(service, onApprove, onReject)
            }
        }
    }
}

@Composable
fun UsersList(profiles: List<UserProfile>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(profiles) { profile ->
            // Molécula: Item de lista de usuario
            UserListItem(profile = profile)
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.2f))
        }
    }
}

@Composable
fun ReportsList(
    summaries: List<ReportedUserSummary>,
    onSuspend: (String) -> Unit,
    onDelete: (String) -> Unit,
    onWarn: (String, Int) -> Unit
) {
    if (summaries.isEmpty()) {
        EmptyStateView(
            title = "Limpieza total",
            description = "No hay reportes de mal comportamiento."
        )
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(summaries) { summary ->
                // Organismo: Tarjeta de resumen de reportes
                ReportSummaryCard(summary, onSuspend, onDelete, onWarn)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardPreview() {
    val sampleServices = listOf(
        Service(id = "1", title = "Reparación de PC", description = "Técnico a domicilio", price = 50000.0)
    )
    val sampleProfiles = listOf(
        UserProfile(id = "1", full_name = "Brandon Daza", role = "admin")
    )

    MaterialTheme {
        AdminDashboardContent(
            isLoading = false,
            pendingServices = sampleServices,
            allProfiles = sampleProfiles,
            reportsSummary = emptyList(),
            onApproveService = {},
            onRejectService = {},
            onSuspendUser = {},
            onDeleteUser = {},
            onWarnUser = { _, _ -> },
            onBack = {},
            onLogout = {}
        )
    }
}
