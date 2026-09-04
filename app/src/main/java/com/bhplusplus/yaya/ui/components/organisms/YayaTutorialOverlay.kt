package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bhplusplus.yaya.ui.components.molecules.YayaTutorialTooltip
import com.bhplusplus.yaya.utils.TutorialManager

/**
 * Modelo para definir un paso del tutorial in-app.
 */
data class TutorialStep(
    val title: String,
    val description: String
)

/**
 * ORGANISMO ATÓMICO: Overlay de Tutorial In-App (ShowOnce)
 * Muestra una máscara translúcida con la secuencia paso a paso.
 * Guarda la persistencia en TutorialManager para mostrarse UNA SOLA VEZ.
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

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Bloquea clics al fondo */ },
            contentAlignment = Alignment.BottomCenter
        ) {
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
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}
