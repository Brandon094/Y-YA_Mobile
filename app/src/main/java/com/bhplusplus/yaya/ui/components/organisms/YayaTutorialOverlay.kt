package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bhplusplus.yaya.ui.components.molecules.YayaTutorialTooltip
import com.bhplusplus.yaya.utils.TutorialManager

/**
 * Modelo para definir un paso del tutorial in-app.
 * Soporta coordenadas targetBounds para iluminar el componente objetivo.
 */
data class TutorialStep(
    val title: String,
    val description: String,
    val targetBounds: Rect? = null,
    val targetCornerRadius: Dp = 16.dp,
    val targetPadding: Dp = 6.dp
)

/**
 * ORGANISMO ATÓMICO: Overlay de Tutorial In-App (ShowOnce + Spotlight Cutout + Smart Alignment)
 * Muestra una máscara translúcida con recortes transparentes e iluminados sobre
 * el elemento que se está explicando.
 * Ajusta automáticamente el posicionamiento del Tooltip (Arriba/Abajo) para no tapar el objetivo.
 */
@Composable
fun YayaTutorialOverlay(
    tutorialKey: String,
    steps: List<TutorialStep>,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    var isVisible by remember { 
        mutableStateOf(!TutorialManager.hasSeenTutorial(context, tutorialKey)) 
    }
    var currentStepIndex by remember { mutableIntStateOf(0) }

    if (!isVisible || steps.isEmpty()) return

    val currentStep = steps[currentStepIndex]
    val primaryColor = MaterialTheme.colorScheme.primary

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Bloquea clics al fondo */ }
        ) {
            val screenHeightPx = constraints.maxHeight.toFloat()
            val isTargetInBottomHalf = currentStep.targetBounds?.let { bounds ->
                val targetCenterY = bounds.top + (bounds.height / 2f)
                targetCenterY > (screenHeightPx / 2f)
            } ?: false

            // MÁSCARA CANVAS CON RECORTE SPOTLIGHT (BlendMode.Clear)
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
            ) {
                // 1. Pintar fondo oscuro translúcido
                drawRect(color = Color.Black.copy(alpha = 0.75f))

                // 2. Si hay un objetivo definido, hacer el recorte transparente (Spotlight)
                currentStep.targetBounds?.let { bounds ->
                    val padding = currentStep.targetPadding.toPx()
                    val topLeft = Offset(bounds.left - padding, bounds.top - padding)
                    val size = Size(bounds.width + (padding * 2), bounds.height + (padding * 2))
                    val cornerRadius = CornerRadius(currentStep.targetCornerRadius.toPx())

                    // Recorte transparente
                    drawRoundRect(
                        color = Color.Transparent,
                        topLeft = topLeft,
                        size = size,
                        cornerRadius = cornerRadius,
                        blendMode = BlendMode.Clear
                    )

                    // Borde pulsante / Anillo de luz
                    drawRoundRect(
                        color = primaryColor,
                        topLeft = topLeft,
                        size = size,
                        cornerRadius = cornerRadius,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }

            // TARJETA DE TUTORIAL (TOOLTIP)
            // Posicionada dinámicamente arriba o abajo para jamás obstruir el objetivo iluminado
            YayaTutorialTooltip(
                title = currentStep.title,
                description = currentStep.description,
                currentStep = currentStepIndex + 1,
                totalSteps = steps.size,
                onNext = {
                    if (currentStepIndex < steps.size - 1) {
                        currentStepIndex++
                    } else {
                        TutorialManager.markTutorialAsSeen(context, tutorialKey)
                        isVisible = false
                        onDismiss()
                    }
                },
                onSkip = {
                    TutorialManager.markTutorialAsSeen(context, tutorialKey)
                    isVisible = false
                    onDismiss()
                },
                modifier = Modifier
                    .align(if (isTargetInBottomHalf) Alignment.TopCenter else Alignment.BottomCenter)
                    .padding(
                        top = if (isTargetInBottomHalf) 80.dp else 0.dp,
                        bottom = if (isTargetInBottomHalf) 0.dp else 32.dp
                    )
            )
        }
    }
}
