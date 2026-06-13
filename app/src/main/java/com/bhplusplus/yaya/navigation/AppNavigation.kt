package com.bhplusplus.yaya.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.ui.screens.welcome.WelcomeScreen
import com.bhplusplus.yaya.ui.screens.home.HomeScreen
import com.bhplusplus.yaya.ui.screens.login.LoginScreen
import com.bhplusplus.yaya.ui.screens.register.RegisterScreen
import com.bhplusplus.yaya.ui.screens.reset.ResetPasswordScreen
import com.bhplusplus.yaya.ui.screens.service_detail.ServiceDetailScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.data.SupabaseManager
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

import com.bhplusplus.yaya.ui.screens.profile.ProfileScreen
import com.bhplusplus.yaya.ui.screens.edit_profile.EditProfileScreen
import com.bhplusplus.yaya.ui.screens.contratacion.PantallaContratacion
import com.bhplusplus.yaya.ui.screens.confirmation.PantallaReservaConfirmada
import com.bhplusplus.yaya.ui.screens.create_service.CreateServiceScreen
import com.bhplusplus.yaya.ui.screens.my_orders.MyOrdersScreen

/**
 * DEFINICIÓN DE RUTAS (PANTALLAS) CON SEGURIDAD DE TIPOS
 * Siguiendo las directrices del manual: Se pasan únicamente los IDs (Strings).
 */
@Serializable object LoadingRoute
@Serializable object WelcomeRoute
@Serializable object LoginRoute
@Serializable object ResetRoute
@Serializable object RegisterRoute
@Serializable object HomeRoute
@Serializable object ProfileRoute
@Serializable object EditProfileRoute
@Serializable object CreateServiceRoute
@Serializable object MyOrdersRoute
@Serializable data class ServiceDetailRoute(val serviceId: String)
@Serializable data class ContratacionRoute(val serviceId: String)
@Serializable data class ConfirmacionRoute(val serviceId: String)

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
        kotlinx.coroutines.delay(2000) // 2 segundos de Splash para que luzca el logo
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
        // Pantalla de Carga Inicial (Splash Screen Profesional)
        composable<LoadingRoute> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFDF9)), // Color hueso elegante
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_yaya_full), // Logo Completo
                    contentDescription = "YÁYA Splash",
                    modifier = Modifier.size(250.dp)
                )
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
                onServiceClick = { service -> 
                    val id = service.id ?: ""
                    navController.navigate(ServiceDetailRoute(id)) 
                },
                onProfileClick = { navController.navigate(ProfileRoute) },
                onCreateServiceClick = { navController.navigate(CreateServiceRoute) },
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
                onMyOrders = { navController.navigate(MyOrdersRoute) },
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

        // Creación de Servicio
        composable<CreateServiceRoute> {
            CreateServiceScreen(
                onBack = { navController.popBackStack() },
                onServiceCreated = { navController.popBackStack() }
            )
        }

        // Mis Pedidos
        composable<MyOrdersRoute> {
            MyOrdersScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Detalle del Servicio
        composable<ServiceDetailRoute> { backStackEntry ->
            val route: ServiceDetailRoute = backStackEntry.toRoute()
            ServiceDetailScreen(
                serviceId = route.serviceId,
                onBack = { navController.popBackStack() },
                onContratar = { navController.navigate(ContratacionRoute(route.serviceId)) }
            )
        }

        // Contratación
        composable<ContratacionRoute> { backStackEntry ->
            val route: ContratacionRoute = backStackEntry.toRoute()
            PantallaContratacion(
                serviceId = route.serviceId,
                onBack = { navController.popBackStack() },
                onContratarClick = { navController.navigate(ConfirmacionRoute(route.serviceId)) }
            )
        }

        // Confirmación
        composable<ConfirmacionRoute> { backStackEntry ->
            val route: ConfirmacionRoute = backStackEntry.toRoute()
            PantallaReservaConfirmada(
                serviceId = route.serviceId,
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
