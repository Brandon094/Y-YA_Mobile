package com.bhplusplus.yaya.ui.screens.edit_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.ui.components.atoms.YayaPrimaryButton
import com.bhplusplus.yaya.ui.components.atoms.YayaTextField
import com.bhplusplus.yaya.ui.components.molecules.AvatarSelector
import com.bhplusplus.yaya.utils.ImageUtils
import com.bhplusplus.yaya.utils.ValidationUtils
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * PANTALLA DE EDICIÓN DE PERFIL
 * Sincronizada con el esquema SQL (Nombre, Teléfono, ID, Fecha Nacimiento, Dirección).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = viewModel()
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var municipalityExpanded by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val bytes = ImageUtils.uriToByteArray(context, it)
                if (bytes != null) {
                    viewModel.uploadAvatar(bytes)
                }
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        viewModel.birthDate = date.toString()
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.register_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.register_cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_profile_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Molécula: Selector de Avatar
            AvatarSelector(
                imageUrl = viewModel.avatarUrl,
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                isLoading = viewModel.isLoading
            )

            Spacer(Modifier.height(8.dp))

            // Átomo: Nombre
            YayaTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = stringResource(R.string.edit_profile_full_name_label),
                enabled = !viewModel.isLoading,
                errorMessage = if (viewModel.name.isNotEmpty() && !ValidationUtils.isValidName(viewModel.name)) "Ingresa nombres válidos sin números ni símbolos" else null,
                leadingIcon = { Icon(Icons.Default.Person, null) }
            )

            // Átomo: Documento ID
            YayaTextField(
                value = viewModel.documentId,
                onValueChange = { viewModel.documentId = it },
                label = stringResource(R.string.register_id_number),
                enabled = !viewModel.isLoading,
                errorMessage = if (viewModel.documentId.isNotEmpty() && !ValidationUtils.isValidDocumentId(viewModel.documentId)) "El documento debe contener de 6 a 12 dígitos" else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.Badge, null) }
            )

            // Átomo: Teléfono
            YayaTextField(
                value = viewModel.phone,
                onValueChange = { viewModel.phone = it },
                label = stringResource(R.string.edit_profile_phone_label),
                enabled = !viewModel.isLoading,
                errorMessage = if (viewModel.phone.isNotEmpty() && !ValidationUtils.isValidPhone(viewModel.phone)) "El teléfono debe contener exactamente 10 números" else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = { Icon(Icons.Default.Phone, null) }
            )

            // Átomo: Dirección
            YayaTextField(
                value = viewModel.address,
                onValueChange = { viewModel.address = it },
                label = stringResource(R.string.register_address),
                enabled = !viewModel.isLoading,
                leadingIcon = { Icon(Icons.Default.Home, null) }
            )

            // Átomo: Municipio (Dropdown)
            Column(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = municipalityExpanded,
                    onExpandedChange = { if (!viewModel.isLoading) municipalityExpanded = !municipalityExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = viewModel.municipality,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Municipio / Ciudad") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = municipalityExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        enabled = !viewModel.isLoading,
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
                                    viewModel.municipality = muni
                                    municipalityExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Átomo: Fecha Nacimiento (Input no editable)
            YayaTextField(
                value = viewModel.birthDate,
                onValueChange = {},
                label = stringResource(R.string.register_birth_date),
                errorMessage = if (viewModel.birthDate.isNotEmpty() && !ValidationUtils.isValidBirthDate(viewModel.birthDate)) "La fecha de nacimiento no puede ser futura" else null,
                modifier = Modifier.clickable { if(!viewModel.isLoading) showDatePicker = true },
                enabled = false,
                readOnly = true,
                leadingIcon = { Icon(Icons.Default.DateRange, null) },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(Modifier.height(8.dp))

            if (viewModel.errorMessage != null) {
                Text(text = viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            // Átomo: Botón Primario
            YayaPrimaryButton(
                text = stringResource(R.string.edit_profile_save_button),
                onClick = { viewModel.updateProfile { onBack() } },
                isLoading = viewModel.isLoading
            )

            TextButton(onClick = onBack, enabled = !viewModel.isLoading) {
                Text(stringResource(R.string.edit_profile_cancel_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfilePreview() {
    EditProfileScreen(onBack = {})
}
