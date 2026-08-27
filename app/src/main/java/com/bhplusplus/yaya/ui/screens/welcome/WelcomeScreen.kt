package com.bhplusplus.yaya.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bhplusplus.yaya.ui.components.organisms.OnboardingCarousel
import com.bhplusplus.yaya.ui.components.organisms.OnboardingPage
import com.bhplusplus.yaya.ui.components.organisms.WelcomeActions

/**
 * PANTALLA DE BIENVENIDA (Página en Atomic Design)
 * Orquesta los organismos para construir la experiencia inicial.
 */
@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    val onboardingPages = listOf(
        OnboardingPage(
            title = "Encuentra el talento ideal",
            description = "Explora cientos de servicios locales, desde limpieza profunda hasta soporte técnico especializado.",
            icon = Icons.Default.Search
        ),
        OnboardingPage(
            title = "Negocia con libertad",
            description = "Habla directamente con el prestador y llega a un acuerdo de precio justo para ambas partes.",
            icon = Icons.Default.Handshake
        ),
        OnboardingPage(
            title = "Seguridad y Confianza",
            description = "Contrata con tranquilidad. Califica la experiencia y ayuda a construir una comunidad de excelencia.",
            icon = Icons.Default.VerifiedUser
        )
    )

    val pagerState = rememberPagerState { onboardingPages.size }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Organismo: Carrusel de información
        OnboardingCarousel(
            pages = onboardingPages,
            pagerState = pagerState,
            modifier = Modifier.weight(1f)
        )

        // Organismo: Acciones principales
        WelcomeActions(
            onLoginClick = onLoginClick,
            onRegisterClick = onRegisterClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen(onLoginClick = {}, onRegisterClick = {})
    }
}
