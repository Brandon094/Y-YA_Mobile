package com.bhplusplus.yaya.ui.screens.service_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import androidx.compose.ui.draw.clip
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.models.UserProfile
import com.bhplusplus.yaya.data.models.ServiceImage
import com.bhplusplus.yaya.data.models.Rating
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * PANTALLA DE DETALLE DEL SERVICIO
 * Maneja la lógica de carga de datos desde Supabase.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceId: String, 
    onBack: () -> Unit,
    onContratar: () -> Unit,
    onChatClick: (String, String) -> Unit, // receiverId, receiverName
    viewModel: ServiceDetailViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showReportDialog by remember { mutableStateOf(false) }

    // DISPARAMOS LA CARGA DESDE SUPABASE AL INICIAR
    LaunchedEffect(serviceId) {
        viewModel.fetchServiceById(serviceId)
    }

    if (showReportDialog) {
        ReportDialog(
            onDismiss = { showReportDialog = false },
            onConfirm = { reason ->
                viewModel.submitReport(reason) { success ->
                    showReportDialog = false
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (success) "Reporte enviado al administrador." else "Error al enviar reporte."
                        )
                    }
                }
            },
            isReporting = viewModel.isReporting
        )
    }

    val service = viewModel.service
    val provider = viewModel.provider

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (viewModel.errorMessage != null) {
                Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(16.dp))
                        Text(text = viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.fetchServiceById(serviceId) }) {
                            Text("Reintentar")
                        }
                    }
                }
            } else if (service != null) {
                ServiceDetailContent(
                    service = service,
                    provider = provider,
                    serviceImages = viewModel.serviceImages,
                    ratings = viewModel.ratings,
                    averageRating = viewModel.averageRating,
                    onBack = onBack,
                    onContratar = onContratar,
                    onReportClick = { showReportDialog = true },
                    onChatClick = {
                        provider?.let { onChatClick(it.id, it.full_name) }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    isReporting: Boolean
) {
    val reasons = listOf("Servicio falso", "Acoso o lenguaje ofensivo", "Fraude o estafa", "Spam", "Suplantación")
    var selectedReason by remember { mutableStateOf(reasons[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reportar Prestador", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Selecciona el motivo de la denuncia. El administrador revisará este caso.", fontSize = 14.sp)
                Spacer(Modifier.height(16.dp))
                reasons.forEach { reason ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable { selectedReason = reason }.padding(vertical = 4.dp)
                    ) {
                        RadioButton(selected = (reason == selectedReason), onClick = { selectedReason = reason })
                        Text(text = reason, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedReason) },
                enabled = !isReporting,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                if (isReporting) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                else Text("Enviar Reporte", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isReporting) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * COMPONENTE VISUAL DE DETALLE DEL SERVICIO (Sin lógica de ViewModel para permitir Previews)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailContent(
    service: Service,
    provider: UserProfile?,
    serviceImages: List<ServiceImage> = emptyList(),
    ratings: List<Rating> = emptyList(),
    averageRating: Double = 0.0,
    onBack: () -> Unit,
    onContratar: () -> Unit,
    onReportClick: () -> Unit,
    onChatClick: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }
    val ratingText = remember(averageRating) {
        String.format(Locale.getDefault(), "%.1f", averageRating)
    }

    // VISOR DE IMAGEN EN PANTALLA COMPLETA CON NAVEGACIÓN (SWIPE)
    if (selectedImageIndex != null && serviceImages.isNotEmpty()) {
        val pagerState = rememberPagerState(initialPage = selectedImageIndex!!) { serviceImages.size }
        
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { selectedImageIndex = null },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    pageSpacing = 16.dp
                ) { page ->
                    AsyncImage(
                        model = serviceImages[page].image_url,
                        contentDescription = "Imagen ampliada ${page + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // Botón Cerrar
                IconButton(
                    onClick = { selectedImageIndex = null },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                }

                // Indicador de página (ej: 1/5)
                Text(
                    text = "${pagerState.currentPage + 1} / ${serviceImages.size}",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.service_detail_title), fontWeight = FontWeight.Bold) },
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
                    IconButton(onClick = onReportClick) {
                        Icon(
                            imageVector = Icons.Default.OutlinedFlag, 
                            contentDescription = "Reportar",
                            tint = Color.Red.copy(alpha = 0.7f)
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(modifier = Modifier.navigationBarsPadding()) {
                    Button(
                        onClick = onContratar,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = stringResource(R.string.service_detail_order_button), 
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Visual (Galería de imágenes o Placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (serviceImages.isNotEmpty()) {
                    // Carrusel simple si hay imágenes
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(0.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        items(serviceImages.size) { index ->
                            AsyncImage(
                                model = serviceImages[index].image_url,
                                contentDescription = "Imagen del servicio $index",
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .fillMaxHeight()
                                    .clickable { selectedImageIndex = index },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                } else {
                    // Placeholder si no hay fotos
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Sin fotos del portafolio", 
                            fontSize = 12.sp, 
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                // TÍTULO Y PRECIO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = service.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = currencyFormatter.format(service.price),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // INDICADOR DE VERIFICACIÓN
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.service_detail_verified),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // INFO DEL PRESTADOR (Real desde public.profiles)
                Text(
                    text = stringResource(R.string.service_detail_provider_label),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar del prestador dinámico
                        Box(
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (provider?.avatar_url != null) {
                                AsyncImage(
                                    model = provider.avatar_url,
                                    contentDescription = "Avatar de ${provider.full_name}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person, 
                                    contentDescription = null, 
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = provider?.full_name ?: "Cargando prestador...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB800), modifier = Modifier.size(14.dp))
                                Text(
                                    text = " $ratingText (${ratings.size} reseñas)",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        
                        Spacer(Modifier.weight(1f))
                        
                        // Botón de Chat (Hito 2)
                        IconButton(
                            onClick = onChatClick,
                            enabled = provider != null,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = "Chatear")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // DESCRIPCIÓN
                Text(
                    text = stringResource(R.string.service_detail_description_label),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = service.description,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // TIEMPO ESTIMADO
                if (!service.estimated_time.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${stringResource(R.string.service_detail_time_label)}: ${service.estimated_time}",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // DISPONIBILIDAD
                if (service.working_days.isNotEmpty()) {
                    val dayNames = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
                    val formattedDays = service.working_days.mapNotNull { if(it in 1..7) dayNames[it-1] else null }.joinToString(", ")
                    val timeRange = "${service.start_time.substring(0, 5)} - ${service.end_time.substring(0, 5)}"
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EventAvailable, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Días de atención: $formattedDays",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Horario: $timeRange",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // SECCIÓN DE RESEÑAS
                Text(
                    text = "Reseñas de clientes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (ratings.isEmpty()) {
                    Text(
                        "Este prestador aún no tiene calificaciones.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    ratings.take(5).forEach { rating ->
                        RatingItem(rating)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // CONDICIONES Y MATERIALES
                Text(
                    text = stringResource(R.string.service_detail_whats_included_label),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Check de Materiales
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (service.materials_included) Color(0xFF4CAF50) else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (service.materials_included) 
                            stringResource(R.string.service_detail_materials_yes) 
                        else stringResource(R.string.service_detail_materials_no),
                        fontSize = 14.sp
                    )
                }
                
                if (!service.materials_included && service.extra_cost > 0) {
                    Text(
                        text = "  + ${currencyFormatter.format(service.extra_cost)} ${stringResource(R.string.service_detail_extra_cost_label)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 28.dp)
                    )
                }

                // Garantía estándar
                Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(text = stringResource(R.string.service_detail_guarantee), fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun RatingItem(rating: Rating) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Estrellas
                Row {
                    for (i in 1..5) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (i <= rating.score) Color(0xFFFFB800) else Color.Gray.copy(alpha = 0.3f)
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = rating.created_at?.take(10) ?: "",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            if (!rating.comment.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = rating.comment,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceDetailPreview() {
    // Usamos datos de prueba para la previsualización
    ServiceDetailContent(
        service = Service(
            title = "Limpieza de Apartamento",
            description = "Servicio profesional de limpieza profunda para apartamentos y casas. Incluye aspirado, trapeado y desinfección.",
            price = 85000.0,
            estimated_time = "4 horas",
            materials_included = true,
        ),
        provider = UserProfile(
            id = "1",
            full_name = "Carlos Mario Pérez",
            phone = "3001234567",
            document_id = "12345",
            birth_date = "1990-01-01",
            address = "Calle 123",
            role = "provider"
        ),
        onBack = {},
        onContratar = {},
        onReportClick = {},
        onChatClick = {}
    )
}
