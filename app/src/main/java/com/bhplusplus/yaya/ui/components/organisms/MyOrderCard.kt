package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhplusplus.yaya.ui.components.atoms.YayaAvatar
import com.bhplusplus.yaya.ui.components.atoms.YayaStatusBadge
import com.bhplusplus.yaya.ui.components.molecules.DetailRow
import com.bhplusplus.yaya.ui.components.molecules.NegotiationActionPill
import com.bhplusplus.yaya.ui.components.molecules.NegotiationHistoryBox
import com.bhplusplus.yaya.ui.screens.my_orders.MyOrderUiState

@Composable
fun MyOrderCard(
    state: MyOrderUiState,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onNegotiate: () -> Unit,
    onRate: () -> Unit,
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
                        imageUrl = state.providerAvatarUrl,
                        size = 52.dp
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(text = state.serviceTitle, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = state.providerName, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                YayaStatusBadge(state.status)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // CUERPO: Detalles
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DetailRow(Icons.Default.LocationOn, state.address)
                DetailRow(Icons.Default.Payments, "Oferta actual: ${state.formattedPrice}")
                DetailRow(Icons.Default.Event, state.formattedDate)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Molécula: Historial de negociación
            NegotiationHistoryBox(description = state.description)

            if (state.status == "pending" || state.status == "accepted" || state.status == "in_progress") {
                Spacer(modifier = Modifier.height(16.dp))

                if (state.isCounterOfferActive && state.status == "pending") {
                    NegotiationActionPill(text = "Nueva oferta recibida. ¿Aceptas?")
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(
                        onClick = onChat,
                        modifier = Modifier.size(52.dp).background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
                    ) { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat", tint = MaterialTheme.colorScheme.onSecondaryContainer) }

                    when (state.status) {
                        "in_progress" -> {
                            Surface(
                                modifier = Modifier.weight(1f).height(52.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("Trabajo en Curso...", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                        "accepted" -> {
                            Button(
                                onClick = onAccept,
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                            ) {
                                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("CONFIRMAR Y EMPEZAR", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            }
                        }
                        else -> { // State 'pending'
                            OutlinedButton(onClick = onNegotiate, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)) {
                                Text("Negociar", fontWeight = FontWeight.Bold)
                            }

                            if (state.isCounterOfferActive) {
                                Button(
                                    onClick = onAccept,
                                    modifier = Modifier.weight(1.2f).height(52.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = ButtonDefaults.buttonElevation(4.dp)
                                ) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Aceptar", fontWeight = FontWeight.ExtraBold)
                                }
                            } else {
                                TextButton(onClick = onReject, modifier = Modifier.height(52.dp)) {
                                    Text("Cancelar", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            // ACCIÓN DE CALIFICAR
            if (state.status == "completed") {
                Spacer(modifier = Modifier.height(20.dp))
                if (state.isRated) {
                    RatingSummaryBox(score = state.ratingScore ?: 0, comment = state.ratingComment)
                } else {
                    Button(
                        onClick = onRate,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB800))
                    ) {
                        Icon(Icons.Default.Star, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("CALIFICAR SERVICIO", fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun RatingSummaryBox(score: Int, comment: String?) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB800).copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "TU CALIFICACIÓN",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFB800)
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (i in 1..5) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (i <= score) Color(0xFFFFB800) else Color.Gray.copy(alpha = 0.3f)
                    )
                }
            }
            if (!comment.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "\"$comment\"",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
