package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhplusplus.yaya.ui.components.atoms.YayaSectionHeader
import com.bhplusplus.yaya.ui.screens.confirmation.ConfirmationUiState

@Composable
fun ConfirmationTicketCard(
    state: ConfirmationUiState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        YayaSectionHeader(title = "RESUMEN DE TU SOLICITUD")
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // BLOQUE: SERVICIO
                ConfirmationDetailRow(Icons.Default.Build, "Servicio", state.serviceTitle)
                ConfirmationDetailRow(Icons.Default.Person, "Prestador", state.providerName)
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                
                // BLOQUE: CITA
                ConfirmationDetailRow(Icons.Default.Event, "Fecha", state.formattedDate)
                ConfirmationDetailRow(Icons.Default.AccessTime, "Hora programada", state.formattedTime)
                ConfirmationDetailRow(Icons.Default.LocationOn, "Lugar de atención", state.address)

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

                // BLOQUE: ECONOMÍA
                ConfirmationDetailRow(Icons.Default.Payments, "Precio base", state.formattedBasePrice)
                
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Gavel, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("TU OFERTA FINAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(state.formattedOfferPrice, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfirmationDetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp).padding(top = 2.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
