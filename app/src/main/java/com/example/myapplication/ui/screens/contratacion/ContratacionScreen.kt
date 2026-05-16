package com.example.myapplication.ui.screens.contratacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.Service

@Composable
fun PantallaContratacion(
    service: Service,
    onContratarClick: () -> Unit,
    viewModel: ContratacionViewModel = viewModel()
) {
    val coral = Color(0xFFE8614A)
    val coralSuave = Color(0xFFFFF0EE)

    LaunchedEffect(service) {
        viewModel.setInitialData(service)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(coral)
                .padding(top = 48.dp, bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            @Suppress("DEPRECATION")
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "YÁYA",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "Conecta. Confía. Contrata.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(coralSuave, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("MC", color = coral, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Maria Chantre", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Prestadora de servicio", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoFila("Edad", "22 Años")
                    InfoFila("Teléfono", "314 288 34256")
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Solicitar servicio",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    CampoFormulario("Servicio", viewModel.servicio) { viewModel.servicio = it }
                    CampoFormulario("Dirección", viewModel.direccion) { viewModel.direccion = it }
                    CampoFormulario("Hora", viewModel.hora) { viewModel.hora = it }
                    CampoFormulario("¿Cuánto ofrece?", viewModel.oferta) { viewModel.oferta = it }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onContratarClick() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = coral)
                    ) {
                        Text(
                            "Contratar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoFila(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        @Suppress("DEPRECATION")
        Text(etiqueta, color = Color.Gray, fontSize = 14.sp)
        Text(valor, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
fun CampoFormulario(etiqueta: String, valor: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(etiqueta, fontSize = 14.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFE8614A),
            focusedLabelColor = Color(0xFFE8614A)
        )
    )
}
