package com.example.myapplication.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.models.Service

import com.example.myapplication.data.SupabaseManager
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.runtime.rememberCoroutineScope

/**
 * PANTALLA PRINCIPAL (HOME)
 * Muestra el catálogo de servicios disponibles para contratar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onServiceClick: (Service) -> Unit, // Navega a los detalles del servicio
    onProfileClick: () -> Unit,        // Navega al perfil del usuario
    onLogout: () -> Unit,              // Regresa al login tras cerrar sesión
    viewModel: HomeViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            // Barra superior con acceso al perfil y notificaciones
            TopAppBar(
                title = { Text("Buscar servicio") },
                navigationIcon = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.Person, 
                            contentDescription = "Mi Perfil",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Próxima funcionalidad */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications, 
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
        },
        bottomBar = {
            // Barra inferior que contiene solo el botón de cerrar sesión
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                SupabaseManager.client.auth.signOut()
                                onLogout()
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            }
        }
    ) { padding ->
        // Contenido principal: Lista de servicios
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "Selecciona el servicio a contratar:",
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Si está cargando datos de Supabase, muestra un Spinner
            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                // Lista de desplazamiento eficiente (Recycler View en Compose)
                LazyColumn {
                    items(viewModel.services) { service ->
                        ServiceItem(service, onClick = { onServiceClick(service) })
                    }
                }
            }
        }
    }
}

/**
 * COMPONENTE DE TARJETA DE SERVICIO
 * Representa un ítem individual dentro de la lista del Home.
 */
@Composable
fun ServiceItem(service: Service, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo del servicio
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(6.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Información de texto
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = service.title, 
                fontSize = 14.sp, 
                color = MaterialTheme.colorScheme.onSurface, 
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = service.description, 
                fontSize = 12.sp, 
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // Icono indicador de navegación
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(onServiceClick = {}, onProfileClick = {}, onLogout = {})
}
