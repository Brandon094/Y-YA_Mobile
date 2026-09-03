package com.bhplusplus.yaya.ui.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.bhplusplus.yaya.ui.components.atoms.YayaPrimaryButton
import com.bhplusplus.yaya.ui.components.atoms.YayaTextField
import com.bhplusplus.yaya.utils.ValidationUtils
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
    onViewTerms: () -> Unit,
    onViewPrivacy: () -> Unit,
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
    var acceptedTerms by remember { mutableStateOf(false) }
    var acceptedPrivacy by remember { mutableStateOf(false) }

    // LÓGICA DE VALIDACIÓN CENTRALIZADA (ValidationUtils - DRY)
    val isNameValid = ValidationUtils.isValidName(name)
    val isDocumentValid = ValidationUtils.isValidDocumentId(documentId)
    val isPhoneValid = ValidationUtils.isValidPhone(phone)
    val isEmailValid = ValidationUtils.isValidEmail(email)
    val isPasswordValid = ValidationUtils.isSecurePassword(password)
    val isBirthDateValid = ValidationUtils.isValidBirthDate(birthDate)
    val isAddressValid = ValidationUtils.isValidAddress(address)

    // El formulario es válido SOLO si todos los campos tienen contenido y cumplen sus reglas
    val isFormValid = isNameValid && isDocumentValid && isPhoneValid && 
                      isEmailValid && isPasswordValid && isBirthDateValid && 
                      isAddressValid && acceptedTerms && acceptedPrivacy

    // ESTADOS DEL SELECTOR DE FECHA (DatePicker - Restringido a hoy o fechas pasadas)
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

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
        YayaTextField(
            value = name,
            onValueChange = { name = it },
            label = stringResource(R.string.register_full_name),
            enabled = !isLoading,
            errorMessage = if (name.isNotEmpty() && !isNameValid) "Ingresa nombres válidos sin números ni símbolos" else null,
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CAMPO: NÚMERO DE IDENTIFICACIÓN (DNI/CÉDULA)
        YayaTextField(
            value = documentId,
            onValueChange = { documentId = it },
            label = stringResource(R.string.register_id_number),
            enabled = !isLoading,
            errorMessage = if (documentId.isNotEmpty() && !isDocumentValid) "El documento debe contener entre 6 y 12 dígitos" else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CAMPO: FECHA DE NACIMIENTO (Input no editable, lanza el selector)
        YayaTextField(
            value = birthDate,
            onValueChange = { },
            label = stringResource(R.string.register_birth_date),
            placeholder = stringResource(R.string.register_select_date_placeholder),
            errorMessage = if (birthDate.isNotEmpty() && !isBirthDateValid) "La fecha de nacimiento no puede ser futura" else null,
            modifier = Modifier
                .clickable { if (!isLoading) showDatePicker = true },
            enabled = false,
            readOnly = true,
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
        YayaTextField(
            value = phone,
            onValueChange = { phone = it },
            label = stringResource(R.string.register_phone),
            enabled = !isLoading,
            errorMessage = if (phone.isNotEmpty() && !isPhoneValid) "El teléfono debe contener exactamente 10 números" else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CAMPO: DIRECCIÓN DE RESIDENCIA
        YayaTextField(
            value = address,
            onValueChange = { address = it },
            label = stringResource(R.string.register_address),
            enabled = !isLoading,
            errorMessage = if (address.isNotEmpty() && !isAddressValid) "Ingresa una dirección válida (mínimo 5 caracteres)" else null,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CAMPO: CORREO ELECTRÓNICO (Con validación visual)
        YayaTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(R.string.register_email),
            enabled = !isLoading,
            errorMessage = if (email.isNotEmpty() && !isEmailValid) "Ingresa un correo válido (ej. usuario@dominio.com)" else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CAMPO: CONTRASEÑA (Con Toggle de visibilidad y acción de finalizar)
        YayaTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.register_password),
            enabled = !isLoading,
            errorMessage = if (password.isNotEmpty() && !isPasswordValid) "Mín. 8 caracteres, con mayúscula, minúscula y número/símbolo" else null,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { performRegister() }),
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

        Spacer(modifier = Modifier.height(24.dp))

        // ACEPTACIÓN LEGAL
        LegalConsentRow(
            text = stringResource(R.string.legal_accept_terms),
            checked = acceptedTerms,
            onCheckedChange = { acceptedTerms = it },
            onReadMore = onViewTerms,
            enabled = !isLoading
        )

        LegalConsentRow(
            text = stringResource(R.string.legal_accept_privacy),
            checked = acceptedPrivacy,
            onCheckedChange = { acceptedPrivacy = it },
            onReadMore = onViewPrivacy,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(32.dp))

        // MENSAJE DE ERROR: Aparece solo si Supabase devuelve un problema
        if (!errorMessage.isNullOrEmpty()) {
            Text(text = errorMessage!!, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
        }

        // BOTÓN PRINCIPAL DE REGISTRO
        YayaPrimaryButton(
            text = stringResource(R.string.register_button),
            onClick = { performRegister() },
            enabled = isFormValid,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ENLACE PARA REGRESAR AL LOGIN
        TextButton(onClick = onGoToLogin, enabled = !isLoading) {
            Text(stringResource(R.string.register_already_have_account), color = MaterialTheme.colorScheme.primary)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun LegalConsentRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onReadMore: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )
        Text(
            text = text,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f).clickable { onCheckedChange(!checked) }
        )
        TextButton(onClick = onReadMore, enabled = enabled) {
            Text(
                text = stringResource(R.string.legal_read_more),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onRegister = {}, onGoToLogin = {}, onViewTerms = {}, onViewPrivacy = {})
}
