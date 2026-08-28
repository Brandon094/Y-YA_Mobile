package com.bhplusplus.yaya.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.ui.components.AvailabilityItemShimmer
import com.bhplusplus.yaya.ui.components.atoms.YayaPrimaryButton
import com.bhplusplus.yaya.ui.components.organisms.AvailabilityDayCard

/**
 * PANTALLA DE GESTIÓN DE HORARIO GLOBAL (Atomic Design Refactor)
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
                    YayaPrimaryButton(
                        text = "Guardar Cambios",
                        onClick = {
                            viewModel.saveAvailability {
                                onBack()
                            }
                        },
                        modifier = Modifier.padding(16.dp),
                        isLoading = viewModel.isSaving
                    )
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
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Configura los días y horas en los que estás disponible para trabajar.",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        items(viewModel.daysState) { dayState ->
                            // Organismo: Tarjeta de disponibilidad diaria
                            AvailabilityDayCard(
                                state = dayState,
                                onToggle = { viewModel.toggleDay(dayState.dayOfWeek) },
                                onTimeSelected = { start, end -> 
                                    viewModel.updateTimes(dayState.dayOfWeek, start, end) 
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
