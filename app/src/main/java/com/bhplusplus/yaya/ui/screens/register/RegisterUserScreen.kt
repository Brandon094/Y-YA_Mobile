package com.bhplusplus.yaya.ui.screens.register

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.clip
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
    onRegister: (String) -> Unit,
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
    var municipality by remember { mutableStateOf("La Plata") }
    var municipalityExpanded by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("client") } // 'client' o 'provider'
    var passwordVisible by remember { mutableStateOf(false) } // Controla si se ve la clave
    var acceptedTerms by remember { mutableStateOf(false) }
    var acceptedPrivacy by remember { mutableStateOf(false) }

    // LÓGICA DE VALIDACIÓN CENTRALIZADA (ValidationUtils - DRY & MVVM)
    val isNameValid = ValidationUtils.isValidName(name)
    val isDocumentValid = ValidationUtils.isValidDocumentId(documentId)
    val isPhoneValid = ValidationUtils.isValidPhone(phone)
    val isEmailValid = ValidationUtils.isValidEmail(email)
    val isPasswordValid = ValidationUtils.isSecurePassword(password)
    val isBirthDateValid = ValidationUtils.isValidBirthDate(birthDate)
    val isAddressValid = ValidationUtils.isValidAddress(address)

    val isStep1Valid = remember(name, documentId, birthDate) {
        viewModel.isStep1Valid(name, documentId, birthDate)
    }
    val isStep2Valid = remember(phone, address, municipality) {
        viewModel.isStep2Valid(phone, address, municipality)
    }
    val isStep3Valid = remember(email, password, acceptedTerms, acceptedPrivacy) {
        viewModel.isStep3Valid(email, password, acceptedTerms, acceptedPrivacy)
    }

    val isFormValid = isStep1Valid && isStep2Valid && isStep3Valid

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
            viewModel.register(name, email, password, phone, address, documentId, birthDate, selectedRole, municipality) { success, registeredRole ->
                if (success) onRegister(registeredRole) // Pasa el rol registrado
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

    // DISEÑO DE LA PANTALLA (WIZARD DE 3 PASOS)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // TÍTULO PRINCIPAL
        Text(
            text = stringResource(R.string.register_title),
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold
        )

        // INDICADOR DE PROGRESO DEL WIZARD (3 PASOS)
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (viewModel.currentStep) {
                        1 -> "PASO 1 DE 3: DATOS PERSONALES Y ROL"
                        2 -> "PASO 2 DE 3: CONTACTO Y UBICACIÓN"
                        else -> "PASO 3 DE 3: SEGURIDAD Y LEGAL"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${(viewModel.currentStep * 33).coerceAtMost(100)}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (viewModel.currentStep / 3f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        }

        if (viewModel.currentStep == 1) {
            // ==================== PASO 1: DATOS PERSONALES Y ROL ====================
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

            // CAMPO: NÚMERO DE IDENTIFICACIÓN
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

            // CAMPO: FECHA DE NACIMIENTO
            YayaTextField(
                value = birthDate,
                onValueChange = { },
                label = stringResource(R.string.register_birth_date),
                placeholder = stringResource(R.string.register_select_date_placeholder),
                errorMessage = if (birthDate.isNotEmpty() && !isBirthDateValid) "La fecha de nacimiento no puede ser futura" else null,
                modifier = Modifier.clickable { if (!isLoading) showDatePicker = true },
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

            // SELECTOR DE ROL
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.register_how_to_use),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (selectedRole == "client") "Rol: Cliente" else "Rol: Prestador",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        onClick = { selectedRole = "client" },
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedRole == "client") MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = if (selectedRole == "client") BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedRole == "client",
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.register_want_services),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    Surface(
                        onClick = { selectedRole = "provider" },
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedRole == "provider") MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = if (selectedRole == "provider") BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedRole == "provider",
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.register_offer_talents),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.goToStep(2) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = isStep1Valid && !isLoading
            ) {
                Text("Siguiente: Contacto ➔", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

        } else if (viewModel.currentStep == 2) {
            // ==================== PASO 2: CONTACTO Y UBICACIÓN ====================
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

            // MUNICIPIO / CIUDAD
            Column(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = municipalityExpanded,
                    onExpandedChange = { if (!isLoading) municipalityExpanded = !municipalityExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = municipality,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Municipio / Ciudad de Residencia") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = municipalityExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = municipalityExpanded,
                        onDismissRequest = { municipalityExpanded = false }
                    ) {
                        ValidationUtils.HUILA_MUNICIPALITIES.forEach { muni ->
                            DropdownMenuItem(
                                text = { Text(muni) },
                                onClick = {
                                    municipality = muni
                                    municipalityExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.goToStep(1) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading
                ) {
                    Text("⬅️ Volver", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.goToStep(3) },
                    modifier = Modifier.weight(1.5f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = isStep2Valid && !isLoading
                ) {
                    Text("Siguiente: Seguridad ➔", fontWeight = FontWeight.Bold)
                }
            }

        } else {
            // ==================== PASO 3: SEGURIDAD Y ACEPTACIÓN LEGAL ====================
            // CAMPO: CORREO ELECTRÓNICO
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

            // CAMPO: CONTRASEÑA
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

            if (!errorMessage.isNullOrEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.goToStep(2) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading
                ) {
                    Text("⬅️ Volver", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { performRegister() },
                    modifier = Modifier.weight(1.5f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = isFormValid && !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White)
                    else Text(stringResource(R.string.register_button), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
            color = MaterialTheme.colorScheme.onBackground,
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
