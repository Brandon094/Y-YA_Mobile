package com.bhplusplus.yaya.ui.screens.service_detail

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.data.models.Rating
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.models.ServiceImage
import com.bhplusplus.yaya.data.models.UserProfile
import com.bhplusplus.yaya.utils.FormatterUtils
import java.util.Locale

/**
 * PANTALLA DE DETALLE DEL SERVICIO (Rediseño Premium UX)
 * Optimizado para alto impacto visual y accesibilidad.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceId: String, 
    onBack: () -> Unit,
    onContratar: () -> Unit,
    onChatClick: (String, String) -> Unit,
    viewModel: ServiceDetailViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showReportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(serviceId) {
        viewModel.fetchServiceById(serviceId)
    }

    if (showReportDialog) {
        ReportDialog(
            onDismiss = { showReportDialog = false },
            onConfirm = { reason ->
                viewModel.submitReport(reason) { success ->
                    showReportDialog = false
                    // Realizar feedback
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
    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }
    val ratingText = remember(averageRating) {
        String.format(Locale.getDefault(), "%.1f", averageRating)
    }

    // VISOR DE IMAGEN EN PANTALLA COMPLETA
    if (selectedImageIndex != null && serviceImages.isNotEmpty()) {
        val pagerState = rememberPagerState(initialPage = selectedImageIndex!!) { serviceImages.size }
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { selectedImageIndex = null },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize(), pageSpacing = 16.dp) { page ->
                    AsyncImage(model = serviceImages[page].image_url, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                }
                IconButton(onClick = { selectedImageIndex = null }, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Talento", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.onSecondary)
                    }
                },
                actions = {
                    IconButton(onClick = onReportClick) {
                        Icon(Icons.Default.OutlinedFlag, null, tint = Color.Red.copy(alpha = 0.7f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Box(modifier = Modifier.navigationBarsPadding()) {
                    Button(
                        onClick = onContratar,
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = "SOLICITAR ESTE SERVICIO", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState())
        ) {
            // Galería Immersiva
            Box(
                modifier = Modifier.fillMaxWidth().height(300.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (serviceImages.isNotEmpty()) {
                    androidx.compose.foundation.lazy.LazyRow(modifier = Modifier.fillMaxSize()) {
                        items(serviceImages.size) { index ->
                            AsyncImage(
                                model = serviceImages[index].image_url,
                                contentDescription = null,
                                modifier = Modifier.fillParentMaxWidth().fillMaxHeight().clickable { selectedImageIndex = index },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)), startY = 600f)))
                    Surface(modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp), color = Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(20.dp)) {
                        Text(text = "1 / ${serviceImages.size}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                } else {
                    Icon(Icons.Default.Image, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth().offset(y = (-24).dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Text(text = service.title, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f), lineHeight = 30.sp)
                        Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(12.dp)) {
                            Text(text = FormatterUtils.formatCurrency(service.price), modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusBadgeDetail("Verificado", Icons.Default.Verified, MaterialTheme.colorScheme.primary)
                        if (service.materials_included) StatusBadgeDetail("Materiales Incluidos", Icons.Default.Inventory2, Color(0xFF4CAF50))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    ProfileSectionHeader("PRESTADOR DEL TALENTO")
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(54.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                                if (provider?.avatar_url != null) AsyncImage(model = provider.avatar_url, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                else Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = provider?.full_name ?: "Cargando...", fontWeight = FontWeight.Bold, fontSize = 17.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(14.dp))
                                    Text(text = " $ratingText (${ratings.size} reseñas)", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            IconButton(onClick = onChatClick, modifier = Modifier.size(44.dp).background(MaterialTheme.colorScheme.primary, CircleShape)) {
                                Icon(Icons.Default.Chat, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    ProfileSectionHeader("ACERCA DEL SERVICIO")
                    Text(text = service.description, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), lineHeight = 24.sp)

                    Spacer(modifier = Modifier.height(32.dp))

                    if (service.working_days.isNotEmpty()) {
                        ProfileSectionHeader("DISPONIBILIDAD DE ATENCIÓN")
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val dayNames = listOf("L", "M", "M", "J", "V", "S", "D")
                            dayNames.forEachIndexed { index, name ->
                                val isSelected = service.working_days.contains(index + 1)
                                Box(modifier = Modifier.sizeIn(minWidth = 36.dp, minHeight = 36.dp).background(color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape), contentAlignment = Alignment.Center) {
                                    Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = if (isSelected) Color.White else Color.Gray)
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(text = "${FormatterUtils.formatTime(service.start_time)} - ${FormatterUtils.formatTime(service.end_time)}", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    ProfileSectionHeader("RESEÑAS RECIENTES")
                    if (ratings.isEmpty()) Text("Aún no hay reseñas para este prestador.", fontSize = 14.sp, color = Color.Gray)
                    else ratings.take(3).forEach { rating -> RatingItem(rating); Spacer(Modifier.height(12.dp)) }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun StatusBadgeDetail(text: String, icon: ImageVector, color: Color) {
    Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp), border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text(text = text, color = color, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun ProfileSectionHeader(title: String) {
    Text(text = title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 12.dp), letterSpacing = 1.sp)
}

@Composable
fun RatingItem(rating: Rating) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.wrapContentWidth()) {
                    for (i in 1..5) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (i <= rating.score) Color(0xFFFFB800) else Color.Gray.copy(alpha = 0.3f))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(text = rating.created_at?.take(10) ?: "", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            }
            if (!rating.comment.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(text = rating.comment, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit, isReporting: Boolean) {
    val reasons = listOf("Servicio falso", "Acoso", "Fraude", "Spam", "Suplantación")
    var selectedReason by remember { mutableStateOf(reasons[0]) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Reportar Prestador", fontWeight = FontWeight.Bold) }, text = {
        Column {
            Text("Selecciona el motivo de la denuncia:", fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
            reasons.forEach { reason ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { selectedReason = reason }.padding(vertical = 4.dp)) {
                    RadioButton(selected = (reason == selectedReason), onClick = { selectedReason = reason })
                    Text(text = reason, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }, confirmButton = {
        Button(onClick = { onConfirm(selectedReason) }, enabled = !isReporting, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
            if (isReporting) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            else Text("Enviar Reporte", color = Color.White)
        }
    }, dismissButton = { TextButton(onClick = onDismiss, enabled = !isReporting) { Text("Cancelar") } })
}

@Preview(showBackground = true)
@Composable
fun ServiceDetailPreview() {
    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Color.White)) {
            Text("Vista Previa de Detalle")
        }
    }
}
