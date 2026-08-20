package com.bhplusplus.yaya.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.data.models.Service
import androidx.compose.runtime.rememberCoroutineScope
import java.text.NumberFormat
import java.util.Locale

/**
 * PANTALLA PRINCIPAL (HOME)
 * Muestra el catálogo de servicios disponibles para contratar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onServiceClick: (Service) -> Unit, // Navega a los detalles del servicio
    onProfileClick: () -> Unit,        // Navega al perfil del usuario
    onMyOrders: () -> Unit,            // Navega a mis pedidos (Cliente)
    onIncomingRequestsClick: () -> Unit, // Navega a solicitudes recibidas (Prestador)
    onChatListClick: () -> Unit,       // Navega al listado de chats
    onCreateServiceClick: () -> Unit,  // Navega a la creación de servicio
    onLogout: () -> Unit,              // Regresa al login tras cerrar sesión
    viewModel: HomeViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            // Barra superior con acceso al perfil y notificaciones (Solicitudes)
            TopAppBar(
                title = { Text(stringResource(R.string.home_top_bar_title)) },
                navigationIcon = {
                    IconButton(onClick = onProfileClick) {
                        if (viewModel.userProfile?.avatar_url != null) {
                            AsyncImage(
                                model = viewModel.userProfile?.avatar_url,
                                contentDescription = stringResource(R.string.home_profile_desc),
                                modifier = Modifier.size(32.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = stringResource(R.string.home_profile_desc),
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                },
                actions = {
                    // Acceso rápido al listado de chats
                    IconButton(onClick = onChatListClick) {
                        BadgedBox(
                            badge = {
                                if (viewModel.unreadMessagesCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = Color.White
                                    ) {
                                        Text(viewModel.unreadMessagesCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Mis Chats",
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }

                    // Acceso rápido a notificaciones/estados según el rol con Badge (Hito 4)
                    IconButton(onClick = {
                        if (viewModel.userRole == "provider" || viewModel.userRole == "admin") {
                            onIncomingRequestsClick()
                        } else {
                            onMyOrders()
                        }
                    }) {
                        BadgedBox(
                            badge = {
                                if (viewModel.notificationCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = Color.White
                                    ) {
                                        Text(viewModel.notificationCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications, 
                                contentDescription = "Notificaciones",
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
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
            } else if (viewModel.filteredServices.isEmpty()) {
                EmptyServicesView(
                    searchQuery = viewModel.searchQuery,
                    onClearSearch = { viewModel.onSearchQueryChange("") }
                )
            } else {
                // Lista filtrada
                LazyColumn {
                    items(viewModel.filteredServices) { service ->
                        val category = viewModel.categories.find { it.id == service.category_id }
                        ServiceItem(
                            service = service, 
                            categoryName = category?.name,
                            onClick = { onServiceClick(service) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * VISTA DE ESTADO VACÍO (UX)
 * Se muestra cuando no hay servicios que coincidan con la búsqueda o categoría.
 */
@Composable
fun EmptyServicesView(searchQuery: String, onClearSearch: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No encontramos servicios",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (searchQuery.isNotEmpty()) 
                "No hay resultados para \"$searchQuery\". Prueba con otra palabra." 
                else "Aún no hay servicios disponibles en esta categoría.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        if (searchQuery.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onClearSearch) {
                Text("Limpiar búsqueda")
            }
        }
    }
}

/**
 * COMPONENTE DE TARJETA DE SERVICIO
 * Representa un ítem individual dentro de la lista del Home.
 * Ahora más compacto y con información de horarios, precios y días.
 */
@Composable
fun ServiceItem(service: Service, categoryName: String?, onClick: () -> Unit) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CO")) }
    val dayNames = listOf("L", "M", "M", "J", "V", "S", "D")

    // Mapeo dinámico de iconos por categoría
    val categoryIcon = remember(categoryName) {
        when (categoryName?.lowercase()) {
            "mascotas", "mascota" -> Icons.Default.Pets
            "hogar", "casa", "limpieza" -> Icons.Default.Home
            "tecnología", "tecnologia" -> Icons.Default.Devices
            "salud", "salud y bienestar" -> Icons.Default.MedicalServices
            "transporte", "trasporte" -> Icons.Default.LocalShipping
            else -> Icons.Default.Work
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono de la categoría (Círculo de color suave)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = categoryIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = service.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Precio destacado en la esquina
                Text(
                    text = currencyFormatter.format(service.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Días de atención (Bolitas mini)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    dayNames.forEachIndexed { index, name ->
                        val dayNumber = index + 1
                        val isSelected = service.working_days.contains(dayNumber)
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Horario de atención
                val timeRange = if (service.start_time.isNotEmpty()) {
                    "${service.start_time.substring(0, 5)} - ${service.end_time.substring(0, 5)}"
                } else ""

                if (timeRange.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = timeRange,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onServiceClick = {}, 
        onProfileClick = {}, 
        onMyOrders = {},
        onIncomingRequestsClick = {},
        onChatListClick = {},
        onCreateServiceClick = {}, 
        onLogout = {}
    )
}
