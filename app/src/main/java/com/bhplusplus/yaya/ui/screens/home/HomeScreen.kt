package com.bhplusplus.yaya.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.ui.components.ServiceItemShimmer
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
    val pullToRefreshState = rememberPullToRefreshState()
    val userName = remember(viewModel.userProfile?.full_name) {
        viewModel.userProfile?.full_name?.substringBefore(" ") ?: ""
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = stringResource(R.string.app_brand_yaya),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (userName.isNotEmpty()) {
                            Text(
                                text = stringResource(R.string.home_welcome_back, userName),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
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
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
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
                        .navigationBarsPadding() // RESPETA BOTONES DE NAVEGACIÓN
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
                // 1. BARRA DE BÚSQUEDA INTEGRADA
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                ) {
                    OutlinedTextField(
                        value = viewModel.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp, top = 8.dp),
                        placeholder = { 
                            Text(
                                stringResource(R.string.home_search_placeholder),
                                fontSize = 14.sp
                            ) 
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = {
                            if (viewModel.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                // 2. SELECTOR DE CATEGORÍAS
                Text(
                    text = "¿Qué buscas hoy?",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(Modifier.height(12.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        FilterChip(
                            selected = viewModel.selectedCategoryId == null,
                            onClick = { viewModel.onCategorySelect(null) },
                            label = { Text("Ver todo") },
                            leadingIcon = if (viewModel.selectedCategoryId == null) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    items(viewModel.categories) { category ->
                        FilterChip(
                            selected = viewModel.selectedCategoryId == category.id,
                            onClick = { viewModel.onCategorySelect(category.id) },
                            label = { Text(category.name) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (viewModel.isLoading && viewModel.filteredServices.isEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(5) {
                            ServiceItemShimmer()
                        }
                    }
                } else if (viewModel.filteredServices.isEmpty()) {
                    EmptyServicesView(
                        searchQuery = viewModel.searchQuery,
                        onClearSearch = { viewModel.onSearchQueryChange("") }
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(viewModel.filteredServices) { uiState ->
                            ServiceItem(
                                state = uiState, 
                                onClick = { onServiceClick(uiState.domain) }
                            )
                        }
                    }
                }
            }
        }
    }
}

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

@Composable
fun ServiceItem(state: ServiceUiState, onClick: () -> Unit) {
    val dayNames = listOf("L", "M", "M", "J", "V", "S", "D")
    val ratingText = remember(state.averageRating) {
        String.format(Locale.getDefault(), "%.1f", state.averageRating)
    }

    val categoryIcon = remember(state.categoryName) {
        when (state.categoryName?.lowercase()) {
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp), // Más redondeado para look moderno
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // HEADER: Avatar + Nombre + Badge Categoría
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Foto del Prestador e Icono de Categoría combinados
                Box(modifier = Modifier.size(48.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.providerAvatarUrl != null) {
                            AsyncImage(
                                model = state.providerAvatarUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    // Icono de categoría pequeño superpuesto
                    Surface(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 2.dp
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.domain.provider?.full_name ?: "Prestador YÁYA",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Badge de Categoría mini
                    Surface(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = (state.categoryName ?: "General").uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                // Precio en un Pill destacado
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = state.formattedPrice,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CUERPO: Título y Descripción
            Text(
                text = state.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = state.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // DIVIDER SUTIL
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(12.dp))

            // FOOTER: Calificación + Disponibilidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Rating con estilo compacto
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (state.totalRatings > 0) Color(0xFFFFB800) else Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (state.totalRatings > 0) "$ratingText (${state.totalRatings})" else "Sin reseñas",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.totalRatings > 0) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )
                }

                // Días (Optimizado para 200% Font Scale)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    dayNames.forEachIndexed { index, name ->
                        val dayNumber = index + 1
                        val isSelected = state.workingDays.contains(dayNumber)
                        Box(
                            modifier = Modifier
                                .sizeIn(minWidth = 24.dp, minHeight = 24.dp) // Dinámico
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) 
                                            else Color.Transparent,
                                    shape = CircleShape
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary 
                                            else MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                                .padding(4.dp), // Espacio interno para que el texto no toque bordes
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) MaterialTheme.colorScheme.primary 
                                        else MaterialTheme.colorScheme.outlineVariant,
                                maxLines = 1 // Evita saltos de línea dentro del círculo
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Color.White)) {
            Text("Vista Previa del Catálogo")
        }
    }
}
