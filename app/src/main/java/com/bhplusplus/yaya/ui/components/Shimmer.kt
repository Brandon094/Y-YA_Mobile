package com.bhplusplus.yaya.ui.components

import androidx.compose.animation.core.*
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * MODIFICADOR DE SHIMMER EFFECT (Premium UX)
 */
fun Modifier.shimmerEffect(
    shape: Shape? = null
): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer")
    
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing)
        ),
        label = "shimmerOffsetX"
    )

    val baseColor = MaterialTheme.colorScheme.surfaceVariant
    val highlightColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)

    val modifier = if (shape != null) this.clip(shape) else this

    modifier.background(
        brush = Brush.linearGradient(
            colors = listOf(baseColor, highlightColor, baseColor),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}

/**
 * SKELETON PARA TARJETAS DE SERVICIO (Home)
 */
@Composable
fun ServiceItemShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(44.dp).shimmerEffect(CircleShape))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Box(Modifier.fillMaxWidth(0.6f).height(20.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                    Spacer(Modifier.height(8.dp))
                    Box(Modifier.fillMaxWidth(0.4f).height(14.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                }
                Box(Modifier.size(60.dp, 24.dp).shimmerEffect(RoundedCornerShape(4.dp)))
            }
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(7) { Box(Modifier.size(20.dp).shimmerEffect(CircleShape)) }
                }
                Box(Modifier.size(80.dp, 16.dp).shimmerEffect(RoundedCornerShape(4.dp)))
            }
        }
    }
}

/**
 * SKELETON PARA TARJETAS DE PEDIDO / SOLICITUD
 */
@Composable
fun RequestItemShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    Box(Modifier.size(52.dp).shimmerEffect(CircleShape))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Box(Modifier.size(120.dp, 20.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                        Spacer(Modifier.height(8.dp))
                        Box(Modifier.size(100.dp, 14.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                    }
                }
                Box(Modifier.size(70.dp, 24.dp).shimmerEffect(RoundedCornerShape(8.dp)))
            }
            Spacer(Modifier.height(20.dp))
            repeat(3) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Box(Modifier.size(18.dp).shimmerEffect(CircleShape))
                    Spacer(Modifier.width(12.dp))
                    Box(Modifier.size(150.dp, 14.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                }
            }
        }
    }
}

/**
 * SKELETON PARA LISTA DE CHATS
 */
@Composable
fun ChatContactItemShimmer() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(54.dp).shimmerEffect(CircleShape))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(Modifier.size(100.dp, 18.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                    Box(Modifier.size(40.dp, 12.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                }
                Spacer(Modifier.height(8.dp))
                Box(Modifier.fillMaxWidth(0.7f).height(14.dp).shimmerEffect(RoundedCornerShape(4.dp)))
            }
        }
    }
}

/**
 * SKELETON PARA ITEMS DE DISPONIBILIDAD
 */
@Composable
fun AvailabilityItemShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(80.dp, 20.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                Box(Modifier.size(40.dp, 24.dp).shimmerEffect(RoundedCornerShape(12.dp)))
            }
        }
    }
}

/**
 * SKELETON PARA DETALLE DE SERVICIO (Evolucionado)
 */
@Composable
fun ServiceDetailShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Galería Immersiva Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .shimmerEffect()
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-24).dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Título y Precio Shimmer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(Modifier.fillMaxWidth(0.6f).height(32.dp).shimmerEffect(RoundedCornerShape(8.dp)))
                    Box(Modifier.size(90.dp, 44.dp).shimmerEffect(RoundedCornerShape(16.dp)))
                }

                Spacer(Modifier.height(16.dp))

                // Badges Shimmer
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(100.dp, 28.dp).shimmerEffect(RoundedCornerShape(8.dp)))
                    Box(Modifier.size(130.dp, 28.dp).shimmerEffect(RoundedCornerShape(8.dp)))
                }

                Spacer(Modifier.height(32.dp))

                // Card Prestador Shimmer
                Box(Modifier.size(160.dp, 14.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(56.dp).shimmerEffect(CircleShape))
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Box(Modifier.size(140.dp, 18.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                            Spacer(Modifier.height(8.dp))
                            Box(Modifier.size(100.dp, 14.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                        }
                        Box(Modifier.size(48.dp).shimmerEffect(CircleShape))
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Descripción Shimmer
                Box(Modifier.size(140.dp, 14.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(12.dp))
                repeat(4) {
                    Box(Modifier.fillMaxWidth().height(14.dp).padding(vertical = 4.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                }
                Box(Modifier.fillMaxWidth(0.7f).height(14.dp).padding(vertical = 4.dp).shimmerEffect(RoundedCornerShape(4.dp)))

                Spacer(Modifier.height(32.dp))

                // Disponibilidad Shimmer
                Box(Modifier.size(150.dp, 14.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    repeat(7) { Box(Modifier.size(36.dp).shimmerEffect(CircleShape)) }
                }
                Spacer(Modifier.height(16.dp))
                Box(Modifier.size(180.dp, 14.dp).shimmerEffect(RoundedCornerShape(4.dp)))

                Spacer(Modifier.height(32.dp))

                // Reseñas Shimmer
                Box(Modifier.size(140.dp, 14.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(12.dp))
                repeat(2) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(Modifier.size(100.dp, 14.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                            Spacer(Modifier.height(8.dp))
                            Box(Modifier.fillMaxWidth().height(12.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                        }
                    }
                }
            }
        }
    }
}

/**
 * SKELETON PARA PANTALLA DE CONTRATACIÓN
 */
@Composable
fun ContratacionShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header Hero Shimmer
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(56.dp).shimmerEffect(CircleShape))
                Spacer(Modifier.width(16.dp))
                Column {
                    Box(Modifier.size(180.dp, 22.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                    Spacer(Modifier.height(8.dp))
                    Box(Modifier.size(120.dp, 16.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                }
            }
        }

        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            // Sección: Cita
            Column {
                Box(Modifier.size(150.dp, 14.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(Modifier.fillMaxWidth().height(56.dp).shimmerEffect(RoundedCornerShape(12.dp)))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.weight(1f).height(56.dp).shimmerEffect(RoundedCornerShape(12.dp)))
                            Box(Modifier.weight(1f).height(56.dp).shimmerEffect(RoundedCornerShape(12.dp)))
                        }
                    }
                }
            }

            // Sección: Negociación
            Column {
                Box(Modifier.size(150.dp, 14.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(100.dp, 14.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                        Spacer(Modifier.height(20.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Box(Modifier.size(48.dp).shimmerEffect(CircleShape))
                            Spacer(Modifier.width(24.dp))
                            Box(Modifier.size(120.dp, 32.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                            Spacer(Modifier.width(24.dp))
                            Box(Modifier.size(48.dp).shimmerEffect(CircleShape))
                        }
                        Spacer(Modifier.height(20.dp))
                        Box(Modifier.fillMaxWidth(0.8f).height(12.dp).shimmerEffect(RoundedCornerShape(4.dp)))
                    }
                }
            }
        }
    }
}
