package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
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
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.ui.components.atoms.YayaAvatar
import com.bhplusplus.yaya.ui.components.atoms.YayaStatusBadge
import com.bhplusplus.yaya.ui.components.molecules.DetailRow
import com.bhplusplus.yaya.ui.components.molecules.NegotiationHistoryBox
import com.bhplusplus.yaya.ui.screens.incoming_requests.IncomingRequestUiState

@Composable
fun IncomingRequestCard(
    state: IncomingRequestUiState,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onNegotiate: () -> Unit,
    onChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // HEADER: Avatar + Nombre + Badge Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    YayaAvatar(
                        imageUrl = state.clientAvatarUrl,
                        size = 52.dp
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(text = state.clientName, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = state.serviceTitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                YayaStatusBadge(state.status)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(16.dp))

            // CUERPO: Detalles
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DetailRow(Icons.Default.LocationOn, state.address)
                DetailRow(Icons.Default.Payments, "Propuesta actual: ${state.formattedPrice}")
                DetailRow(Icons.Default.Event, state.formattedDate)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Molécula: Historial de negociación
            NegotiationHistoryBox(description = state.description)

            if (state.status == "pending" || state.status == "accepted" || state.status == "in_progress") {
                Spacer(modifier = Modifier.height(16.dp))

                if (state.isClientOfferPending && state.status == "pending") {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(Modifier.width(10.dp))
                            Text("Nueva oferta recibida. ¿Aceptas?", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // BOTÓN CHAT
                    IconButton(
                        onClick = onChat,
                        modifier = Modifier.size(52.dp).background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
                    ) { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chatear", tint = MaterialTheme.colorScheme.onSecondaryContainer) }

                    when (state.status) {
                        "in_progress" -> {
                            Button(
                                onClick = onAccept,
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                            ) {
                                Icon(Icons.Default.DoneAll, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Finalizar Trabajo", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        "accepted" -> {
                            Surface(
                                modifier = Modifier.weight(1f).height(52.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("Esperando al Cliente...", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                }
                            }
                        }
                        else -> { // State 'pending'
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

                            if (state.isClientOfferPending) {
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
                                TextButton(onClick = onReject, modifier = Modifier.height(52.dp)) {
                                    Text(stringResource(R.string.incoming_requests_reject), color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
                
                if (state.status == "accepted" || state.status == "in_progress" || state.isClientOfferPending) {
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onReject, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                        Text(
                            text = when (state.status) {
                                "accepted" -> "Cancelar Servicio"
                                "in_progress" -> "Cancelar Trabajo en Curso"
                                else -> stringResource(R.string.incoming_requests_reject)
                            }, 
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
