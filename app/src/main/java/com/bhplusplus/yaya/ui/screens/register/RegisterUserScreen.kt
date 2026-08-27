package com.bhplusplus.yaya.ui.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import android.util.Patterns

/**
 * PANTALLA DE REGISTRO INTEGRAL
 * Captura toda la información necesaria para el perfil del usuario según el modelo YYA.
 * Incluye validaciones en tiempo real y una experiencia de usuario optimizada.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegister: () -> Unit,
    onGoToLogin: () -> Unit,
    viewModel: RegisterUserViewModel = viewModel()
) {
    // Administrador de foco para saltar entre campos automáticamente al usar el teclado
    val focusManager = LocalFocusManager.current

    // ESTADOS LOCALES: Almacenan lo que el usuario escribe en cada campo
    var name by remember { mutableStateOf("") }
    var documentId by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("client") } // 'client' o 'provider'
    var passwordVisible by remember { mutableStateOf(false) } // Controla si se ve la clave

    // LÓGICA DE VALIDACIÓN: Se actualiza cada vez que el usuario escribe algo
    // Valida que el email tenga un formato estándar (nombre@dominio.com)
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    // Valida que la contraseña sea lo suficientemente larga y segura (mínimo 6 chars)
    val isPasswordValid = password.length >= 6
    // El formulario es válido SOLO si todos los campos tienen contenido y cumplen sus reglas
    val isFormValid = name.isNotBlank() && documentId.isNotBlank() && birthDate.isNotBlank() && 
                      isEmailValid && phone.isNotBlank() && address.isNotBlank() && isPasswordValid

    // ESTADOS DEL SELECTOR DE FECHA (DatePicker)
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // OBSERVACIÓN DEL VIEWMODEL: Escuchamos si Supabase está cargando o devolvió un error
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    // FUNCIÓN DE REGISTRO: Solo se ejecuta si el formulario es válido
    val performRegister = {
        if (isFormValid) {
            viewModel.register(name, email, password, phone, address, documentId, birthDate, selectedRole) { success ->
                if (success) onRegister() // Si fue exitoso, navegamos fuera de la pantalla
            }
        }
    }

    // DIÁLOGO DEL SELECTOR DE FECHA: Aparece al tocar el campo de nacimiento
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                    if (selectedDate != null) {
                        // Convertimos los milisegundos a una fecha legible en formato ISO
                        val date = Instant.ofEpochMilli(selectedDate)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        // El formato YYYY-MM-DD es el estándar que pide Supabase
                        birthDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.register_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.register_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // DISEÑO DE LA PANTALLA
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding() // RESPETA BOTONES DE NAVEGACIÓN
            .verticalScroll(rememberScrollState()) // Soporta scroll si la pantalla es pequeña
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // TÍTULO PRINCIPAL
        Text(
            text = stringResource(R.string.register_title),
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CAMPO: NOMBRE COMPLETO
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.register_full_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading, // Se deshabilita durante la carga
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), // Muestra tecla 'Siguiente'
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CAMPO: NÚMERO DE IDENTIFICACIÓN (DNI/CÉDULA)
        OutlinedTextField(
            value = documentId,
            onValueChange = { documentId = it },
            label = { Text(stringResource(R.string.register_id_number)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CAMPO: FECHA DE NACIMIENTO (Input no editable, lanza el selector)
        OutlinedTextField(
            value = birthDate,
            onValueChange = { }, // Bloqueado manual para evitar errores de formato
            label = { Text(stringResource(R.string.register_birth_date)) },
            placeholder = { Text(stringResource(R.string.register_select_date_placeholder)) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if (!isLoading) showDatePicker = true }, // Lanza el DatePicker
            enabled = false, // Evita que aparezca el teclado estándar
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            leadingIcon = { 
                IconButton(onClick = { if (!isLoading) showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CAMPO: TELÉFONO DE CONTACTO
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(stringResource(R.string.register_phone)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CAMPO: DIRECCIÓN DE RESIDENCIA
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text(stringResource(R.string.register_address)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CAMPO: CORREO ELECTRÓNICO (Con validación visual)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.register_email)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            isError = email.isNotEmpty() && !isEmailValid, // Marca error si el formato no es válido
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CAMPO: CONTRASEÑA (Con Toggle de visibilidad y acción de finalizar)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.register_password)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            isError = password.isNotEmpty() && !isPasswordValid, // Marca error si es muy corta
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { performRegister() }), // Dispara el botón al dar 'Enter'
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // SELECTOR DE ROL: Define cómo el usuario participará en el sistema
        Text(
            text = stringResource(R.string.register_how_to_use),
            modifier = Modifier.align(Alignment.Start),
            fontWeight = FontWeight.Medium
        )
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selectedRole == "client", onClick = { selectedRole = "client" }, enabled = !isLoading)
            Text(stringResource(R.string.register_want_services), modifier = Modifier.padding(end = 16.dp))
            RadioButton(selected = selectedRole == "provider", onClick = { selectedRole = "provider" }, enabled = !isLoading)
            Text(stringResource(R.string.register_offer_talents))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // MENSAJE DE ERROR: Aparece solo si Supabase devuelve un problema
        if (!errorMessage.isNullOrEmpty()) {
            Text(text = errorMessage!!, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
        }

        // BOTÓN PRINCIPAL DE REGISTRO
        Button(
            onClick = { performRegister() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = !isLoading && isFormValid // Solo se activa si el formulario está perfecto
        ) {
            if (isLoading) {
                // Indicador de progreso mientras se comunica con Supabase
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text(stringResource(R.string.register_button), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ENLACE PARA REGRESAR AL LOGIN
        TextButton(onClick = onGoToLogin, enabled = !isLoading) {
            Text(stringResource(R.string.register_already_have_account), color = MaterialTheme.colorScheme.primary)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onRegister = {}, onGoToLogin = {})
}
