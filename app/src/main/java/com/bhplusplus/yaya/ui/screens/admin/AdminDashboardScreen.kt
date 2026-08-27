package com.bhplusplus.yaya.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.bhplusplus.yaya.data.models.Report
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.models.UserProfile
import com.bhplusplus.yaya.ui.components.AdminPendingItemShimmer
import com.bhplusplus.yaya.ui.components.ReportItemShimmer
import com.bhplusplus.yaya.ui.components.UserItemShimmer
import com.bhplusplus.yaya.utils.FormatterUtils

/**
 * PANTALLA DE DASHBOARD ADMINISTRATIVO (Hito 5)
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
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pendientes", "Usuarios", "Reportes")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Administrativo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary,
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
                        onDelete = onDeleteUser
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
        shape = RoundedCornerShape(16.dp), // Más redondeado
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) { // Top para fuentes grandes
                if (service.provider?.avatar_url != null) {
                    AsyncImage(
                        model = service.provider.avatar_url,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(12.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.title, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Por: ${service.provider?.full_name ?: "Desconocido"}", 
                        fontSize = 12.sp, 
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = service.description, 
                fontSize = 14.sp, 
                color = MaterialTheme.colorScheme.onSurfaceVariant, 
                maxLines = 3,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Precio: ${FormatterUtils.formatCurrency(service.price)}", 
                    fontWeight = FontWeight.ExtraBold, 
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp
                )
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = { onReject(service.id!!) },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Red.copy(alpha = 0.1f))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Rechazar", tint = Color.Red)
                }
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = { onApprove(service.id!!) },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f))
                ) {
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
                leadingContent = { 
                    if (profile.avatar_url != null) {
                        AsyncImage(
                            model = profile.avatar_url,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Person, null)
                    }
                },
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
fun ReportsList(
    summaries: List<ReportedUserSummary>,
    onSuspend: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    if (summaries.isEmpty()) {
        EmptyState("No hay reportes de mal comportamiento.")
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(summaries) { summary ->
                ReportSummaryCard(summary, onSuspend, onDelete)
            }
        }
    }
}

@Composable
fun ReportSummaryCard(
    summary: ReportedUserSummary,
    onSuspend: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    val (severityColor, actionLabel) = when {
        summary.count >= 5 -> Color.Red to "ELIMINACIÓN RECOMENDADA"
        summary.count >= 3 -> Color(0xFFFF9800) to "SUSPENSIÓN RECOMENDADA"
        else -> Color(0xFFFFC107) to "LLAMADO DE ATENCIÓN"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(severityColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (summary.profile?.avatar_url != null) {
                        AsyncImage(
                            model = summary.profile.avatar_url,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Person, null, tint = severityColor)
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(summary.profile?.full_name ?: "Usuario Desconocido", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text("ID: ${summary.profile?.id?.take(8)}...", fontSize = 12.sp, color = Color.Gray)
                }
                
                Surface(
                    color = severityColor,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${summary.count} REPORTES",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = severityColor,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(12.dp))

            // Lista de motivos (Previsualización)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                summary.reports.take(3).forEach { report ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(Icons.Default.Error, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(Modifier.width(8.dp))
                        Text(report.reason, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (summary.count > 3) {
                    Text("... y ${summary.count - 3} más", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(start = 22.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { onSuspend(summary.profile?.id!!) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, severityColor)
                ) {
                    Text("Suspender", color = severityColor)
                }

                Button(
                    onClick = { onDelete(summary.profile?.id!!) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (summary.count >= 5) Color.Red else Color.Gray)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            }
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
        Service(
            id = "1",
            title = "Reparación de PC",
            description = "Servicio técnico a domicilio",
            price = 50000.0,
        ),
        Service(
            id = "2",
            title = "Limpieza de Sofá",
            description = "Limpieza profunda con vapor",
            price = 80000.0,
        )
    )
    val sampleProfiles = listOf(
        UserProfile(id = "1", full_name = "Brandon Daza", role = "admin"),
        UserProfile(id = "2", full_name = "Juan Perez", role = "provider")
    )

    AdminDashboardContent(
        isLoading = false,
        pendingServices = sampleServices,
        allProfiles = sampleProfiles,
        reportsSummary = emptyList(),
        onApproveService = {},
        onRejectService = {},
        onSuspendUser = {},
        onDeleteUser = {},
        onBack = {},
        onLogout = {}
    )
}
