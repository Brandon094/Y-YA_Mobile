package com.bhplusplus.yaya.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.models.UserProfile
import com.bhplusplus.yaya.data.models.Report

/**
 * PANTALLA DE DASHBOARD ADMINISTRATIVO (Hito 5)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    AdminDashboardContent(
        isLoading = viewModel.isLoading,
        pendingServices = viewModel.pendingServices,
        allProfiles = viewModel.allProfiles,
        reports = viewModel.reports,
        onApproveService = { viewModel.approveService(it) },
        onRejectService = { viewModel.rejectService(it) },
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContent(
    isLoading: Boolean,
    pendingServices: List<Service>,
    allProfiles: List<UserProfile>,
    reports: List<Report>,
    onApproveService: (String) -> Unit,
    onRejectService: (String) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pendientes", "Usuarios", "Reportes")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Administrativo YÁYA", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondary
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
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (selectedTab) {
                    0 -> PendingServicesList(
                        services = pendingServices,
                        onApprove = onApproveService,
                        onReject = onRejectService
                    )
                    1 -> UsersList(profiles = allProfiles)
                    2 -> ReportsList(reports = reports)
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
        EmptyState("No hay servicios pendientes de aprobación.")
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(services) { service ->
                AdminServiceCard(service, onApprove, onReject)
            }
        }
    }
}

@Composable
fun AdminServiceCard(service: Service, onApprove: (String) -> Unit, onReject: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(service.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(service.description, fontSize = 14.sp, color = Color.Gray, maxLines = 2)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Precio: $${service.price}", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { onReject(service.id!!) }) {
                    Icon(Icons.Default.Close, contentDescription = "Rechazar", tint = Color.Red)
                }
                IconButton(onClick = { onApprove(service.id!!) }) {
                    Icon(Icons.Default.Check, contentDescription = "Aprobar", tint = Color(0xFF4CAF50))
                }
            }
        }
    }
}

@Composable
fun UsersList(profiles: List<UserProfile>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(profiles) { profile ->
            ListItem(
                headlineContent = { Text(profile.full_name) },
                supportingContent = { Text("Rol: ${profile.role} | ID: ${profile.document_id ?: "N/A"}") },
                leadingContent = { Icon(Icons.Default.Person, null) },
                trailingContent = {
                    if (profile.role == "admin") {
                        Badge(containerColor = MaterialTheme.colorScheme.primary) { Text("Admin") }
                    }
                }
            )
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.2f))
        }
    }
}

@Composable
fun ReportsList(reports: List<Report>) {
    if (reports.isEmpty()) {
        EmptyState("No hay reportes de mal comportamiento.")
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(reports) { report ->
                ReportCard(report)
            }
        }
    }
}

@Composable
fun ReportCard(report: Report) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Denunciado: ${report.reported?.full_name ?: "ID: " + report.reported_user_id}", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text("Motivo: ${report.reason}", fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("Reportado por: ${report.reporter?.full_name ?: "Usuario"}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Warning, null, modifier = Modifier.size(48.dp), tint = Color.Gray)
            Text(message, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardPreview() {
    val sampleServices = listOf(
        Service(id = "1", title = "Reparación de PC", description = "Servicio técnico a domicilio", price = 50000.0),
        Service(id = "2", title = "Limpieza de Sofá", description = "Limpieza profunda con vapor", price = 80000.0)
    )
    val sampleProfiles = listOf(
        UserProfile(id = "1", full_name = "Brandon Daza", role = "admin"),
        UserProfile(id = "2", full_name = "Juan Perez", role = "provider")
    )

    AdminDashboardContent(
        isLoading = false,
        pendingServices = sampleServices,
        allProfiles = sampleProfiles,
        reports = emptyList(),
        onApproveService = {},
        onRejectService = {},
        onLogout = {}
    )
}
