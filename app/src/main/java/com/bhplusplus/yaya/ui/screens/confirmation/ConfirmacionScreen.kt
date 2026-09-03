package com.bhplusplus.yaya.ui.screens.confirmation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.ui.components.ContratacionShimmer
import com.bhplusplus.yaya.ui.components.atoms.YayaPrimaryButton
import com.bhplusplus.yaya.ui.components.organisms.ConfirmationTicketCard
import com.bhplusplus.yaya.ui.components.organisms.SuccessHeroBanner

/**
 * PANTALLA DE CONFIRMACIÓN DE RESERVA (Atomic Design Refactor)
 * Orquestada mediante componentes atómicos reutilizables.
 */
@Composable
fun PantallaReservaConfirmada(
    serviceId: String,
    requestId: String,
    onContinuarClick: () -> Unit,
    viewModel: ConfirmacionViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    LaunchedEffect(requestId) {
        viewModel.loadRequestData(requestId, serviceId)
    }

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(modifier = Modifier.navigationBarsPadding()) {
                    YayaPrimaryButton(
                        text = stringResource(R.string.confirmation_continue_button).uppercase(),
                        onClick = {
                            Toast.makeText(context, "¡Regresando al inicio!", Toast.LENGTH_SHORT).show()
                            onContinuarClick()
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    ) { padding ->
        if (viewModel.isLoading && uiState == null) {
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                ContratacionShimmer()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Organismo: Hero Banner de Éxito
                SuccessHeroBanner(
                    title = stringResource(R.string.confirmation_success_title),
                    subtitle = stringResource(R.string.confirmation_success_message)
                )

                if (uiState != null) {
                    // CUERPO: TICKET DE DETALLES (Organismo)
                    Column(modifier = Modifier.padding(24.dp)) {
                        ConfirmationTicketCard(state = uiState)

                        Spacer(modifier = Modifier.height(24.dp))

                        // NOTA DE SEGURIDAD (Sección persistente)
                        Surface(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = "Recuerda que el prestador puede enviarte una contraoferta. Mantente atento al chat.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmacionPreview() {
    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Color.White)) {
            Text("Vista Previa de Confirmación Atómica")
        }
    }
}
