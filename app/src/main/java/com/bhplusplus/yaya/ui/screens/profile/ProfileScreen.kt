package com.bhplusplus.yaya.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.bhplusplus.yaya.R

/**
 * PANTALLA DE PERFIL DE USUARIO
 * Muestra la información real recuperada de Supabase incluyendo los nuevos campos del MVP.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,    // Navega a la pantalla de edición
    onMyOrders: () -> Unit,       // Navega al historial de pedidos
    onIncomingRequests: () -> Unit, // Navega a solicitudes recibidas (solo prestadores)
    onMyServices: () -> Unit,     // Navega a la gestión de servicios propios
    onChangePassword: () -> Unit, // Navega al flujo de recuperación/cambio
    onLogout: () -> Unit,         // Acción para cerrar sesión
    onBack: () -> Unit,           // Regresa al Home
    viewModel: ProfileViewModel = viewModel()
) {
    val profile = viewModel.userProfile

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.profile_back_desc),
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Sección del Avatar (Imagen de perfil)
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(4.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (profile?.avatar_url != null) {
                        AsyncImage(
                            model = profile.avatar_url,
                            contentDescription = stringResource(R.string.profile_avatar_desc),
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.profile_avatar_desc),
                            modifier = Modifier.size(65.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nombre y Email destacados
                Text(
                    text = profile?.full_name ?: stringResource(R.string.profile_loading),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = viewModel.email,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // TARJETA DE INFORMACIÓN PERSONAL
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = stringResource(R.string.profile_personal_info),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        InfoRow(icon = Icons.Default.Badge, label = stringResource(R.string.profile_document_id_label), value = profile?.document_id ?: stringResource(R.string.profile_not_registered))
                        InfoRow(icon = Icons.Default.Phone, label = stringResource(R.string.profile_phone_label), value = profile?.phone ?: stringResource(R.string.profile_not_registered))
                        InfoRow(icon = Icons.Default.Home, label = stringResource(R.string.profile_address_label), value = profile?.address ?: stringResource(R.string.profile_not_registered_fem))
                        InfoRow(icon = Icons.Default.Cake, label = stringResource(R.string.profile_birth_date_label), value = profile?.birth_date ?: stringResource(R.string.profile_not_registered))
                        InfoRow(icon = Icons.Default.Star, label = stringResource(R.string.profile_role_label), value = if(profile?.role == "provider") stringResource(R.string.profile_role_provider) else stringResource(R.string.profile_role_client))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // LISTA DE OPCIONES NAVEGABLES
                // Si el usuario es prestador, habilitamos la gestión de solicitudes entrantes y servicios propios
                if (profile?.role == "provider" || profile?.role == "admin") {
                    ProfileOption("Mis Servicios Publicados", Icons.Default.Work, onMyServices)
                    ProfileOption(stringResource(R.string.incoming_requests_title), Icons.Default.MoveToInbox, onIncomingRequests)
                }

                ProfileOption(stringResource(R.string.my_orders_title), Icons.Default.History, onMyOrders)
                ProfileOption(stringResource(R.string.profile_edit_option), Icons.Default.Edit, onEditProfile)
                ProfileOption(stringResource(R.string.profile_change_password_option), Icons.Default.Lock, onChangePassword)
                
                Spacer(modifier = Modifier.height(40.dp))

                // Botón de Cerrar Sesión
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.profile_logout_button), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * COMPONENTE PARA FILAS DE INFORMACIÓN DETALLADA
 */
@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon, 
            contentDescription = null, 
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), 
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label, 
                fontSize = 12.sp, 
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value, 
                fontSize = 16.sp, 
                color = MaterialTheme.colorScheme.onSurface, 
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * COMPONENTE REUTILIZABLE PARA LAS OPCIONES DEL PERFIL
 */
@Composable
fun ProfileOption(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de la opción con fondo circular suave
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )

            // Indicador de navegación (Flecha)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        onEditProfile = {},
        onMyOrders = {},
        onIncomingRequests = {},
        onMyServices = {},
        onChangePassword = {},
        onLogout = {},
        onBack = {}
    )
}
