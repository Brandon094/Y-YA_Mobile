package com.example.myapplication.ui.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R

/**
 * PANTALLA DE REGISTRO
 * Permite a nuevos usuarios crear una cuenta en la plataforma.
 */
@Composable
fun RegisterScreen(
    onRegister: () -> Unit,    // Regresa al login o avanza tras registrarse
    onGoToLogin: () -> Unit,   // Opción para ir al login si ya tiene cuenta
    viewModel: RegisterUserViewModel = viewModel()
) {
    // Estados para capturar la entrada del usuario
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Estados observados desde el ViewModel de Registro
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título de la pantalla
        Text(
            text = "Únete",
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Campo: Nombre de usuario
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            placeholder = { Text("Escribe tu nombre de usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            trailingIcon = {
                Icon(Icons.Default.Person, contentDescription = null)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Correo electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            placeholder = { Text("Escribe tu correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            trailingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Contraseña con transformación visual
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Mostrar mensaje de error si la validación en Supabase falla
        if (!errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Botón para procesar el registro
        Button(
            onClick = {
                viewModel.register(name, email, password) { success ->
                    if (success) {
                        onRegister() 
                    }
                }
            },
            modifier = Modifier
                .width(180.dp)
                .height(50.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Unirse", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Autenticarse con", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))

        Spacer(modifier = Modifier.height(16.dp))

        // Icono de Google (placeholder)
        Image(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Google",
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("¿Ya tienes cuenta?", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))

        // Opción para regresar al login
        TextButton(onClick = { onGoToLogin() }, enabled = !isLoading) {
            Text(
                "Iniciar sesión",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onRegister = {},
        onGoToLogin = {}
    )
}
