package com.bhplusplus.yaya.ui.screens.incoming_requests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.ui.theme.YYATheme
import androidx.compose.ui.tooling.preview.Preview
import com.bhplusplus.yaya.data.models.ServiceRequest

/**
 * PANTALLA DE SOLICITUDES RECIBIDAS (VISTA PRESTADOR)
 * Ahora incluye sistema de Contraoferta (Subasta) para negociar el precio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomingRequestsScreen(
    onBack: () -> Unit,
    onChatClick: (String, String) -> Unit, // receiverId, receiverName
    viewModel: IncomingRequestsViewModel = viewModel()
) {
    var showNegotiationDialog by remember { mutableStateOf<ServiceRequest?>(null) }
    var counterPrice by remember { mutableStateOf("") }

    // Dialogo de Contraoferta (Premium UX)
    if (showNegotiationDialog != null) {
        val currentRequest = showNegotiationDialog!!
        
        // Inicializamos con el precio actual de la solicitud
        LaunchedEffect(currentRequest) {
            counterPrice = currentRequest.final_price.toInt().toString()
        }

        AlertDialog(
            onDismissRequest = { showNegotiationDialog = null },
            title = { 
                Text(
                    "Proponer Contraoferta", 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ) 
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Precio propuesto por cliente: $${currentRequest.final_price.toInt()}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(20.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilledIconButton(
                            onClick = {
                                val current = counterPrice.toDoubleOrNull() ?: 0.0
                                counterPrice = (current - 5000).coerceAtLeast(0.0).toInt().toString()
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Icon(Icons.Default.Remove, "Menos")
                        }

                        OutlinedTextField(
                            value = counterPrice,
                            onValueChange = { counterPrice = it.filter { char -> char.isDigit() } },
                            modifier = Modifier.width(130.dp).padding(horizontal = 8.dp),
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            prefix = { Text("$") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )

                        FilledIconButton(
                            onClick = {
                                val current = counterPrice.toDoubleOrNull() ?: 0.0
                                counterPrice = (current + 5000).toInt().toString()
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Icon(Icons.Default.Add, "Más")
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Incrementos de $5,000",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray.copy(alpha = 0.6f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendCounterOffer(currentRequest, counterPrice)
                        showNegotiationDialog = null
                        counterPrice = ""
                    },
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Enviar Propuesta") }
            },
            dismissButton = {
                TextButton(onClick = { showNegotiationDialog = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.incoming_requests_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (viewModel.requests.isEmpty()) {
                EmptyIncomingRequestsView()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewModel.requests) { request ->
                        IncomingRequestItem(
                            request = request,
                            onAccept = { viewModel.updateRequestStatus(request.id!!, "accepted") },
                            onReject = { viewModel.updateRequestStatus(request.id!!, "cancelled") },
                            onNegotiate = { showNegotiationDialog = request },
                            onChat = {
                                onChatClick(request.client_id, request.client?.full_name ?: "Cliente")
                            },
                            isClientOfferPending = viewModel.isClientOfferPending(request),
                            formattedDate = viewModel.formatDate(request.scheduled_date)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Cada tarjeta de solicitud para el prestador.
 */
@Composable
fun IncomingRequestItem(
    request: ServiceRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onNegotiate: () -> Unit,
    onChat: () -> Unit,
    isClientOfferPending: Boolean,
    formattedDate: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // HEADER: Avatar Cliente + Nombre + Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (request.client?.avatar_url != null) {
                            AsyncImage(
                                model = request.client.avatar_url,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = request.client?.full_name ?: "Cliente",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = request.services?.title ?: "Servicio Solicitado",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                StatusBadge(request.status)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(16.dp))

            // DETALLES: Ubicación, Precio y Fecha
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DetailRow(Icons.Default.LocationOn, request.service_address)
                DetailRow(Icons.Default.Payments, "Propuesta actual: $${request.final_price.toInt()}")
                DetailRow(Icons.Default.Event, formattedDate)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECCIÓN DE NEGOCIACIÓN
            Text(
                text = "ESTADO DE LA PROPUESTA",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(8.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info, 
                        contentDescription = null, 
                        modifier = Modifier.size(16.dp).padding(top = 2.dp), 
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .heightIn(max = 120.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = request.request_description ?: "Sin detalles adicionales",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ACCIONES (Solo si está pendiente)
            if (request.status == "pending") {
                Spacer(modifier = Modifier.height(16.dp))

                if (isClientOfferPending) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Nueva oferta recibida. ¿Aceptas?",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón CHAT
                    IconButton(
                        onClick = onChat,
                        modifier = Modifier
                            .size(52.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chatear", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    }

                    // Botón Contraoferta
                    OutlinedButton(
                        onClick = onNegotiate,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Gavel, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Negociar", fontWeight = FontWeight.Bold)
                    }

                    // Botón Aceptar (Solo si hay oferta del cliente)
                    if (isClientOfferPending) {
                        Button(
                            onClick = onAccept,
                            modifier = Modifier.weight(1.1f).height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(4.dp)
                        ) {
                            Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Aceptar", fontWeight = FontWeight.ExtraBold)
                        }
                    } else {
                        // Opción de Rechazar si no hay oferta pendiente de aceptar
                        TextButton(
                            onClick = onReject,
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text(stringResource(R.string.incoming_requests_reject), color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                
                // Si hay oferta del cliente, mostramos el rechazar abajo pequeño para no quitar espacio
                if (isClientOfferPending) {
                    Spacer(Modifier.height(8.dp))
                    TextButton(
                        onClick = onReject,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(stringResource(R.string.incoming_requests_reject), color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, textColor, label) = when (status) {
        "pending" -> Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer, "Pendiente")
        "accepted" -> Triple(Color(0xFFE8F5E9), Color(0xFF4CAF50), "Aceptada") // Mantener verde éxito para aceptación
        "completed" -> Triple(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer, "Completada")
        "cancelled" -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, "Cancelada")
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, status)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun EmptyIncomingRequestsView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Spacer(Modifier.height(16.dp))
        Text(text = stringResource(R.string.incoming_requests_empty), color = Color.Gray)
    }
}

@Composable
fun DetailRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text, 
            fontSize = 14.sp, 
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IncomingRequestsPreview() {
    val dummyClient = com.bhplusplus.yaya.data.models.UserProfile(
        id = "2",
        full_name = "Brandon Brandon",
        role = "client",
        avatar_url = null
    )
    
    val dummyService = com.bhplusplus.yaya.data.models.Service(
        title = "Limpieza de Apartamento",
        price = 60000.0,
        provider_id = "1"
    )
    
    val dummyRequest = ServiceRequest(
        id = "1",
        client_id = "2",
        service_id = "1",
        final_price = 60000.0,
        request_description = "Oferta inicial: 60000. Programado para: 2026-08-30 a las 10:00\n🔹 Nueva oferta Cliente: $65000",
        service_address = "Carrera 7 #10-20",
        scheduled_date = "2026-08-30T10:00:00Z",
        status = "pending",
        client = dummyClient,
        services = dummyService
    )

    YYATheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                "Vista Previa: Solicitudes Recibidas",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            IncomingRequestItem(
                request = dummyRequest,
                onAccept = {},
                onReject = {},
                onNegotiate = {},
                onChat = {},
                isClientOfferPending = true,
                formattedDate = "2026-08-30"
            )
        }
    }
}
