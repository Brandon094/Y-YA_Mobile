package com.bhplusplus.yaya.ui.screens.create_service

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.utils.ImageUtils

/**
 * PANTALLA DE CREACIÓN Y EDICIÓN DE SERVICIO
 * Sincronizada con el esquema SQL de Supabase.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateServiceScreen(
    serviceId: String? = null,
    onBack: () -> Unit,
    onServiceCreated: () -> Unit,
    viewModel: CreateServiceViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            val byteArrayList = uris.mapNotNull { ImageUtils.uriToByteArray(context, it) }
            viewModel.selectedImages = viewModel.selectedImages + byteArrayList
        }
    )

    // ESTADOS
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var estimatedTime by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("18:00") }
    var materialsIncluded by remember { mutableStateOf(false) }
    var extraCost by remember { mutableStateOf("0") }
    
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedCategoryName by remember { mutableStateOf("Selecciona una categoría") }
    var expanded by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    // Diálogo de éxito y moderación
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* No permitir cerrar sin botón */ },
            title = { Text("¡Servicio Publicado!", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Tu talento ha sido registrado con éxito. " +
                    "Para garantizar la calidad de YÁYA, un administrador revisará tu servicio pronto. " +
                    "Recibirás una notificación cuando sea aprobado y visible para todos.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onServiceCreated()
                    }
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    // Si recibimos un ID, cargamos los datos del servicio para editar
    LaunchedEffect(serviceId) {
        if (serviceId != null) {
            viewModel.loadServiceData(serviceId) { service ->
                title = service.title
                description = service.description
                price = service.price.toString()
                estimatedTime = service.estimated_time ?: ""
                selectedDays = service.working_days.toSet()
                startTime = service.start_time.substring(0, 5)
                endTime = service.end_time.substring(0, 5)
                materialsIncluded = service.materials_included
                extraCost = service.extra_cost.toString()
                selectedCategoryId = service.category_id
                
                // Buscamos el nombre de la categoría
                val cat = viewModel.categories.find { it.id == service.category_id }
                if (cat != null) selectedCategoryName = cat.name
            }
        }
    }
    
    // Actualizar el nombre de la categoría una vez carguen las categorías si estamos en modo edición
    LaunchedEffect(viewModel.categories) {
        if (selectedCategoryId != null) {
            val cat = viewModel.categories.find { it.id == selectedCategoryId }
            if (cat != null) selectedCategoryName = cat.name
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (serviceId == null) stringResource(R.string.create_service_title) else "Editar Servicio", 
                        fontWeight = FontWeight.Bold 
                    ) 
                },
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
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
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

            // DISPONIBILIDAD (Day Picker UX)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Días de prestación del servicio:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val days = listOf("L", "M", "M", "J", "V", "S", "D")
                    days.forEachIndexed { index, name ->
                        val dayNumber = index + 1
                        val isSelected = selectedDays.contains(dayNumber)
                        
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                                .clickable(enabled = !isLoading) {
                                    selectedDays = if (isSelected) selectedDays - dayNumber else selectedDays + dayNumber
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                color = if (isSelected) Color.White else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // SELECTORES DE HORA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Hora Inicio") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("08:00") },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("Hora Fin") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("18:00") },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp)
                )
            }

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

            // PORTAFOLIO DE IMÁGENES
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Portafolio del servicio (Imágenes):",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón para añadir imágenes
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }

                    // Lista de imágenes seleccionadas (Vista previa)
                    // Para simplificar, mostramos las primeras 3
                    viewModel.selectedImages.take(3).forEach { bytes ->
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = bytes,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    
                    if (viewModel.selectedImages.size > 3) {
                        Box(
                            modifier = Modifier.size(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+${viewModel.selectedImages.size - 3}", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                if (viewModel.selectedImages.isNotEmpty()) {
                    TextButton(onClick = { viewModel.selectedImages = emptyList() }) {
                        Text("Limpiar imágenes", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            }

            if (!errorMessage.isNullOrEmpty()) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    viewModel.saveService(serviceId, title, description, price, selectedCategoryId, estimatedTime, selectedDays.toList().sorted(), startTime, endTime, materialsIncluded, extraCost) { success ->
                        if (success) {
                            if (serviceId == null) {
                                // Si es nuevo, mostramos el aviso de moderación
                                showSuccessDialog = true
                            } else {
                                // Si es edición, navegamos directamente
                                onServiceCreated()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading && title.isNotBlank() && selectedCategoryId != null
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White)
                else Text(
                    text = if (serviceId == null) stringResource(R.string.create_service_button) else "Guardar Cambios", 
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
