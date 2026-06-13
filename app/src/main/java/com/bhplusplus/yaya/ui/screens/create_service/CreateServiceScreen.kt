package com.bhplusplus.yaya.ui.screens.create_service

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.R

/**
 * PANTALLA DE CREACIÓN DE SERVICIO
 * Sincronizada con el esquema SQL (provider_id, category_id, title, description, price, extra_cost, etc.)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateServiceScreen(
    onBack: () -> Unit,
    onServiceCreated: () -> Unit,
    viewModel: CreateServiceViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current

    // ESTADOS
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var estimatedTime by remember { mutableStateOf("") }
    var materialsIncluded by remember { mutableStateOf(false) }
    var extraCost by remember { mutableStateOf("0") }
    
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedCategoryName by remember { mutableStateOf("Selecciona una categoría") }
    var expanded by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_service_title), fontWeight = FontWeight.Bold) },
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
            // CATEGORÍA
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { if (!isLoading) expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    viewModel.categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategoryId = category.id
                                selectedCategoryName = category.name
                                expanded = false
                            }
                        )
                    }
                }
            }

            // TÍTULO
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text(stringResource(R.string.create_service_field_title)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading, shape = RoundedCornerShape(12.dp)
            )

            // DESCRIPCIÓN
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text(stringResource(R.string.create_service_field_description)) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                enabled = !isLoading, shape = RoundedCornerShape(12.dp)
            )

            // PRECIO BASE
            OutlinedTextField(
                value = price, onValueChange = { price = it },
                label = { Text(stringResource(R.string.create_service_field_price)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !isLoading, shape = RoundedCornerShape(12.dp)
            )

            // TIEMPO ESTIMADO
            OutlinedTextField(
                value = estimatedTime, onValueChange = { estimatedTime = it },
                label = { Text("Tiempo estimado (ej: 2 horas)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading, shape = RoundedCornerShape(12.dp)
            )

            // MATERIALES
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = materialsIncluded, onCheckedChange = { materialsIncluded = it }, enabled = !isLoading)
                Text("¿Incluye materiales e insumos?")
            }

            // COSTO EXTRA (Si no incluye materiales)
            if (!materialsIncluded) {
                OutlinedTextField(
                    value = extraCost, onValueChange = { extraCost = it },
                    label = { Text("Costo extra de materiales") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading, shape = RoundedCornerShape(12.dp)
                )
            }

            if (!errorMessage.isNullOrEmpty()) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    viewModel.createService(title, description, price, selectedCategoryId, estimatedTime, materialsIncluded, extraCost) {
                        if (it) onServiceCreated()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading && title.isNotBlank() && selectedCategoryId != null
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White)
                else Text(stringResource(R.string.create_service_button), fontWeight = FontWeight.Bold)
            }
        }
    }
}
