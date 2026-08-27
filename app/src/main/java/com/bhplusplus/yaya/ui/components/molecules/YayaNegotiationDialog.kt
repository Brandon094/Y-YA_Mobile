package com.bhplusplus.yaya.ui.components.molecules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun YayaNegotiationDialog(
    modifier: Modifier = Modifier,
    initialPrice: Double,
    minPrice: Double = 0.0,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    title: String = "Proponer Contraoferta",
    subtitle: String = "",
    priceLabel: String = "Incrementos de $5,000",
    errorLabel: String = ""
) {
    var counterPrice by remember { mutableStateOf(initialPrice.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { 
            Text(
                text = title, 
                fontWeight = FontWeight.Bold, 
                color = MaterialTheme.colorScheme.primary
            ) 
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (subtitle.isNotEmpty()) {
                    Text(text = subtitle, fontSize = 13.sp, color = Color.Gray)
                    Spacer(Modifier.height(20.dp))
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilledIconButton(
                        onClick = {
                            val price = counterPrice.toDoubleOrNull() ?: 0.0
                            if (price > minPrice) {
                                counterPrice = (price - 5000).coerceAtLeast(minPrice).toInt().toString()
                            }
                        },
                        enabled = (counterPrice.toDoubleOrNull() ?: 0.0) > minPrice,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) { Icon(Icons.Default.Remove, "Menos") }

                    OutlinedTextField(
                        value = counterPrice,
                        onValueChange = { newValue ->
                            val numeric = newValue.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                            counterPrice = if (numeric < minPrice) minPrice.toInt().toString() else numeric.toInt().toString()
                        },
                        modifier = Modifier.width(130.dp).padding(horizontal = 8.dp),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center, 
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
                            val price = counterPrice.toDoubleOrNull() ?: 0.0
                            counterPrice = (price + 5000).toInt().toString()
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) { Icon(Icons.Default.Add, "Más") }
                }
                
                Spacer(Modifier.height(12.dp))
                Text(
                    text = if (errorLabel.isNotEmpty() && (counterPrice.toDoubleOrNull() ?: 0.0) <= minPrice) errorLabel else priceLabel, 
                    style = MaterialTheme.typography.labelSmall, 
                    color = if (errorLabel.isNotEmpty() && (counterPrice.toDoubleOrNull() ?: 0.0) <= minPrice) MaterialTheme.colorScheme.error else Color.Gray.copy(alpha = 0.6f)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(counterPrice) },
                shape = RoundedCornerShape(12.dp),
                enabled = (counterPrice.toDoubleOrNull() ?: 0.0) >= minPrice
            ) { Text("Enviar Propuesta") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
