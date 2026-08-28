package com.bhplusplus.yaya.ui.components.molecules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YayaReportDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    isReporting: Boolean,
    modifier: Modifier = Modifier
) {
    val reasons = listOf("Servicio falso", "Acoso", "Fraude", "Spam", "Suplantación")
    var selectedReason by remember { mutableStateOf(reasons[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            Text(
                text = "Reportar Prestador",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Selecciona el motivo de la denuncia:",
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(16.dp))
                reasons.forEach { reason ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (reason == selectedReason),
                            onClick = { selectedReason = reason }
                        )
                        Text(
                            text = reason,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedReason) },
                enabled = !isReporting,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                if (isReporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Enviar Reporte", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isReporting
            ) {
                Text("Cancelar")
            }
        }
    )
}
