package com.bhplusplus.yaya.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.SupabaseManager
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
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
    onCreateServiceClick: () -> Unit,  // Navega a la creación de servicio
    onLogout: () -> Unit,              // Regresa al login tras cerrar sesión
    viewModel: HomeViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            // Barra superior con acceso al perfil y notificaciones
            TopAppBar(
                title = { Text(stringResource(R.string.home_top_bar_title)) },
                navigationIcon = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.home_profile_desc),
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
        floatingActionButton = {
            // LÓGICA DE PERMISOS: Solo mostramos el botón si el usuario es 'provider' (Prestador) o 'admin'
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
                        onClick = onLogout,
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
        // Contenido principal
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. BARRA DE BÚSQUEDA
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar servicios...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (viewModel.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // 2. SELECTOR DE CATEGORÍAS (Horizontal)
            Text(
                text = "Categorías",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Opción "Todas"
                item {
                    FilterChip(
                        selected = viewModel.selectedCategoryId == null,
                        onClick = { viewModel.onCategorySelect(null) },
                        label = { Text("Todas") }
                    )
                }
                // Categorías de la base de datos
                items(viewModel.categories) { category ->
                    FilterChip(
                        selected = viewModel.selectedCategoryId == category.id,
                        onClick = { viewModel.onCategorySelect(category.id) },
                        label = { Text(category.name) }
                    )
                }
            }

            Text(
                text = stringResource(R.string.home_select_service_label),
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
                // Lista filtrada
                LazyColumn {
                    items(viewModel.filteredServices) { service ->
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
    HomeScreen(onServiceClick = {}, onProfileClick = {}, onCreateServiceClick = {}, onLogout = {})
}
