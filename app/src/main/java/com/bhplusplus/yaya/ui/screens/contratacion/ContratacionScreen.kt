package com.bhplusplus.yaya.ui.screens.contratacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.data.ServiceRepository
import com.bhplusplus.yaya.data.models.Service

/**
 * PANTALLA DE CONTRATACIÓN
 * Permite al cliente ingresar los detalles para solicitar un servicio.
 * Cumple con las directrices de pasar únicamente el ID del servicio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaContratacion(
    serviceId: String,
    onBack: () -> Unit,
    onContratarClick: () -> Unit,
    viewModel: ContratacionViewModel = viewModel()
) {
    // Obtenemos el servicio desde el repositorio usando el ID
    val service = ServiceRepository.findById(serviceId)

    // Cargamos los datos iniciales del servicio en el ViewModel
    LaunchedEffect(service) {
        viewModel.setInitialData(service)
        // Buscamos la info del prestador real usando el provider_id del servicio
        service.provider_id?.let { viewModel.loadProviderInfo(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.contratacion_request_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
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
            // TARJETA DEL PRESTADOR
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = viewModel.providerProfile?.full_name?.take(1) ?: "P", 
                                color = MaterialTheme.colorScheme.primary, 
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = viewModel.providerProfile?.full_name ?: stringResource(R.string.contratacion_unknown_provider), 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.contratacion_provider_label), 
                                fontSize = 12.sp, 
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    
                    if (viewModel.providerProfile != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        InfoFila(stringResource(R.string.contratacion_phone_label), viewModel.providerProfile?.phone ?: "N/A")
                        InfoFila(stringResource(R.string.profile_address_label), viewModel.providerProfile?.address ?: "N/A")
                    }
                }
            }

            // FORMULARIO DE SOLICITUD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.contratacion_details_section),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    CampoFormulario(
                        etiqueta = stringResource(R.string.contratacion_service_field), 
                        valor = service.title,
                        enabled = false
                    ) { }

                    CampoFormulario(
                        etiqueta = stringResource(R.string.contratacion_address_field), 
                        valor = viewModel.direccion,
                        enabled = !viewModel.isLoading,
                        placeholder = stringResource(R.string.contratacion_address_placeholder)
                    ) { viewModel.direccion = it }

                    CampoFormulario(
                        etiqueta = stringResource(R.string.contratacion_time_field), 
                        valor = viewModel.hora,
                        enabled = !viewModel.isLoading,
                        placeholder = stringResource(R.string.contratacion_time_placeholder)
                    ) { viewModel.hora = it }

                    CampoFormulario(
                        etiqueta = stringResource(R.string.contratacion_offer_field), 
                        valor = viewModel.oferta,
                        enabled = !viewModel.isLoading
                    ) { viewModel.oferta = it }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (viewModel.errorMessage != null) {
                        Text(text = viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)
                    }

                    Button(
                        onClick = { 
                            viewModel.contratar(service) { success ->
                                if (success) onContratarClick()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = !viewModel.isLoading && viewModel.direccion.isNotBlank()
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text(
                                text = stringResource(R.string.contratacion_button),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoFila(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = etiqueta, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(text = valor, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun CampoFormulario(
    etiqueta: String, 
    valor: String, 
    enabled: Boolean = true,
    placeholder: String = "",
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(etiqueta) },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        singleLine = true,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ContratacionPreview() {
    PantallaContratacion(serviceId = "1", onBack = {}, onContratarClick = {})
}
