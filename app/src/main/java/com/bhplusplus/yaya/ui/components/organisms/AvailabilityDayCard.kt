package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhplusplus.yaya.ui.components.molecules.YayaTimePickerDialog
import com.bhplusplus.yaya.ui.screens.profile.DayAvailabilityState
import java.util.Locale

@Composable
fun AvailabilityDayCard(
    state: DayAvailabilityState,
    onToggle: () -> Unit,
    onTimeSelected: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayNames = listOf("", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    if (showStartPicker) {
        YayaTimePickerDialog(
            onDismiss = { showStartPicker = false },
            onConfirm = { h, m ->
                val time = String.format(Locale.getDefault(), "%02d:%02d:00", h, m)
                onTimeSelected(time, state.endTime)
                showStartPicker = false
            },
            initialHour = state.startTime.substringBefore(":").toIntOrNull() ?: 8
        )
    }

    if (showEndPicker) {
        YayaTimePickerDialog(
            onDismiss = { showEndPicker = false },
            onConfirm = { h, m ->
                val time = String.format(Locale.getDefault(), "%02d:%02d:00", h, m)
                onTimeSelected(state.startTime, time)
                showEndPicker = false
            },
            initialHour = state.endTime.substringBefore(":").toIntOrNull() ?: 18
        )
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (state.isWorking) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        border = if (state.isWorking) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // DÍA
            Text(
                text = dayNames[state.dayOfWeek],
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = if (state.isWorking) MaterialTheme.colorScheme.onSurface else Color.Gray,
                modifier = Modifier.width(80.dp)
            )

            // RANGO DE HORAS O DESCANSO
            if (state.isWorking) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        onClick = { showStartPicker = true },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = state.startTime.take(5),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text("➔", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    Surface(
                        onClick = { showEndPicker = true },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = state.endTime.take(5),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            } else {
                Text(
                    text = "Día de descanso",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            // SWITCH
            Switch(
                checked = state.isWorking,
                onCheckedChange = { onToggle() },
                modifier = Modifier.scale(0.85f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
