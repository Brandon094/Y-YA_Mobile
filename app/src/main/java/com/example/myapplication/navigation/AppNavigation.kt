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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.myapplication.data.SupabaseManager
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

import com.example.myapplication.ui.screens.profile.ProfileScreen
import com.example.myapplication.ui.screens.edit_profile.EditProfileScreen

// ---------- Screens ----------
sealed class Screen {
    object Loading : Screen() 
    object Welcome : Screen()
    object Login : Screen()
    object Reset : Screen()
    object RegisterUser : Screen()
    object Home : Screen()
    object Profile : Screen() 
    object EditProfile : Screen() // Añadimos Editar Perfil
    data class ServiceDetail(val service: Service) : Screen()
}

// ---------- Navigation ----------
@Composable
fun AppNavigation() {
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Loading) }

    LaunchedEffect(Unit) {
        val session = SupabaseManager.client.auth.currentSessionOrNull()
        if (session != null) {
            currentScreen = Screen.Home
        } else {
            currentScreen = Screen.Welcome
        }
    }

    when (val screen = currentScreen) {
        Screen.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Red)
            }
        }

        // Bienvenida

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
            onProfileClick = { currentScreen = Screen.Profile },
            onLogout = { currentScreen = Screen.Login }
        )

        // Perfil
        Screen.Profile -> ProfileScreen(
            onEditProfile = { currentScreen = Screen.EditProfile },
            onChangePassword = { currentScreen = Screen.Reset },
            onLogout = {
                scope.launch {
                    SupabaseManager.client.auth.signOut()
                    currentScreen = Screen.Login
                }
            },
            onBack = { currentScreen = Screen.Home }
        )

        // Editar Perfil
        Screen.EditProfile -> EditProfileScreen(
            onBack = { currentScreen = Screen.Profile }
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