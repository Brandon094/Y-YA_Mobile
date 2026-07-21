package com.bhplusplus.yaya.ui.screens.contratacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.models.UserProfile
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar

/**
 * PANTALLA DE CONTRATACIÓN
 * Ahora permite seleccionar Fecha y Hora de forma profesional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaContratacion(
    serviceId: String,
    onBack: () -> Unit,
    onContratarClick: (String) -> Unit, // Recibe el ID de la solicitud creada
    viewModel: ContratacionViewModel = viewModel()
) {
    // CARGA REAL DESDE SUPABASE
    LaunchedEffect(serviceId) {
        viewModel.loadData(serviceId)
    }

    // Lógica del Selector de Fecha
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        viewModel.fecha = date.toString() // YYYY-MM-DD
                        viewModel.checkAvailability() // Validar disponibilidad (Hito 1)
                    }
                    showDatePicker = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Lógica del Reloj Digital
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
                    viewModel.checkAvailability() // Validar disponibilidad (Hito 1)
                    showTimePicker = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Selecciona la hora", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(16.dp))
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    val service = viewModel.service

    if (viewModel.isLoading && service == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (service != null) {
        ContratacionContent(
            service = service,
            provider = viewModel.providerProfile,
            direccion = viewModel.direccion,
            onDireccionChange = { viewModel.direccion = it },
            fecha = viewModel.fecha,
            onFechaClick = { showDatePicker = true },
            hora = viewModel.hora,
            onHoraClick = { showTimePicker = true },
            oferta = viewModel.oferta,
            onOfertaChange = { viewModel.oferta = it },
            isLoading = viewModel.isLoading,
            errorMessage = viewModel.errorMessage,
            isAvailable = viewModel.isAvailable, // Hito 1
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
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContratacionContent(
    service: Service,
    provider: UserProfile?,
    direccion: String,
    onDireccionChange: (String) -> Unit,
    fecha: String,
    onFechaClick: () -> Unit,
    hora: String,
    onHoraClick: () -> Unit,
    oferta: String,
    onOfertaChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    isAvailable: Boolean, // Hito 1
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Tarjeta Prestador
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(50.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = provider?.full_name ?: stringResource(R.string.contratacion_unknown_provider), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = stringResource(R.string.contratacion_provider_label), fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            // Formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = stringResource(R.string.contratacion_details_section), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(16.dp))
                    
                    CampoFormulario(stringResource(R.string.contratacion_service_field), service.title, false) {}
                    CampoFormulario(stringResource(R.string.contratacion_address_field), direccion, !isLoading, stringResource(R.string.contratacion_address_placeholder), onDireccionChange)
                    
                    // Selector Fecha
                    OutlinedTextField(
                        value = fecha, onValueChange = {},
                        label = { Text(stringResource(R.string.contratacion_date_field)) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { if(!isLoading) onFechaClick() },
                        enabled = false, shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.DateRange, null, tint = MaterialTheme.colorScheme.primary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    // Selector Hora
                    OutlinedTextField(
                        value = hora, onValueChange = {},
                        label = { Text(stringResource(R.string.contratacion_time_field)) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { if(!isLoading) onHoraClick() },
                        enabled = false, shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.AccessTime, null, tint = MaterialTheme.colorScheme.primary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    CampoFormulario(stringResource(R.string.contratacion_offer_field), oferta, !isLoading, "", onOfertaChange)

                    if (errorMessage != null) {
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
                    }

                    Button(
                        onClick = onContratar,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading && direccion.isNotBlank() && fecha.isNotBlank() && hora.isNotBlank() && isAvailable
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        else Text(stringResource(R.string.contratacion_button), fontWeight = FontWeight.Bold)
                    }
                }
            }
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
    ContratacionContent(
        service = Service(title = "Limpieza",), provider = null,
        direccion = "", onDireccionChange = {}, 
        fecha = "", onFechaClick = {},
        hora = "", onHoraClick = {},
        oferta = "", onOfertaChange = {}, isLoading = false, errorMessage = null,
        isAvailable = true,
        onBack = {}, onContratar = {}
    )
}
