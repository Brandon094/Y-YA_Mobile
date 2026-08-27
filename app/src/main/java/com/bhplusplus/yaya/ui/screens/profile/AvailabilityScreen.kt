package com.bhplusplus.yaya.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.ui.components.AvailabilityItemShimmer
import java.util.Locale

/**
 * PANTALLA DE GESTIÓN DE HORARIO GLOBAL
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailabilityScreen(
    onBack: () -> Unit,
    viewModel: AvailabilityViewModel = viewModel()
) {
    val pullToRefreshState = androidx.compose.material3.pulltorefresh.rememberPullToRefreshState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Horario General", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(modifier = Modifier.navigationBarsPadding()) {
                    Button(
                        onClick = {
                            viewModel.saveAvailability {
                                onBack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !viewModel.isSaving
                    ) {
                        if (viewModel.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = viewModel.isLoading,
            onRefresh = { viewModel.loadAvailability() },
            state = pullToRefreshState,
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (viewModel.isLoading && viewModel.daysState.all { !it.isWorking }) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(7) {
                            AvailabilityItemShimmer()
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "Configura los días y horas en los que estás disponible para trabajar.",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        items(viewModel.daysState) { dayState ->
                            DayAvailabilityItem(
                                state = dayState,
                                onToggle = { viewModel.toggleDay(dayState.dayOfWeek) },
                                onTimeSelected = { start, end -> viewModel.updateTimes(dayState.dayOfWeek, start, end) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayAvailabilityItem(
    state: DayAvailabilityState,
    onToggle: () -> Unit,
    onTimeSelected: (String, String) -> Unit
) {
    val dayNames = listOf("", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    if (showStartPicker) {
        TimePickerDialog(
            onDismiss = { showStartPicker = false },
            onConfirm = { h, m ->
                val time = String.format(Locale.getDefault(), "%02d:%02d:00", h, m)
                onTimeSelected(time, state.endTime)
                showStartPicker = false
            }
        )
    }

    if (showEndPicker) {
        TimePickerDialog(
            onDismiss = { showEndPicker = false },
            onConfirm = { h, m ->
                val time = String.format(Locale.getDefault(), "%02d:%02d:00", h, m)
                onTimeSelected(state.startTime, time)
                showEndPicker = false
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (state.isWorking) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dayNames[state.dayOfWeek],
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (state.isWorking) MaterialTheme.colorScheme.onSurface else Color.Gray
                )
                Switch(checked = state.isWorking, onCheckedChange = { onToggle() })
            }

            if (state.isWorking) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimeSelector(
                        label = "Inicia",
                        time = state.startTime.take(5),
                        modifier = Modifier.weight(1f),
                        onClick = { showStartPicker = true }
                    )
                    TimeSelector(
                        label = "Termina",
                        time = state.endTime.take(5),
                        modifier = Modifier.weight(1f),
                        onClick = { showEndPicker = true }
                    )
                }
            }
        }
    }
}

@Composable
fun TimeSelector(label: String, time: String, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier.height(50.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(label, fontSize = 10.sp, color = Color.Gray)
                Text(time, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(onDismiss: () -> Unit, onConfirm: (Int, Int) -> Unit) {
    val state = rememberTimePickerState(is24Hour = true)
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour, state.minute) }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                TimePicker(state = state)
            }
        }
    )
}
