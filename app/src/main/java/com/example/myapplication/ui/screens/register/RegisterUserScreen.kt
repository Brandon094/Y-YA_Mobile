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

@Composable
fun RegisterScreen(
    onRegister: () -> Unit,
    onGoToLogin: () -> Unit,
    viewModel: RegisterUserViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Observamos el estado del ViewModel
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
        // Título
        Text(
            text = "Únete",
            fontSize = 32.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Nombre (puedes guardarlo en Supabase luego en una tabla de perfiles)
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

        // Email
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

        // Password
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

        // Mostrar error si existe
        if (!errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Botón Unirse conectado al ViewModel
        Button(
            onClick = {
                viewModel.register(email, password) { success ->
                    if (success) {
                        // Si el registro es exitoso, navegamos o avisamos
                        onRegister() 
                    }
                }
            },
            modifier = Modifier
                .width(180.dp)
                .height(50.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
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

        Text("Autenticarse con", color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        // Google icon
        Image(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Google",
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("¿Ya tienes cuenta?", color = Color.Black)

        TextButton(onClick = { onGoToLogin() }, enabled = !isLoading) {
            Text(
                "Iniciar sesión",
                color = Color.Red // Cambié a rojo para que se vea, antes era blanco sobre fondo claro
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
