package com.bhplusplus.yaya.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.bhplusplus.yaya.ui.screens.welcome.WelcomeScreen
import com.bhplusplus.yaya.ui.screens.home.HomeScreen
import com.bhplusplus.yaya.ui.screens.login.LoginScreen
import com.bhplusplus.yaya.ui.screens.register.RegisterScreen
import com.bhplusplus.yaya.ui.screens.reset.ResetPasswordScreen
import com.bhplusplus.yaya.ui.screens.service_detail.ServiceDetailScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.bhplusplus.yaya.data.SupabaseManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import com.bhplusplus.yaya.data.models.UserProfile
import kotlinx.serialization.json.jsonPrimitive

import com.bhplusplus.yaya.ui.screens.profile.ProfileScreen
import com.bhplusplus.yaya.ui.screens.edit_profile.EditProfileScreen
import com.bhplusplus.yaya.ui.screens.profile.AvailabilityScreen
import com.bhplusplus.yaya.ui.screens.contratacion.PantallaContratacion
import com.bhplusplus.yaya.ui.screens.confirmation.PantallaReservaConfirmada
import com.bhplusplus.yaya.ui.screens.create_service.CreateServiceScreen
import com.bhplusplus.yaya.ui.screens.my_orders.MyOrdersScreen
import com.bhplusplus.yaya.ui.screens.incoming_requests.IncomingRequestsScreen
import com.bhplusplus.yaya.ui.screens.my_services.MyServicesScreen
import com.bhplusplus.yaya.ui.screens.admin.AdminDashboardScreen
import com.bhplusplus.yaya.ui.screens.chat.ChatScreen
import com.bhplusplus.yaya.ui.screens.chat.ChatListScreen

import android.util.Log

/**
 * DEFINICIÓN DE RUTAS (PANTALLA) CON SEGURIDAD DE TIPOS
 */
@Serializable object WelcomeRoute
@Serializable object LoginRoute
@Serializable object ResetRoute
@Serializable object RegisterRoute
@Serializable object HomeRoute
@Serializable object AdminDashboardRoute 
@Serializable object ProfileRoute
@Serializable object EditProfileRoute
@Serializable object AvailabilityRoute
@Serializable data class CreateServiceRoute(val serviceId: String? = null)
@Serializable object MyOrdersRoute
@Serializable object IncomingRequestsRoute
@Serializable object MyServicesRoute
@Serializable object ChatListRoute 
@Serializable data class ServiceDetailRoute(val serviceId: String)
@Serializable data class ChatRoute(val receiverId: String, val receiverName: String) 
@Serializable data class ContratacionRoute(val serviceId: String)
@Serializable data class ConfirmacionRoute(val serviceId: String, val requestId: String)

/**
 * NAVEGACIÓN PRINCIPAL
 * Utiliza Jetpack Compose Navigation con Type-Safety.
 */
@Composable
fun AppNavigation(startRoute: Any) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // Función para manejar redirecciones tras login/registro
    val navigateByRole: () -> Unit = {
        navController.navigate(HomeRoute) { popUpTo(0) { inclusive = true } }
    }

    NavHost(
        navController = navController,
        startDestination = startRoute
    ) {
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
                onLoginSuccess = { navigateByRole() },
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
                onMyOrders = { navController.navigate(MyOrdersRoute) },
                onIncomingRequestsClick = { navController.navigate(IncomingRequestsRoute) },
                onChatListClick = { navController.navigate(ChatListRoute) },
                onCreateServiceClick = { navController.navigate(CreateServiceRoute()) },
                onLogout = {
                    scope.launch {
                        SupabaseManager.client.auth.signOut()
                        navController.navigate(WelcomeRoute) {
                            popUpTo(HomeRoute) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Dashboard de Administración
        composable<AdminDashboardRoute> {
            AdminDashboardScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    scope.launch {
                        SupabaseManager.client.auth.signOut()
                        navController.navigate(WelcomeRoute) {
                            popUpTo(AdminDashboardRoute) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Chat en Tiempo Real
        composable<ChatRoute> { backStackEntry ->
            val route: ChatRoute = backStackEntry.toRoute()
            ChatScreen(
                receiverId = route.receiverId,
                receiverName = route.receiverName,
                onBack = { navController.popBackStack() }
            )
        }

        // Listado de Chats
        composable<ChatListRoute> {
            ChatListScreen(
                onBack = { navController.popBackStack() },
                onChatClick = { id, name ->
                    navController.navigate(ChatRoute(id, name))
                }
            )
        }

        // Perfil
        composable<ProfileRoute> {
            ProfileScreen(
                onEditProfile = { navController.navigate(EditProfileRoute) },
                onAvailability = { navController.navigate(AvailabilityRoute) },
                onMyOrders = { navController.navigate(MyOrdersRoute) },
                onIncomingRequests = { navController.navigate(IncomingRequestsRoute) },
                onMyServices = { navController.navigate(MyServicesRoute) },
                onAdminDashboard = { navController.navigate(AdminDashboardRoute) },
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

        // Disponibilidad
        composable<AvailabilityRoute> {
            AvailabilityScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Creación de Servicio
        composable<CreateServiceRoute> { backStackEntry ->
            val route: CreateServiceRoute = backStackEntry.toRoute()
            CreateServiceScreen(
                serviceId = route.serviceId,
                onBack = { navController.popBackStack() },
                onServiceCreated = { navController.popBackStack() }
            )
        }

        // Mis Pedidos
        composable<MyOrdersRoute> {
            MyOrdersScreen(
                onBack = { navController.popBackStack() },
                onChatClick = { id, name ->
                    navController.navigate(ChatRoute(id, name))
                }
            )
        }

        // Solicitudes Recibidas
        composable<IncomingRequestsRoute> {
            IncomingRequestsScreen(
                onBack = { navController.popBackStack() },
                onChatClick = { id, name ->
                    navController.navigate(ChatRoute(id, name))
                }
            )
        }

        // Mis Servicios Publicados
        composable<MyServicesRoute> {
            MyServicesScreen(
                onBack = { navController.popBackStack() },
                onEditService = { serviceId ->
                    navController.navigate(CreateServiceRoute(serviceId))
                }
            )
        }

        // Detalle del Servicio
        composable<ServiceDetailRoute> { backStackEntry ->
            val route: ServiceDetailRoute = backStackEntry.toRoute()
            ServiceDetailScreen(
                serviceId = route.serviceId,
                onBack = { navController.popBackStack() },
                onContratar = { navController.navigate(ContratacionRoute(route.serviceId)) },
                onChatClick = { id, name ->
                    navController.navigate(ChatRoute(id, name))
                }
            )
        }

        // Contratación
        composable<ContratacionRoute> { backStackEntry ->
            val route: ContratacionRoute = backStackEntry.toRoute()
            PantallaContratacion(
                serviceId = route.serviceId,
                onBack = { navController.popBackStack() },
                onContratarClick = { requestId -> 
                    navController.navigate(ConfirmacionRoute(route.serviceId, requestId)) 
                }
            )
        }

        // Confirmación
        composable<ConfirmacionRoute> { backStackEntry ->
            val route: ConfirmacionRoute = backStackEntry.toRoute()
            PantallaReservaConfirmada(
                serviceId = route.serviceId,
                requestId = route.requestId,
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
    AppNavigation(WelcomeRoute)
}
