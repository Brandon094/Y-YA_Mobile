package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhplusplus.yaya.ui.screens.my_services.MyServiceUiState

@Composable
fun MyServiceCard(
    state: MyServiceUiState,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar servicio?") },
            text = { Text("Se borrará permanentemente de la plataforma YÁYA.") },
            confirmButton = {
                Button(
                    onClick = { 
                        onDelete()
                        showDeleteDialog = false 
                    }, 
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.title, 
                        fontWeight = FontWeight.ExtraBold, 
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = state.formattedPrice, 
                        color = MaterialTheme.colorScheme.primary, 
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Switch(
                        checked = state.isActive,
                        onCheckedChange = { if (!state.isPending) onToggleStatus() },
                        enabled = !state.isPending,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = state.statusLabel,
                        fontSize = 10.sp,
                        color = Color(state.statusColor),
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = state.description, 
                fontSize = 14.sp, 
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f), 
                maxLines = 2
            )
            
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Editar información", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
