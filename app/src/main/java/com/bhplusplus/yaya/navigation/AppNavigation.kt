package com.bhplusplus.yaya.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
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
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import com.bhplusplus.yaya.data.models.UserProfile
import kotlinx.serialization.json.jsonPrimitive

import com.bhplusplus.yaya.ui.screens.profile.ProfileScreen
import com.bhplusplus.yaya.ui.screens.edit_profile.EditProfileScreen
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
 * DEFINICIÓN DE RUTAS (PANTALLAS) CON SEGURIDAD DE TIPOS
 */
@Serializable object LoadingRoute
@Serializable object WelcomeRoute
@Serializable object LoginRoute
@Serializable object ResetRoute
@Serializable object RegisterRoute
@Serializable object HomeRoute
@Serializable object AdminDashboardRoute // Hito 5: Ruta de Administración
@Serializable object ProfileRoute
@Serializable object EditProfileRoute
@Serializable data class CreateServiceRoute(val serviceId: String? = null)
@Serializable object MyOrdersRoute
@Serializable object IncomingRequestsRoute
@Serializable object MyServicesRoute
@Serializable object ChatListRoute // Nueva ruta para el listado de chats
@Serializable data class ServiceDetailRoute(val serviceId: String)
@Serializable data class ChatRoute(val receiverId: String, val receiverName: String) // Hito 2: Chat
@Serializable data class ContratacionRoute(val serviceId: String)
@Serializable data class ConfirmacionRoute(val serviceId: String, val requestId: String)

/**
 * NAVEGACIÓN PRINCIPAL
 * Utiliza Jetpack Compose Navigation con Type-Safety.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // Función centralizada para decidir a dónde ir según el rol
    val checkRoleAndNavigate: () -> Unit = {
        scope.launch {
            try {
                val session = SupabaseManager.client.auth.currentSessionOrNull()
                if (session != null) {
                    val userId = session.user?.id ?: throw Exception("User not found")
                    
                    // Consultamos el perfil con un pequeño reintento interno o directo a la tabla
                    val profile = SupabaseManager.client.postgrest["profiles"]
                        .select { filter { eq("id", userId) } }
                        .decodeSingle<UserProfile>()

                    Log.d("Navigation", "Rol detectado en DB: ${profile.role}")

                    if (profile.role.equals("admin", ignoreCase = true)) {
                        navController.navigate(AdminDashboardRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.navigate(HomeRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                } else {
                    navController.navigate(WelcomeRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            } catch (e: Exception) {
                Log.e("Navigation", "Error al validar rol: ${e.message}")
                // Fallback a metadata si la tabla profiles falla
                val session = SupabaseManager.client.auth.currentSessionOrNull()
                val role = session?.user?.userMetadata?.get("role")?.jsonPrimitive?.content ?: "client"
                
                Log.d("Navigation", "Rol detectado en Metadata (Fallback): $role")
                
                if (role.equals("admin", ignoreCase = true)) {
                    navController.navigate(AdminDashboardRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                } else {
                    navController.navigate(HomeRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000) // 2 segundos de Splash
        checkRoleAndNavigate()
    }

    NavHost(
        navController = navController,
        startDestination = LoadingRoute
    ) {
        // ... (resto del NavHost)
        // Pantalla de Carga Inicial
        composable<LoadingRoute> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFDF9)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_splash),
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
                    checkRoleAndNavigate()
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
                onMyOrders = { navController.navigate(MyOrdersRoute) },
                onIncomingRequestsClick = { navController.navigate(IncomingRequestsRoute) },
                onChatListClick = { navController.navigate(ChatListRoute) },
                onCreateServiceClick = { navController.navigate(CreateServiceRoute()) },
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

        // Dashboard de Administración (Hito 5)
        composable<AdminDashboardRoute> {
            AdminDashboardScreen(
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

        // Chat en Tiempo Real (Hito 2)
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
                onMyOrders = { navController.navigate(MyOrdersRoute) },
                onIncomingRequests = { navController.navigate(IncomingRequestsRoute) },
                onMyServices = { navController.navigate(MyServicesRoute) },
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

        // Solicitudes Recibidas (Prestador)
        composable<IncomingRequestsRoute> {
            IncomingRequestsScreen(
                onBack = { navController.popBackStack() },
                onChatClick = { id, name ->
                    navController.navigate(ChatRoute(id, name))
                }
            )
        }

        // Mis Servicios Publicados (Prestador)
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
    AppNavigation()
}
