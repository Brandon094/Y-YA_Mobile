package com.example.myapplication.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.data.models.Service
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
import com.example.myapplication.ui.screens.contratacion.PantallaContratacion
import com.example.myapplication.ui.screens.confirmation.PantallaReservaConfirmada

/**
 * DEFINICIÓN DE RUTAS (PANTALLAS)
 * Usamos una 'sealed class' para representar las diferentes pantallas de la app.
 * Esto permite un manejo seguro y exhaustivo en el sistema de navegación.
 */
sealed class Screen {
    object Loading : Screen()         // Pantalla de carga (mientras verifica sesión)
    object Welcome : Screen()         // Pantalla de bienvenida
    object Login : Screen()           // Inicio de sesión
    object Reset : Screen()           // Restablecer contraseña
    object RegisterUser : Screen()    // Registro de nuevo usuario
    object Home : Screen()            // Pantalla principal (listado de servicios)
    object Profile : Screen()         // Ver perfil del usuario
    object EditProfile : Screen()     // Formulario para editar datos de perfil
    data class Contratacion(val service: Service) : Screen()    // Pantalla de contratación (Yya2)
    data class Confirmacion(val service: Service) : Screen()    // Pantalla de confirmación (Yya2)
    // ServiceDetail recibe un objeto de tipo 'Service' como argumento
    data class ServiceDetail(val service: Service) : Screen()
}

/**
 * NAVEGACIÓN PRINCIPAL
 * Este componente actúa como el "cerebro" que decide qué pantalla mostrar.
 */
@Composable
fun AppNavigation() {
    // scope permite ejecutar funciones suspendidas (como signOut) desde la UI
    val scope = rememberCoroutineScope()
    
    // Estado que controla qué pantalla se está visualizando actualmente
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Loading) }

    // LaunchedEffect se ejecuta al iniciar la aplicación.
    // Verifica si el usuario tiene una sesión activa en Supabase.
    LaunchedEffect(Unit) {
        val session = SupabaseManager.client.auth.currentSessionOrNull()
        if (session != null) {
            currentScreen = Screen.Home // Si hay sesión, saltamos al Home
        } else {
            currentScreen = Screen.Welcome // Si no, mostramos la Bienvenida
        }
    }

    // Estructura condicional que renderiza la pantalla correspondiente según el estado
    when (val screen = currentScreen) {
        // Indicador visual de carga
        Screen.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Red)
            }
        }

        // Pantalla de Bienvenida: Navega a Login o Registro
        Screen.Welcome -> WelcomeScreen(
            onLoginClick = { currentScreen = Screen.Login },
            onRegisterClick = { currentScreen = Screen.RegisterUser }
        )

        // Login: Si tiene éxito, navega al Home
        Screen.Login -> LoginScreen(
            onLoginSuccess = { currentScreen = Screen.Home },
            onNavigateToReset = { currentScreen = Screen.Reset },
            onNavigateToRegister = { currentScreen = Screen.RegisterUser }
        )

        // Recuperar Contraseña: Al finalizar regresa al Login
        Screen.Reset -> ResetPasswordScreen(
            onPasswordReset = { currentScreen = Screen.Login },
            onBack = { currentScreen = Screen.Login }
        )

        // Registro: Al finalizar regresa al Login para que el usuario entre
        Screen.RegisterUser -> RegisterScreen(
            onRegister = { currentScreen = Screen.Login },
            onGoToLogin = { currentScreen = Screen.Login }
        )

        // Home: Muestra servicios y permite ir al Perfil o Detalle
        Screen.Home -> HomeScreen(
            onServiceClick = { service -> currentScreen = Screen.ServiceDetail(service) },
            onProfileClick = { currentScreen = Screen.Profile },
            onLogout = { currentScreen = Screen.Login }
        )

        // Perfil: Permite editar datos, cambiar clave o cerrar sesión
        Screen.Profile -> ProfileScreen(
            onEditProfile = { currentScreen = Screen.EditProfile },
            onChangePassword = { currentScreen = Screen.Reset },
            onLogout = {
                scope.launch {
                    SupabaseManager.client.auth.signOut() // Cierra sesión en Supabase
                    currentScreen = Screen.Login
                }
            },
            onBack = { currentScreen = Screen.Home }
        )

        // Editar Perfil: Regresa al perfil tras guardar cambios
        Screen.EditProfile -> EditProfileScreen(
            onBack = { currentScreen = Screen.Profile }
        )

        is Screen.ServiceDetail -> ServiceDetailScreen(
            service = screen.service,
            onBack = { currentScreen = Screen.Home },
            onContratar = { currentScreen = Screen.Contratacion(screen.service) }
        )

        // Flujo Yya2: Contratación
        is Screen.Contratacion -> PantallaContratacion(
            service = screen.service,
            onContratarClick = { currentScreen = Screen.Confirmacion(screen.service) }
        )

        // Flujo Yya2: Confirmación
        is Screen.Confirmacion -> PantallaReservaConfirmada(
            service = screen.service,
            onContinuarClick = { currentScreen = Screen.Home }
        )
    }
}

// Previsualización de la navegación (útil durante el desarrollo)
@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    AppNavigation()
}
