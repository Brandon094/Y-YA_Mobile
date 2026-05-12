package com.example.myapplication.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.screens.welcome.WelcomeScreen
import com.example.myapplication.ui.screens.home.HomeScreen
import com.example.myapplication.ui.screens.login.LoginScreen
import com.example.myapplication.ui.screens.register.RegisterScreen
import com.example.myapplication.ui.screens.reset.ResetPasswordScreen

// ---------- Screens ----------
sealed class Screen {
    object Welcome : Screen()
    object Login : Screen()
    object Reset : Screen()
    object RegisterUser : Screen()
    object Home : Screen()
}

// ---------- Navigation ----------
@Composable
fun AppNavigation() {

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Welcome) }

    when (currentScreen) {

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
        Screen.Home -> HomeScreen()
    }
}

// ---------- Preview ----------
@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    AppNavigation()
}