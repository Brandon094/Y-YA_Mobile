package com.bhplusplus.yaya.ui.screens.incoming_requests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.data.models.ServiceRequest

/**
 * PANTALLA DE SOLICITUDES RECIBIDAS (VISTA PRESTADOR)
 * Permite al prestador ver quién lo ha contratado y gestionar las solicitudes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomingRequestsScreen(
    onBack: () -> Unit,
    viewModel: IncomingRequestsViewModel = viewModel()
) {
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
                            onReject = { viewModel.updateRequestStatus(request.id!!, "cancelled") }
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
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Info del Cliente
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = request.client?.full_name ?: "Cliente Desconocido",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                StatusBadge(request.status)
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))

            // Info del Servicio
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(text = request.services?.title ?: "Servicio solicitado", fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(text = request.service_address, fontSize = 14.sp)
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(text = request.scheduled_date?.take(16)?.replace("T", " ") ?: "Sin fecha", fontSize = 14.sp)
            }

            if (!request.request_description.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = request.request_description,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ACCIONES (Solo si está pendiente)
            if (request.status == "pending") {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(stringResource(R.string.incoming_requests_reject))
                    }
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.incoming_requests_accept))
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, textColor, label) = when (status) {
        "pending" -> Triple(Color(0xFFFFF4E5), Color(0xFFFF9800), "Pendiente")
        "accepted" -> Triple(Color(0xFFE8F5E9), Color(0xFF4CAF50), "Aceptada")
        "completed" -> Triple(Color(0xFFE3F2FD), Color(0xFF2196F3), "Completada")
        "cancelled" -> Triple(Color(0xFFFFEBEE), Color(0xFFF44336), "Cancelada")
        else -> Triple(Color.LightGray, Color.DarkGray, status)
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
