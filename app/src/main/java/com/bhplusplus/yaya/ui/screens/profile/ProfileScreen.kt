package com.bhplusplus.yaya.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
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
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.ui.components.atoms.PoweredByBH
import com.bhplusplus.yaya.ui.components.atoms.YayaSecondaryButton
import com.bhplusplus.yaya.ui.components.atoms.YayaSectionHeader
import com.bhplusplus.yaya.ui.components.molecules.ProfileOptionItem
import com.bhplusplus.yaya.ui.components.organisms.ProfileHeroHeader
import com.bhplusplus.yaya.ui.components.organisms.ProfileSectionCard

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
    onTerms: () -> Unit,
    onPrivacy: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val profile = viewModel.userProfile
    var showDeleteDialog by remember { mutableStateOf(false) }

    // DIÁLOGO DE CONFIRMACIÓN DE BORRADO
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar tu cuenta?", fontWeight = FontWeight.Bold) },
            text = { 
                Text("Esta acción eliminará tu perfil y datos de servicios de YÁYA de forma permanente. No podrás deshacerlo.") 
            },
            confirmButton = {
                Button(
                    onClick = { 
                        viewModel.deleteAccount {
                            showDeleteDialog = false
                            onLogout()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    enabled = !viewModel.isDeletingAccount
                ) {
                    if (viewModel.isDeletingAccount) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Eliminar para siempre", color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }, enabled = !viewModel.isDeletingAccount) {
                    Text("Cancelar")
                }
            }
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
                // Organismo: Cabecera Hero
                ProfileHeroHeader(
                    name = profile?.full_name ?: "Usuario YÁYA",
                    email = viewModel.email,
                    avatarUrl = profile?.avatar_url,
                    role = profile?.role ?: "client"
                )

                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                    
                    // --- SECCIÓN: ADMINISTRACIÓN (Solo Admin) ---
                    if (profile?.role == "admin") {
                        YayaSectionHeader("ADMINISTRACIÓN")
                        ProfileSectionCard(
                            modifier = Modifier.padding(bottom = 24.dp)
                        ) {
                            ProfileOptionItem(
                                title = "Panel Administrativo",
                                icon = Icons.Default.AdminPanelSettings,
                                onClick = onAdminDashboard,
                                badgeCount = viewModel.pendingAdminServicesCount
                            )
                        }
                    }

                    // --- SECCIÓN: GESTIÓN DE TALENTO (Solo Prestadores/Admin) ---
                    if (profile?.role == "provider" || profile?.role == "admin") {
                        YayaSectionHeader("MI TALENTO")
                        ProfileSectionCard(
                            modifier = Modifier.padding(bottom = 24.dp)
                        ) {
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
                        }
                    }

                    // --- SECCIÓN: MI ACTIVIDAD ---
                    YayaSectionHeader("MI ACTIVIDAD")
                    ProfileSectionCard(
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
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

                    // --- SECCIÓN: CONFIGURACIÓN DE CUENTA ---
                    YayaSectionHeader("CONFIGURACIÓN")
                    ProfileSectionCard(
                        modifier = Modifier.padding(bottom = 40.dp)
                    ) {
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
                            
                            // LEGAL
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

                    // Átomo: Botón Secundario (Outlined) para Salida
                    YayaSecondaryButton(
                        text = stringResource(R.string.profile_logout_button),
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))
                    
                    // Sello de Marca Atómico
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
            onTerms = {},
            onPrivacy = {},
            onChangePassword = {},
            onLogout = {},
            onBack = {}
        )
    }
}
