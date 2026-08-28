package com.bhplusplus.yaya.ui.components.molecules

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhplusplus.yaya.ui.components.atoms.YayaTextField

@Composable
fun YayaRatingDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit,
    isSubmitting: Boolean,
    modifier: Modifier = Modifier
) {
    var score by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            Text(
                text = "Calificar Servicio",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "¿Cómo calificarías el trabajo recibido?",
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(16.dp))
                Row {
                    for (i in 1..5) {
                        IconButton(onClick = { score = i }) {
                            Icon(
                                imageVector = if (i <= score) Icons.Default.Star else Icons.Default.StarOutline,
                                contentDescription = null,
                                tint = if (i <= score) Color(0xFFFFB800) else Color.Gray
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                
                YayaTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = "Escribe un comentario (opcional)",
                    singleLine = false,
                    modifier = Modifier.heightIn(min = 100.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(score, comment) },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB800))
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Enviar Calificación", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSubmitting
            ) {
                Text("Cancelar")
            }
        }
    )
}
