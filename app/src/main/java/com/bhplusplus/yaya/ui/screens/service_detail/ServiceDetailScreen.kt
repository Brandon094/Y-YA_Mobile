package com.bhplusplus.yaya.ui.screens.service_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.bhplusplus.yaya.ui.components.ServiceDetailShimmer
import com.bhplusplus.yaya.ui.components.atoms.YayaPrimaryButton
import com.bhplusplus.yaya.ui.components.atoms.YayaSectionHeader
import com.bhplusplus.yaya.ui.components.molecules.DayIndicator
import com.bhplusplus.yaya.ui.components.molecules.StatusBadgeDetail
import com.bhplusplus.yaya.ui.components.molecules.YayaRatingItem
import com.bhplusplus.yaya.ui.components.molecules.YayaReportDialog
import com.bhplusplus.yaya.ui.components.organisms.ProviderCard
import com.bhplusplus.yaya.ui.components.organisms.ServiceDetailGallery
import com.bhplusplus.yaya.utils.FormatterUtils

/**
 * PANTALLA DE DETALLE DEL SERVICIO (Atomic Design Refactor)
 * Orquestada mediante componentes atómicos reutilizables.
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
        YayaReportDialog(
            onDismiss = { showReportDialog = false },
            onConfirm = { reason ->
                viewModel.submitReport(reason) { success ->
                    showReportDialog = false
                }
            },
            isReporting = viewModel.isReporting
        )
    }

    val service = viewModel.service
    val provider = viewModel.provider

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Talento", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.onSecondary)
                    }
                },
                actions = {
                    IconButton(onClick = { showReportDialog = true }) {
                        Icon(Icons.Default.OutlinedFlag, null, tint = Color.Red.copy(alpha = 0.7f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Box(modifier = Modifier.navigationBarsPadding()) {
                    YayaPrimaryButton(
                        text = "SOLICITAR ESTE SERVICIO",
                        onClick = onContratar,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (viewModel.isLoading) {
                ServiceDetailShimmer()
            } else if (service != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Organismo: Galería
                    ServiceDetailGallery(
                        images = viewModel.serviceImages,
                        onImageClick = { /* No-op or Expand */ }
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-24).dp),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            // Título y Precio
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                                Text(text = service.title, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f), lineHeight = 30.sp)
                                Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(12.dp)) {
                                    Text(text = FormatterUtils.formatCurrency(service.price), modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            
                            // Moléculas: Badges de estado
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                StatusBadgeDetail("Verificado", Icons.Default.Verified, MaterialTheme.colorScheme.primary)
                                if (service.materials_included) {
                                    StatusBadgeDetail("Materiales Incluidos", Icons.Default.Inventory2, Color(0xFF4CAF50))
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // Organismo: Card del Prestador
                            YayaSectionHeader("PRESTADOR DEL TALENTO")
                            ProviderCard(
                                provider = provider,
                                averageRating = viewModel.averageRating,
                                totalRatings = viewModel.ratings.size,
                                onChatClick = {
                                    provider?.let { onChatClick(it.id, it.full_name) }
                                }
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Sección: Descripción
                            YayaSectionHeader("ACERCA DEL SERVICIO")
                            Text(text = service.description, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), lineHeight = 24.sp)

                            Spacer(modifier = Modifier.height(32.dp))

                            // Sección: Disponibilidad
                            if (service.working_days.isNotEmpty()) {
                                YayaSectionHeader("DISPONIBILIDAD DE ATENCIÓN")
                                DayIndicator(workingDays = service.working_days)
                                Spacer(Modifier.height(16.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(text = "${FormatterUtils.formatTime(service.start_time)} - ${FormatterUtils.formatTime(service.end_time)}", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(modifier = Modifier.height(32.dp))
                            }

                            // Sección: Reseñas
                            YayaSectionHeader("RESEÑAS RECIENTES")
                            if (viewModel.ratings.isEmpty()) {
                                Text("Aún no hay reseñas para este prestador.", fontSize = 14.sp, color = Color.Gray)
                            } else {
                                viewModel.ratings.take(3).forEach { rating -> 
                                    YayaRatingItem(rating = rating)
                                    Spacer(Modifier.height(12.dp)) 
                                }
                            }

                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceDetailPreview() {
    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Color.White)) {
            Text("Vista Previa de Detalle Atómico")
        }
    }
}
