package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhplusplus.yaya.ui.components.atoms.YayaAvatar
import com.bhplusplus.yaya.ui.screens.admin.ReportedUserSummary

@Composable
fun ReportSummaryCard(
    summary: ReportedUserSummary,
    onSuspend: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (severityColor, actionLabel) = when {
        summary.count >= 5 -> Color.Red to "ELIMINACIÓN RECOMENDADA"
        summary.count >= 3 -> Color(0xFFFF9800) to "SUSPENSIÓN RECOMENDADA"
        else -> Color(0xFFFFC107) to "LLAMADO DE ATENCIÓN"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                YayaAvatar(
                    imageUrl = summary.profile?.avatar_url,
                    size = 48.dp,
                    modifier = Modifier.background(severityColor.copy(alpha = 0.1f), CircleShape)
                )
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(summary.profile?.full_name ?: "Usuario Desconocido", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text("ID: ${summary.profile?.id?.take(8)}...", fontSize = 12.sp, color = Color.Gray)
                }
                
                Surface(
                    color = severityColor,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${summary.count} REPORTES",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = severityColor,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(12.dp))

            // Lista de motivos
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                summary.reports.take(3).forEach { report ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(Icons.Default.Error, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(Modifier.width(8.dp))
                        Text(report.reason, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (summary.count > 3) {
                    Text("... y ${summary.count - 3} más", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(start = 22.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { onSuspend(summary.profile?.id!!) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, severityColor)
                ) {
                    Text("Suspender", color = severityColor)
                }

                Button(
                    onClick = { onDelete(summary.profile?.id!!) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (summary.count >= 5) Color.Red else Color.Gray)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            }
        }
    }
}
