package com.bhplusplus.yaya.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.ui.components.AvailabilityItemShimmer
import com.bhplusplus.yaya.ui.components.atoms.YayaPrimaryButton
import com.bhplusplus.yaya.ui.components.organisms.AvailabilityDayCard
import com.bhplusplus.yaya.ui.components.organisms.TutorialStep
import com.bhplusplus.yaya.ui.components.organisms.YayaTutorialOverlay
import com.bhplusplus.yaya.utils.TutorialManager

/**
 * PANTALLA DE GESTIÓN DE HORARIO GLOBAL (Atomic Design Refactor)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailabilityScreen(
    onBack: () -> Unit,
    viewModel: AvailabilityViewModel = viewModel()
) {
    val pullToRefreshState = androidx.compose.material3.pulltorefresh.rememberPullToRefreshState()
    var summaryBannerBounds by remember { mutableStateOf<Rect?>(null) }
    var presetsBarBounds by remember { mutableStateOf<Rect?>(null) }
    var daysListBounds by remember { mutableStateOf<Rect?>(null) }
    var saveButtonBounds by remember { mutableStateOf<Rect?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Horario General", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(modifier = Modifier.navigationBarsPadding()) {
                    YayaPrimaryButton(
                        text = "Guardar Cambios",
                        onClick = {
                            viewModel.saveAvailability {
                                onBack()
                            }
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .onGloballyPositioned { coords ->
                                saveButtonBounds = coords.boundsInWindow()
                            },
                        isLoading = viewModel.isSaving
                    )
                }
            }
        }
    ) { padding ->
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = viewModel.isLoading,
            onRefresh = { viewModel.loadAvailability() },
            state = pullToRefreshState,
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (viewModel.isLoading && viewModel.daysState.all { !it.isWorking }) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(7) {
                            AvailabilityItemShimmer()
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { coords ->
                                daysListBounds = coords.boundsInWindow()
                            },
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onGloballyPositioned { coords ->
                                        summaryBannerBounds = coords.boundsInWindow()
                                    },
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "📊 MI JORNADA MAESTRA",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary,
                                            letterSpacing = 1.sp
                                        )
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "${viewModel.activeDaysCount} Días (${viewModel.totalWeeklyHours}h/sem)",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(10.dp))

                                    Text(
                                        text = "Configuración Rápida en 1 Clic:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onGloballyPositioned { coords ->
                                                presetsBarBounds = coords.boundsInWindow()
                                            },
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        FilterChip(
                                            selected = viewModel.activeDaysCount == 5 && viewModel.daysState.take(5).all { it.isWorking },
                                            onClick = { viewModel.applyPresetMonToFri() },
                                            label = { Text("💼 L a V", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                                        )
                                        FilterChip(
                                            selected = viewModel.activeDaysCount == 7,
                                            onClick = { viewModel.applyPresetAllDays() },
                                            label = { Text("📅 Todos", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                                        )
                                        FilterChip(
                                            selected = false,
                                            onClick = { viewModel.clearAllDays() },
                                            label = { Text("🧹 Limpiar", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (!viewModel.message.isNullOrBlank()) {
                            item {
                                Surface(
                                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                ) {
                                    Text(
                                        text = viewModel.message!!,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }

                        // TARJETA COMPACTA DE LOS 7 DÍAS (ZERO SCROLL)
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    viewModel.daysState.forEachIndexed { index, dayState ->
                                        AvailabilityDayCard(
                                            state = dayState,
                                            onToggle = { viewModel.toggleDay(dayState.dayOfWeek) },
                                            onTimeSelected = { start, end -> 
                                                viewModel.updateTimes(dayState.dayOfWeek, start, end) 
                                            }
                                        )
                                        if (index < viewModel.daysState.size - 1) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                thickness = 0.5.dp,
                                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ORGANISMO ATÓMICO: Tutorial In-App (ShowOnce + Spotlight Cutout)
    YayaTutorialOverlay(
        tutorialKey = TutorialManager.TUTORIAL_AVAILABILITY_MASTER,
        steps = listOf(
            TutorialStep(
                title = "📊 Resumen de Jornada Maestra",
                description = "Muestra cuántos días activos tienes configurados y la carga horaria semanal. Te brinda una visión ejecutiva inmediata de tu agenda laboral.",
                targetBounds = summaryBannerBounds,
                targetCornerRadius = 20.dp,
                targetPadding = 2.dp
            ),
            TutorialStep(
                title = "⚡ Atajos de Configuración en 1 Clic",
                description = "Usa los atajos rápidos para activar de Lunes a Viernes, Lunes a Domingo o Limpiar tu agenda en un solo toque, sin tener que encender cada día manualmente.",
                targetBounds = presetsBarBounds,
                targetCornerRadius = 16.dp,
                targetPadding = 4.dp
            ),
            TutorialStep(
                title = "🗓️ Tabla Compacta de Días Hábiles",
                description = "Usa los interruptores para activar los días que trabajas y toca las píldoras de hora para cambiar tu horario de inicio y fin de forma personalizada.",
                targetBounds = daysListBounds,
                targetCornerRadius = 20.dp,
                targetPadding = 4.dp
            ),
            TutorialStep(
                title = "💾 Guardar y Sincronizar Agenda",
                description = "Guarda tus cambios para sincronizar tu disponibilidad general. YÁYA usará estos horarios para validar que tus servicios específicos no se traslapen.",
                targetBounds = saveButtonBounds,
                targetCornerRadius = 16.dp,
                targetPadding = 2.dp
            )
        )
    )
}
