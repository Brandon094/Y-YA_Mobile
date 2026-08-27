package com.bhplusplus.yaya.ui.screens.contratacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import com.bhplusplus.yaya.R
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar

/**
 * PANTALLA DE CONTRATACIÓN
 * Arquitectura MVVM: La View solo renderiza el estado procesado del ViewModel.
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
    val datePickerState = rememberDatePickerState()

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
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (uiState != null) {
        ContratacionContent(
            uiState = uiState,
            direccion = viewModel.direccion,
            onDireccionChange = { viewModel.direccion = it },
            fecha = viewModel.fecha,
            onFechaClick = { showDatePicker = true },
            hora = viewModel.hora,
            onHoraClick = { showTimePicker = true },
            ofertaRaw = viewModel.oferta,
            onOfertaChange = { viewModel.updateOferta(it) },
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

/**
 * COMPONENTE VISUAL DE CONTRATACIÓN
 * Optimizado para accesibilidad (fuentes al 200%) y navegación.
 */
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
    ofertaRaw: String,
    onOfertaChange: (String) -> Unit,
    onIncrementar: () -> Unit,
    onDecrementar: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onContratar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.contratacion_request_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SECCIÓN 1: RESUMEN DEL SERVICIO
            Text(
                text = stringResource(R.string.contratacion_details_section).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow(Icons.Default.Build, stringResource(R.string.contratacion_service_field), uiState.serviceTitle)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    InfoRow(Icons.Default.Payments, stringResource(R.string.contratacion_base_price_field), uiState.formattedBasePrice)
                }
            }

            // SECCIÓN 2: DATOS DE RESERVA
            Text(text = "TUS DATOS DE RESERVA", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CampoFormulario(stringResource(R.string.contratacion_address_field), direccion, !isLoading, stringResource(R.string.contratacion_address_placeholder), onDireccionChange)
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SelectorBoton(
                            icon = Icons.Default.DateRange,
                            label = if (fecha.isEmpty()) stringResource(R.string.contratacion_date_field) else fecha,
                            modifier = Modifier.weight(1f),
                            onClick = onFechaClick,
                            enabled = !isLoading
                        )
                        SelectorBoton(
                            icon = Icons.Default.AccessTime,
                            label = if (hora.isEmpty()) stringResource(R.string.contratacion_time_field) else hora,
                            modifier = Modifier.weight(1f),
                            onClick = onHoraClick,
                            enabled = !isLoading
                        )
                    }
                }
            }

            // SECCIÓN 3: NEGOCIACIÓN (AJUSTE PARA FUENTES GRANDES)
            Text(text = stringResource(R.string.contratacion_offer_field).uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Ajusta tu oferta (Mínimo: ${uiState.formattedBasePrice})", style = MaterialTheme.typography.bodySmall, color = Color.Gray, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        FilledIconButton(
                            onClick = onDecrementar,
                            enabled = !isLoading && !uiState.isOfferAtBase,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) { Icon(Icons.Default.Remove, contentDescription = "Menos") }

                        OutlinedTextField(
                            value = ofertaRaw,
                            onValueChange = onOfertaChange,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp), // WEIGHT PARA NO DESBORDAR
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 18.sp),
                            prefix = { Text("$") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        )

                        FilledIconButton(
                            onClick = onIncrementar,
                            enabled = !isLoading,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) { Icon(Icons.Default.Add, contentDescription = "Más") }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(text = "Propuesta: ${uiState.formattedOffer}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
                }
            }

            if (errorMessage != null) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth()) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 13.sp, modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center, lineHeight = 18.sp)
                }
            }

            Spacer(Modifier.weight(1f))

            Box(modifier = Modifier.navigationBarsPadding()) {
                Button(
                    onClick = onContratar,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp), // heightIn para fuentes grandes
                    shape = RoundedCornerShape(16.dp),
                    enabled = uiState.canContratar
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    else Text(stringResource(R.string.contratacion_button), fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, null, modifier = Modifier.size(20.dp).padding(top = 2.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, lineHeight = 20.sp)
        }
    }
}

@Composable
fun SelectorBoton(icon: ImageVector, label: String, modifier: Modifier, onClick: () -> Unit, enabled: Boolean) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 56.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun CampoFormulario(label: String, value: String, enabled: Boolean, placeholder: String = "", onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label) }, placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        singleLine = true, enabled = enabled, shape = RoundedCornerShape(12.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun ContratacionPreview() {
    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Color.White)) {
            Text("Vista Previa de Contratación")
        }
    }
}
