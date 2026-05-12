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

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Logo en caja blanca
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

        // Nombre app
        Text(
            text = "yáya",
            fontSize = 28.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Frase principal
        Text(
            text = "Conecta. Confía. Contrata",
            fontSize = 20.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción
        Text(
            text = "Encuentra el servicio que buscas.\nOfrece tu talento.\nTodo en un solo lugar: YAYA.",
            fontSize = 14.sp,
            color = Color.Black,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Botón login
        Button(
            onClick = { onLoginClick() },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            ),
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
        ) {
            Text("Inicia sesión", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¿No tienes cuenta?",
            fontSize = 14.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(10.dp))
        // Botón regístro
        OutlinedButton(
            onClick = { onRegisterClick() },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
        ) {
            Text("Regístrate", color = Color.Black)
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