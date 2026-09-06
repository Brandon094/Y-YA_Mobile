package com.bhplusplus.yaya.ui.screens.contratacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.ui.components.ContratacionShimmer
import com.bhplusplus.yaya.ui.components.atoms.YayaPrimaryButton
import com.bhplusplus.yaya.ui.components.atoms.YayaSectionHeader
import com.bhplusplus.yaya.ui.components.atoms.YayaTextField
import com.bhplusplus.yaya.ui.components.molecules.PriceNegotiator
import com.bhplusplus.yaya.ui.components.molecules.YayaSelectorButton
import com.bhplusplus.yaya.ui.components.organisms.ProfileSectionCard
import com.bhplusplus.yaya.ui.components.organisms.ServiceRequestHero
import com.bhplusplus.yaya.ui.components.organisms.TutorialStep
import com.bhplusplus.yaya.ui.components.organisms.YayaTutorialOverlay
import com.bhplusplus.yaya.utils.TutorialManager
import com.bhplusplus.yaya.utils.ValidationUtils
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar

/**
 * PANTALLA DE CONTRATACIÓN (Atomic Design Refactor)
 * Orquestada mediante componentes atómicos reutilizables.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaContratacion(
    serviceId: String,
    onBack: () -> Unit,
    onContratarClick: (String) -> Unit,
    viewModel: ContratacionViewModel = viewModel()
) {
    LaunchedEffect(serviceId) {
        viewModel.loadData(serviceId)
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val todayStartUtc = java.time.LocalDate.now()
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant()
                    .toEpochMilli()
                return utcTimeMillis >= todayStartUtc
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                        viewModel.fecha = date.toString()
                        viewModel.checkAvailability()
                    }
                    showDatePicker = false
                }) { Text("Confirmar") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        initialMinute = 0,
        is24Hour = true
    )

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val hour = if (timePickerState.hour < 10) "0${timePickerState.hour}" else "${timePickerState.hour}"
                    val minute = if (timePickerState.minute < 10) "0${timePickerState.minute}" else "${timePickerState.minute}"
                    viewModel.hora = "$hour:$minute"
                    viewModel.checkAvailability()
                    showTimePicker = false
                }) { Text("Confirmar") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") } },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Selecciona la hora", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(16.dp))
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    val uiState = viewModel.uiState

    if (viewModel.isLoading && uiState == null) {
        ContratacionShimmer()
    } else if (uiState != null) {
        ContratacionContent(
            uiState = uiState,
            direccion = viewModel.direccion,
            onDireccionChange = { viewModel.direccion = it },
            fecha = viewModel.fecha,
            onFechaClick = { showDatePicker = true },
            hora = viewModel.hora,
            onHoraClick = { showTimePicker = true },
            onIncrementar = { viewModel.incrementarOferta() },
            onDecrementar = { viewModel.decrementarOferta() },
            isLoading = viewModel.isLoading,
            errorMessage = viewModel.errorMessage,
            onBack = onBack,
            onContratar = {
                viewModel.contratar { success, requestId ->
                    if (success && requestId != null) onContratarClick(requestId)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContratacionContent(
    uiState: ContratacionUiState,
    direccion: String,
    onDireccionChange: (String) -> Unit,
    fecha: String,
    onFechaClick: () -> Unit,
    hora: String,
    onHoraClick: () -> Unit,
    onIncrementar: () -> Unit,
    onDecrementar: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onContratar: () -> Unit
) {
    var addressBounds by remember { mutableStateOf<Rect?>(null) }
    var dateTimeBounds by remember { mutableStateOf<Rect?>(null) }
    var negotiatorBounds by remember { mutableStateOf<Rect?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Programar Solicitud", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.onSecondary)
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
                    YayaPrimaryButton(
                        text = "CONFIRMAR SOLICITUD",
                        onClick = onContratar,
                        modifier = Modifier.padding(16.dp),
                        enabled = uiState.canContratar,
                        isLoading = isLoading
                    )
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
            // Organismo: Hero Header de Solicitud
            ServiceRequestHero(
                title = uiState.serviceTitle,
                providerName = uiState.providerName,
                avatarUrl = uiState.providerAvatarUrl
            )

            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                
                // Sección: Detalles de la Cita
                Column {
                    YayaSectionHeader("DETALLES DE LA CITA")
                    ProfileSectionCard {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Átomo: Campo de dirección
                            YayaTextField(
                                value = direccion,
                                onValueChange = onDireccionChange,
                                label = "Dirección de atención",
                                errorMessage = if (direccion.isNotEmpty() && !ValidationUtils.isValidAddress(direccion)) "Ingresa una dirección válida (mínimo 5 caracteres)" else null,
                                leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary) },
                                enabled = !isLoading,
                                modifier = Modifier.onGloballyPositioned { coords ->
                                    addressBounds = coords.boundsInWindow()
                                }
                            )
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.onGloballyPositioned { coords ->
                                    dateTimeBounds = coords.boundsInWindow()
                                }
                            ) {
                                YayaSelectorButton(
                                    label = if (fecha.isEmpty()) "Fecha" else fecha,
                                    icon = Icons.Default.CalendarMonth,
                                    modifier = Modifier.weight(1f),
                                    onClick = onFechaClick,
                                    enabled = !isLoading
                                )
                                YayaSelectorButton(
                                    label = if (hora.isEmpty()) "Hora" else hora,
                                    icon = Icons.Default.Schedule,
                                    modifier = Modifier.weight(1f),
                                    onClick = onHoraClick,
                                    enabled = !isLoading
                                )
                            }
                        }
                    }
                }

                // Sección: Negociación (Molécula)
                Column(
                    modifier = Modifier.onGloballyPositioned { coords ->
                        negotiatorBounds = coords.boundsInWindow()
                    }
                ) {
                    YayaSectionHeader("NEGOCIACIÓN DE PRECIO")
                    PriceNegotiator(
                        currentOffer = uiState.formattedOffer,
                        basePrice = uiState.formattedBasePrice,
                        onIncrement = onIncrementar,
                        onDecrement = onDecrementar,
                        isDecrementEnabled = !uiState.isOfferAtBase,
                        isLoading = isLoading
                    )
                }

                if (errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(text = errorMessage, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                
                Spacer(Modifier.height(80.dp))
            }
        }
    }

    // ORGANISMO ATÓMICO: Tutorial In-App (ShowOnce + Spotlight Cutout)
    YayaTutorialOverlay(
        tutorialKey = TutorialManager.TUTORIAL_CONTRATACION_HANDSHAKE,
        steps = listOf(
            TutorialStep(
                title = "Dirección de Atención",
                description = "Ingresa la dirección exacta de residencia o lugar donde el prestador deberá acudir para realizar el trabajo.",
                targetBounds = addressBounds,
                targetCornerRadius = 16.dp,
                targetPadding = 2.dp
            ),
            TutorialStep(
                title = "Agendamiento de Cita",
                description = "Selecciona la fecha y la hora programada. El sistema bloquea automáticamente días no trabajados, fechas pasadas u horas transcurridas hoy.",
                targetBounds = dateTimeBounds,
                targetCornerRadius = 16.dp,
                targetPadding = 4.dp
            ),
            TutorialStep(
                title = "Cierre de Trato Handshake",
                description = "Propón tu oferta económica inicial usando los botones + y -. Al confirmar, se enviará la solicitud al prestador para su aceptación o contraoferta.",
                targetBounds = negotiatorBounds,
                targetCornerRadius = 20.dp,
                targetPadding = 4.dp
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ContratacionPreview() {
    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Color.White)) {
            Text("Vista Previa de Contratación Atómica")
        }
    }
}
