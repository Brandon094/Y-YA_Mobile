package com.bhplusplus.yaya.ui.components.molecules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatInputBar(
    textValue: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 12.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textValue,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Mensaje...", fontSize = 15.sp) },
                shape = RoundedCornerShape(28.dp),
                maxLines = 5,
                enabled = enabled,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
            Spacer(Modifier.width(8.dp))
            
            val isSendEnabled = enabled && textValue.isNotBlank()
            
            FloatingActionButton(
                onClick = { if (isSendEnabled) onSendClick() },
                containerColor = if (isSendEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isSendEnabled) Color.White else Color.Gray,
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send, 
                    contentDescription = "Enviar", 
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
