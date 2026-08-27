package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.ui.components.atoms.YayaAvatar
import com.bhplusplus.yaya.utils.FormatterUtils

@Composable
fun AdminServiceCard(
    service: Service,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                YayaAvatar(
                    imageUrl = service.provider?.avatar_url,
                    size = 40.dp
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.title, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Por: ${service.provider?.full_name ?: "Desconocido"}", 
                        fontSize = 12.sp, 
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = service.description, 
                fontSize = 14.sp, 
                color = MaterialTheme.colorScheme.onSurfaceVariant, 
                maxLines = 3,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Precio: ${FormatterUtils.formatCurrency(service.price)}", 
                    fontWeight = FontWeight.ExtraBold, 
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp
                )
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = { onReject(service.id!!) },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Red.copy(alpha = 0.1f))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Rechazar", tint = Color.Red)
                }
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = { onApprove(service.id!!) },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f))
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Aprobar", tint = Color(0xFF4CAF50))
                }
            }
        }
    }
}
