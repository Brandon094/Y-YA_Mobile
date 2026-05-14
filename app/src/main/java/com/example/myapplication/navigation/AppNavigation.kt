package com.example.myapplication.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.data.Service
import com.example.myapplication.ui.screens.welcome.WelcomeScreen
import com.example.myapplication.ui.screens.home.HomeScreen
import com.example.myapplication.ui.screens.login.LoginScreen
import com.example.myapplication.ui.screens.register.RegisterScreen
import com.example.myapplication.ui.screens.reset.ResetPasswordScreen
import com.example.myapplication.ui.screens.service_detail.ServiceDetailScreen

// ---------- Screens ----------
sealed class Screen {
    object Welcome : Screen()
    object Login : Screen()
    object Reset : Screen()
    object RegisterUser : Screen()
    object Home : Screen()
    data class ServiceDetail(val service: Service) : Screen()
}

// ---------- Navigation ----------
@Composable
fun AppNavigation() {

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Welcome) }

    when (val screen = currentScreen) {

        // Bienvenida
        Screen.Welcome -> WelcomeScreen(
            onLoginClick = { currentScreen = Screen.Login },
            onRegisterClick = { currentScreen = Screen.RegisterUser }
        )

        // Login
        Screen.Login -> LoginScreen(
            onLoginSuccess = { currentScreen = Screen.Home },
            onNavigateToReset = { currentScreen = Screen.Reset }
        )

        // Reset password
        Screen.Reset -> ResetPasswordScreen(
            onPasswordReset = { currentScreen = Screen.Login },
            onBack = { currentScreen = Screen.Login }
        )

        // Registro
        Screen.RegisterUser -> RegisterScreen(
            onRegister = { currentScreen = Screen.Login },
            onGoToLogin = { currentScreen = Screen.Login }
        )

        // Home REAL
        Screen.Home -> HomeScreen(
            onServiceClick = { service -> currentScreen = Screen.ServiceDetail(service) },
            onLogout = { currentScreen = Screen.Login }
        )

        // Detalle de Servicio
        is Screen.ServiceDetail -> ServiceDetailScreen(
            service = screen.service,
            onBack = { currentScreen = Screen.Home }
        )
    }
}

// ---------- Preview ----------
@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    AppNavigation()
}