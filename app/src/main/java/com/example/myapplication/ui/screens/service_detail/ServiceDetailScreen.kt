package com.example.myapplication.ui.screens.service_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.Service

/**
 * PANTALLA DE DETALLE DEL SERVICIO
 * Muestra información extendida sobre un servicio seleccionado, incluyendo
 * descripción, beneficios y opciones de contratación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    service: Service, // Objeto con la info del servicio
    onBack: () -> Unit // Función para regresar a la lista
) {
    Scaffold(
        topBar = {
            // Barra superior personalizada con el título y botón de retorno
            TopAppBar(
                title = { Text("Detalles del Servicio", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        },
        bottomBar = {
            // Barra inferior fija con el botón principal de acción
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = { /* Lógica de contratación - MVP Placeholder */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        "Solicitar este servicio",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { padding ->
        // Contenido con soporte para scroll si la descripción es larga
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Cabecera Visual con Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Column(modifier = Modifier.padding(24.dp)) {
                // Título y calificación (ficticia para el MVP)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = service.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB800))
                        Text("4.8", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Etiqueta de estatus/verificación
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Servicio Verificado",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sección de Descripción
                Text(
                    text = "Descripción del servicio",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = service.description,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sección de Beneficios (Listado estático para demostración)
                Text(
                    text = "¿Qué incluye?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                val benefits = listOf(
                    "Personal altamente capacitado",
                    "Equipos y materiales incluidos",
                    "Seguro contra accidentes",
                    "Garantía de satisfacción"
                )

                benefits.forEach { benefit ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = benefit, 
                            fontSize = 14.sp, 
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp)) // Espacio de seguridad para el BottomBar
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceDetailScreenPreview() {
    ServiceDetailScreen(
        service = Service(
            title = "Limpieza profunda",
            description = "Una limpieza detallada que incluye vidrios, pisos y rincones difíciles."
        ),
        onBack = {}
    )
}
