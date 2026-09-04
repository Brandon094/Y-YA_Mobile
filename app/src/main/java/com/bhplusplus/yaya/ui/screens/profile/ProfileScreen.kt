package com.bhplusplus.yaya.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.ui.components.atoms.PoweredByBH
import com.bhplusplus.yaya.ui.components.atoms.YayaSecondaryButton
import com.bhplusplus.yaya.ui.components.atoms.YayaSectionHeader
import com.bhplusplus.yaya.ui.components.molecules.ProfileOptionItem
import com.bhplusplus.yaya.ui.components.molecules.RatingIndicator
import com.bhplusplus.yaya.ui.components.molecules.YayaConfirmationDialog
import com.bhplusplus.yaya.ui.components.molecules.YayaRatingItem
import com.bhplusplus.yaya.ui.components.organisms.ProfileHeroHeader
import com.bhplusplus.yaya.ui.components.organisms.ProfileSectionCard
import com.bhplusplus.yaya.ui.components.organisms.TutorialStep
import com.bhplusplus.yaya.ui.components.organisms.YayaTutorialOverlay
import com.bhplusplus.yaya.utils.TutorialManager

/**
 * PANTALLA DE PERFIL DE USUARIO (Atomic Design Refactor)
 * Orquestada mediante componentes atómicos reutilizables.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onAvailability: () -> Unit,
    onMyOrders: () -> Unit,
    onIncomingRequests: () -> Unit,
    onMyServices: () -> Unit,
    onAdminDashboard: () -> Unit,
    onChatList: () -> Unit,
    onUserManual: () -> Unit,
    onTerms: () -> Unit,
    onPrivacy: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val profile = viewModel.userProfile
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showRatingsSheet by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    // DIÁLOGO DE CONFIRMACIÓN DE CERRAR SESIÓN (Molécula Atómica)
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

    if (showRatingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showRatingsSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Mi Reputación y Reseñas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                RatingIndicator(
                    rating = viewModel.averageRating,
                    totalRatings = viewModel.totalRatings
                )
                Spacer(Modifier.height(16.dp))

                if (viewModel.providerRatings.isEmpty()) {
                    Text(
                        text = "Aún no has recibido reseñas de clientes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)
                    ) {
                        items(viewModel.providerRatings) { rating ->
                            YayaRatingItem(rating = rating)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    // DIÁLOGO DE CONFIRMACIÓN DE BORRADO (Molécula Atómica)
    if (showDeleteDialog) {
        YayaConfirmationDialog(
            title = "¿Eliminar tu cuenta?",
            message = "Esta acción eliminará tu perfil y datos de servicios de YÁYA de forma permanente. No podrás deshacerlo.",
            confirmButtonText = "Eliminar para siempre",
            onConfirm = {
                viewModel.deleteAccount {
                    showDeleteDialog = false
                    onLogout()
                }
            },
            onDismiss = { showDeleteDialog = false },
            isDestructive = true,
            isLoading = viewModel.isDeletingAccount
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { padding ->
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
            ) {
                // Organismo: Cabecera Hero 2.0 (con botón flotante de Lápiz/Editar)
                ProfileHeroHeader(
                    name = profile?.full_name ?: "Usuario YÁYA",
                    email = viewModel.email,
                    avatarUrl = profile?.avatar_url,
                    role = profile?.role ?: "client",
                    averageRating = viewModel.averageRating,
                    totalRatings = viewModel.totalRatings,
                    onEditProfileClick = onEditProfile
                )

                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {

                    // TARJETAS DE ACCESO RÁPIDO (Quick Action Cards para Prestador / Admin)
                    if (profile?.role == "provider" || profile?.role == "admin") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Card 1: Servicios
                            Surface(
                                onClick = onMyServices,
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Work,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "Mis Servicios",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Gestionar",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Card 2: Solicitudes
                            Surface(
                                onClick = onIncomingRequests,
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    BadgedBox(
                                        badge = {
                                            if (viewModel.pendingRequestsCount > 0) {
                                                Badge(
                                                    containerColor = MaterialTheme.colorScheme.primary,
                                                    contentColor = Color.White
                                                ) {
                                                    Text(viewModel.pendingRequestsCount.toString())
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MoveToInbox,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "Solicitudes",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (viewModel.pendingRequestsCount > 0) "${viewModel.pendingRequestsCount} pendientes" else "Al día",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Card 3: Reputación
                            Surface(
                                onClick = { showRatingsSheet = true },
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFFFB800).copy(alpha = 0.12f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB800),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "Reputación",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (viewModel.totalRatings > 0) String.format(java.util.Locale.US, "⭐ %.1f (%d)", viewModel.averageRating, viewModel.totalRatings) else "Sin reseñas",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // PESTAÑAS DE NAVEGACIÓN SEGMENTADA
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        divider = { HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant) },
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("💼 Mi Operación", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("⚙️ Ajustes y Ayuda", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        )
                    }

                    if (selectedTab == 0) {
                        // --- PESTAÑA 0: OPERACIÓN Y ACTIVIDAD ---
                        if (profile?.role == "admin") {
                            YayaSectionHeader("ADMINISTRACIÓN")
                            ProfileSectionCard(modifier = Modifier.padding(bottom = 20.dp)) {
                                ProfileOptionItem(
                                    title = "Panel Administrativo",
                                    icon = Icons.Default.AdminPanelSettings,
                                    onClick = onAdminDashboard,
                                    badgeCount = viewModel.pendingAdminServicesCount
                                )
                            }
                        }

                        if (profile?.role == "provider" || profile?.role == "admin") {
                            YayaSectionHeader("MI TALENTO Y DISPONIBILIDAD")
                            ProfileSectionCard(modifier = Modifier.padding(bottom = 20.dp)) {
                                ProfileOptionItem(
                                    title = "Mi Horario de Trabajo",
                                    icon = Icons.Default.Schedule,
                                    onClick = onAvailability
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                ProfileOptionItem(
                                    title = "Mis Servicios Publicados",
                                    icon = Icons.Default.Work,
                                    onClick = onMyServices
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                ProfileOptionItem(
                                    title = stringResource(R.string.incoming_requests_title),
                                    icon = Icons.Default.MoveToInbox,
                                    onClick = onIncomingRequests,
                                    badgeCount = viewModel.pendingRequestsCount
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                ProfileOptionItem(
                                    title = "Mi Reputación y Reseñas",
                                    icon = Icons.Default.Star,
                                    onClick = { showRatingsSheet = true },
                                    badgeText = if (viewModel.totalRatings > 0) String.format(java.util.Locale.US, "⭐ %.1f (%d)", viewModel.averageRating, viewModel.totalRatings) else "Sin opiniones"
                                )
                            }
                        }

                        YayaSectionHeader("MI ACTIVIDAD DE CLIENTE")
                        ProfileSectionCard(modifier = Modifier.padding(bottom = 20.dp)) {
                            Column {
                                ProfileOptionItem(
                                    title = stringResource(R.string.my_orders_title),
                                    icon = Icons.Default.History,
                                    onClick = onMyOrders
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                ProfileOptionItem(
                                    title = "Mis Mensajes",
                                    icon = Icons.AutoMirrored.Filled.Chat,
                                    onClick = onChatList,
                                    badgeCount = viewModel.unreadMessagesCount
                                )
                            }
                        }
                    } else {
                        // --- PESTAÑA 1: AJUSTES, AYUDA Y LEGAL ---
                        YayaSectionHeader("SEGURIDAD Y CONFIGURACIÓN")
                        ProfileSectionCard(modifier = Modifier.padding(bottom = 20.dp)) {
                            Column {
                                ProfileOptionItem(
                                    title = stringResource(R.string.profile_edit_option),
                                    icon = Icons.Default.Badge,
                                    onClick = onEditProfile
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                ProfileOptionItem(
                                    title = stringResource(R.string.profile_change_password_option),
                                    icon = Icons.Default.Lock,
                                    onClick = onChangePassword
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                ProfileOptionItem(
                                    title = "Manual de Uso de la App",
                                    icon = Icons.AutoMirrored.Filled.MenuBook,
                                    onClick = onUserManual
                                )
                            }
                        }

                        YayaSectionHeader("LEGAL Y SOPORTE")
                        ProfileSectionCard(modifier = Modifier.padding(bottom = 24.dp)) {
                            Column {
                                ProfileOptionItem(
                                    title = stringResource(R.string.legal_terms_title),
                                    icon = Icons.Default.Gavel,
                                    onClick = onTerms
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                ProfileOptionItem(
                                    title = stringResource(R.string.legal_privacy_title),
                                    icon = Icons.Default.PrivacyTip,
                                    onClick = onPrivacy
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                ProfileOptionItem(
                                    title = "Eliminar mi cuenta",
                                    icon = Icons.Default.DeleteForever,
                                    onClick = { showDeleteDialog = true },
                                    isDestructive = true
                                )
                            }
                        }

                        YayaSecondaryButton(
                            text = stringResource(R.string.profile_logout_button),
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(16.dp))
                        
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            PoweredByBH()
                        }
                    }
                }
            }
        }
    }

    // ORGANISMO ATÓMICO: Tutorial In-App (ShowOnce)
    YayaTutorialOverlay(
        tutorialKey = TutorialManager.TUTORIAL_PROFILE_REPUTATION,
        steps = listOf(
            TutorialStep(
                title = "Navegación Modular por Pestañas",
                description = "Usa las pestañas superiores para alternar fácilmente entre tu operación diaria y tus configuraciones."
            ),
            TutorialStep(
                title = "Mi Reputación y Reseñas",
                description = "Consulta tus estrellas promedio y lee los comentarios recibidos por tus clientes desde tu perfil."
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen(
            onEditProfile = {},
            onAvailability = {},
            onMyOrders = {},
            onIncomingRequests = {},
            onMyServices = {},
            onAdminDashboard = {},
            onChatList = {},
            onUserManual = {},
            onTerms = {},
            onPrivacy = {},
            onChangePassword = {},
            onLogout = {},
            onBack = {}
        )
    }
}
