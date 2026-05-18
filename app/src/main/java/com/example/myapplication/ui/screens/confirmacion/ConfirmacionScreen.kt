package com.example.myapplication.ui.screens.confirmacion

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.models.Service

@Composable
fun PantallaReservaConfirmada(
    service: Service,
    onContinuarClick: () -> Unit,
    viewModel: ConfirmacionViewModel = viewModel()
) {
    val coral = Color(0xFFE8614A)
    val context = LocalContext.current

    LaunchedEffect(service) {
        viewModel.setServiceData(service)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(coral),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header YÁYA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            @Suppress("DEPRECATION")
            Text(
                text = "YÁYA",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 4.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ícono de confirmación
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(Color.White.copy(alpha = 0.2f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "✓", fontSize = 48.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        @Suppress("DEPRECATION")
        Text(
            text = "¡Reserva confirmada!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Banner mensaje
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(Color.White.copy(alpha = 0.25f), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            @Suppress("DEPRECATION")
            Text(
                text = "Tu servicio ha sido reservado exitosamente.",
                fontSize = 13.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta de detalles
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F7))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                @Suppress("DEPRECATION")
                Text(
                    text = "Detalles de tu reserva",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = coral,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(12.dp))

                DetalleReservaFila("🧹", "Servicio", viewModel.servicio)
                DetalleReservaFila("👤", "Prestador", viewModel.prestador)
                DetalleReservaFila("📅", "Fecha", viewModel.fecha)
                DetalleReservaFila("📍", "Ubicación", viewModel.ubicacion)
                DetalleReservaFila("💰", "Precio", viewModel.precio)
                DetalleReservaFila("⏱", "Tiempo", viewModel.tiempo)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón continuar
        Button(
            onClick = { 
                Toast.makeText(context, "Reserva finalizada", Toast.LENGTH_SHORT).show()
                onContinuarClick() 
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            )
        ) {
            Text(
                text = "Continuar",
                color = coral,
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
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        @Suppress("DEPRECATION")
        Text(text = icono, fontSize = 16.sp, modifier = Modifier.width(28.dp))
        @Suppress("DEPRECATION")
        Text(
            text = etiqueta,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = valor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color(0xFF333333)
        )
    }
}
