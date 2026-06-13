package com.example.myapplication.ui.screens.reset

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * PANTALLA DE RECUPERACIÓN DE CONTRASEÑA
 * Gestiona el envío de correos electrónicos para restablecer el acceso a la cuenta.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    onPasswordReset: () -> Unit, // Acción post-recuperación (opcional en este flujo)
    onBack: () -> Unit,          // Regresa a la pantalla anterior
    viewModel: ResetPasswordViewModel = viewModel()
) {
    // Estado para capturar el correo electrónico de recuperación
    var email by remember { mutableStateOf("") }
    
    // Observamos estados desde el ViewModel de Supabase
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()
    val isSuccess by viewModel.isSuccess.observeAsState(false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.reset_password_title),
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.reset_password_back_desc),
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            // Vista de ÉXITO: Aparece cuando Supabase confirma el envío del email
            if (isSuccess) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Green
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    stringResource(R.string.reset_password_success_title),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    stringResource(R.string.reset_password_success_message),
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { onBack() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(stringResource(R.string.reset_password_back_to_login), color = Color.White)
                }
            } else {
                // Vista de FORMULARIO: Pide el correo del usuario
                Text(
                    stringResource(R.string.reset_password_instructions),
                    modifier = Modifier.padding(bottom = 24.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.login_email_label)) },
                    placeholder = { Text(stringResource(R.string.reset_password_email_placeholder)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = errorMessage != null
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Mensaje de error (ej. correo no encontrado)
                if (!errorMessage.isNullOrEmpty()) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Botón para disparar la acción en Supabase
                Button(
                    onClick = {
                        viewModel.sendResetPasswordEmail(email)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(stringResource(R.string.reset_password_send_link), color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordScreenPreview() {
    ResetPasswordScreen(
        onPasswordReset = {},
        onBack = {}
    )
}
