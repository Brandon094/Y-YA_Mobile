package com.bhplusplus.yaya.ui.screens.confirmation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.data.models.Service

/**
 * PANTALLA DE CONFIRMACIÓN DE RESERVA
 * Muestra el resumen del servicio solicitado exitosamente con datos REALES de Supabase.
 */
@Composable
fun PantallaReservaConfirmada(
    serviceId: String,
    requestId: String,
    onContinuarClick: () -> Unit,
    viewModel: ConfirmacionViewModel = viewModel()
) {
    val context = LocalContext.current

    // Carga de datos reales al iniciar
    LaunchedEffect(requestId) {
        viewModel.loadRequestData(requestId, serviceId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary) // Fondo rojo corporativo
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header YÁYA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.app_brand_yaya),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                letterSpacing = 4.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ícono de confirmación
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "✓", fontSize = 56.sp, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.confirmation_success_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Banner informativo
        Surface(
            modifier = Modifier.padding(horizontal = 24.dp),
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.confirmation_success_message),
                modifier = Modifier.padding(16.dp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tarjeta de detalles (Ticket)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.confirmation_details_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                DetalleReservaFila("🧹", stringResource(R.string.confirmation_service_label), viewModel.servicio)
                DetalleReservaFila("👤", stringResource(R.string.confirmation_provider_label), viewModel.prestador)
                DetalleReservaFila("📅", stringResource(R.string.confirmation_date_label), viewModel.fecha)
                DetalleReservaFila("📍", stringResource(R.string.confirmation_location_label), viewModel.ubicacion)
                DetalleReservaFila("💰", stringResource(R.string.confirmation_price_label), viewModel.precio)
                DetalleReservaFila("⏱", stringResource(R.string.confirmation_time_label), viewModel.tiempo)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        // Botón de acción principal
        val finishedMsg = stringResource(R.string.confirmation_toast_finished)
        Button(
            onClick = { 
                Toast.makeText(context, finishedMsg, Toast.LENGTH_SHORT).show()
                onContinuarClick() 
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 40.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            elevation = ButtonDefaults.buttonElevation(4.dp)
        ) {
            Text(
                text = stringResource(R.string.confirmation_continue_button),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun DetalleReservaFila(icono: String, etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icono, fontSize = 18.sp, modifier = Modifier.width(32.dp))
        Text(
            text = etiqueta,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 14.sp,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = valor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmacionPreview() {
    // Para el preview, PantallaReservaConfirmada requiere serviceId y requestId
    // Pero como tiene ViewModel interno, es mejor crear un content separado si quisiéramos preview limpio.
    // Por ahora, simulamos los datos mínimos.
    Surface {
        Text("Previsualización no disponible directamente para pantallas con lógica de red intensa.")
    }
}
