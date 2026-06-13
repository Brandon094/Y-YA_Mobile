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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.myapplication.data.SupabaseManager
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

import com.example.myapplication.ui.screens.profile.ProfileScreen
import com.example.myapplication.ui.screens.edit_profile.EditProfileScreen
import com.example.myapplication.ui.screens.contratacion.PantallaContratacion
import com.example.myapplication.ui.screens.confirmation.PantallaReservaConfirmada

/**
 * DEFINICIÓN DE RUTAS (PANTALLAS) CON SEGURIDAD DE TIPOS
 */
@Serializable object LoadingRoute
@Serializable object WelcomeRoute
@Serializable object LoginRoute
@Serializable object ResetRoute
@Serializable object RegisterRoute
@Serializable object HomeRoute
@Serializable object ProfileRoute
@Serializable object EditProfileRoute
@Serializable data class ServiceDetailRoute(val service: Service)
@Serializable data class ContratacionRoute(val service: Service)
@Serializable data class ConfirmacionRoute(val service: Service)

/**
 * NAVEGACIÓN PRINCIPAL
 * Utiliza Jetpack Compose Navigation con Type-Safety.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    
    // LaunchedEffect para verificar sesión al iniciar
    LaunchedEffect(Unit) {
        val session = SupabaseManager.client.auth.currentSessionOrNull()
        if (session != null) {
            navController.navigate(HomeRoute) {
                popUpTo(LoadingRoute) { inclusive = true }
            }
        } else {
            navController.navigate(WelcomeRoute) {
                popUpTo(LoadingRoute) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = LoadingRoute
    ) {
        // Pantalla de Carga Inicial
        composable<LoadingRoute> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Red)
            }
        }

        // Bienvenida
        composable<WelcomeRoute> {
            WelcomeScreen(
                onLoginClick = { navController.navigate(LoginRoute) },
                onRegisterClick = { navController.navigate(RegisterRoute) }
            )
        }

        // Login
        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = { 
                    navController.navigate(HomeRoute) {
                        popUpTo(WelcomeRoute) { inclusive = true }
                    }
                },
                onNavigateToReset = { navController.navigate(ResetRoute) },
                onNavigateToRegister = { navController.navigate(RegisterRoute) }
            )
        }

        // Recuperar Contraseña
        composable<ResetRoute> {
            ResetPasswordScreen(
                onPasswordReset = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        // Registro
        composable<RegisterRoute> {
            RegisterScreen(
                onRegister = { navController.navigate(LoginRoute) },
                onGoToLogin = { navController.navigate(LoginRoute) }
            )
        }

        // Home
        composable<HomeRoute> {
            HomeScreen(
                onServiceClick = { service -> navController.navigate(ServiceDetailRoute(service)) },
                onProfileClick = { navController.navigate(ProfileRoute) },
                onLogout = {
                    scope.launch {
                        SupabaseManager.client.auth.signOut()
                        navController.navigate(LoginRoute) {
                            popUpTo(HomeRoute) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Perfil
        composable<ProfileRoute> {
            ProfileScreen(
                onEditProfile = { navController.navigate(EditProfileRoute) },
                onChangePassword = { navController.navigate(ResetRoute) },
                onLogout = {
                    scope.launch {
                        SupabaseManager.client.auth.signOut()
                        navController.navigate(WelcomeRoute) {
                            popUpTo(HomeRoute) { inclusive = true }
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Editar Perfil
        composable<EditProfileRoute> {
            EditProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Detalle del Servicio
        composable<ServiceDetailRoute> { backStackEntry ->
            val route: ServiceDetailRoute = backStackEntry.toRoute()
            ServiceDetailScreen(
                service = route.service,
                onBack = { navController.popBackStack() },
                onContratar = { navController.navigate(ContratacionRoute(route.service)) }
            )
        }

        // Contratación
        composable<ContratacionRoute> { backStackEntry ->
            val route: ContratacionRoute = backStackEntry.toRoute()
            PantallaContratacion(
                service = route.service,
                onContratarClick = { navController.navigate(ConfirmacionRoute(route.service)) }
            )
        }

        // Confirmación
        composable<ConfirmacionRoute> { backStackEntry ->
            val route: ConfirmacionRoute = backStackEntry.toRoute()
            PantallaReservaConfirmada(
                service = route.service,
                onContinuarClick = { 
                    navController.navigate(HomeRoute) {
                        popUpTo(HomeRoute) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    AppNavigation()
}
