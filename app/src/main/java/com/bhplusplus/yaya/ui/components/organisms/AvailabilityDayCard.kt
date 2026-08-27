package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhplusplus.yaya.ui.components.molecules.TimeSelectorPill
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

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (state.isWorking) MaterialTheme.colorScheme.surface 
                             else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (state.isWorking) 2.dp else 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dayNames[state.dayOfWeek],
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = if (state.isWorking) MaterialTheme.colorScheme.onSurface else Color.Gray
                )
                Switch(
                    checked = state.isWorking, 
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            if (state.isWorking) {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TimeSelectorPill(
                        label = "INICIA",
                        time = state.startTime.take(5),
                        modifier = Modifier.weight(1f),
                        onClick = { showStartPicker = true }
                    )
                    TimeSelectorPill(
                        label = "TERMINA",
                        time = state.endTime.take(5),
                        modifier = Modifier.weight(1f),
                        onClick = { showEndPicker = true }
                    )
                }
            }
        }
    }
}
