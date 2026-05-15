package com.example.myapplication.ui.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

/**
 * PANTALLA DE BIENVENIDA (WELCOME)
 * Es la primera pantalla que ve un usuario no autenticado.
 * Presenta la propuesta de valor de la aplicación YAYA.
 */
@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,    // Navega al flujo de inicio de sesión
    onRegisterClick: () -> Unit  // Navega al flujo de registro
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Contenedor del Logo de la App
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(Color.White, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nombre de la marca
        Text(
            text = "yáya",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Eslógan principal
        Text(
            text = "Conecta. Confía. Contrata",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Texto descriptivo de la misión de la app
        Text(
            text = "Encuentra el servicio que buscas.\nOfrece tu talento.\nTodo en un solo lugar: YAYA.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Botón principal: Acceso al Login
        Button(
            onClick = { onLoginClick() },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
        ) {
            Text("Inicia sesión", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Opción secundaria: Registro de nueva cuenta
        Text(
            text = "¿No tienes cuenta?",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(10.dp))
        
        OutlinedButton(
            onClick = { onRegisterClick() },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text("Regístrate", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(
        onLoginClick = {},
        onRegisterClick = {}
    )
}
