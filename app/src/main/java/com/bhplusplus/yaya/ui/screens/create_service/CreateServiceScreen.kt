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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.ui.components.organisms.TutorialStep
import com.bhplusplus.yaya.ui.components.organisms.YayaTutorialOverlay
import com.bhplusplus.yaya.utils.ImageUtils
import com.bhplusplus.yaya.utils.TutorialManager
import com.bhplusplus.yaya.utils.ValidationUtils

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

    // ESTADOS DE FORMULARIO (WIZARD DE 2 PASOS CONECTADO AL VIEWMODEL)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var timeUnitExpanded by remember { mutableStateOf(false) }

    var selectedDays by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("18:00") }
    var materialsIncluded by remember { mutableStateOf(false) }
    var extraCost by remember { mutableStateOf("0") }
    var municipality by remember { mutableStateOf("La Plata") }
    var municipalityExpanded by remember { mutableStateOf(false) }
    
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedCategoryName by remember { mutableStateOf("Selecciona una categoría") }
    var expanded by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var municipalityBounds by remember { mutableStateOf<Rect?>(null) }
    var scheduleBounds by remember { mutableStateOf<Rect?>(null) }
    var daysPickerBounds by remember { mutableStateOf<Rect?>(null) }

    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    val isStep1Valid = remember(title, description, selectedCategoryId, municipality) {
        viewModel.isStep1Valid(title, description, selectedCategoryId, municipality)
    }

    val currentValidationError = remember(
        title, price, selectedCategoryId, selectedDays, startTime, endTime, municipality,
        viewModel.masterWorkingDays, viewModel.existingServicesList
    ) {
        if (municipality.isBlank()) {
            "Selecciona un municipio de atención"
        } else {
            viewModel.validateServiceData(
                title = title,
                price = price,
                categoryId = selectedCategoryId,
                workingDays = selectedDays.toList().sorted(),
                startTime = startTime,
                endTime = endTime,
                serviceId = serviceId
            )
        }
    }

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
        viewModel.loadProviderAvailabilityAndServices(serviceId)
        if (serviceId != null) {
            viewModel.loadServiceData(serviceId) { service ->
                title = service.title
                description = service.description
                price = service.price.toString()
                selectedDays = service.working_days.toSet()
                startTime = service.start_time.substring(0, 5)
                endTime = service.end_time.substring(0, 5)
                materialsIncluded = service.materials_included
                extraCost = service.extra_cost.toString()
                municipality = service.municipality ?: "La Plata"
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
                .navigationBarsPadding() // RESPETA BOTONES DE NAVEGACIÓN
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // INDICADOR DE PROGRESO DEL WIZARD (2 PASOS)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (viewModel.currentStep == 1) "PASO 1 DE 2: INFORMACIÓN BÁSICA" else "PASO 2 DE 2: PRECIO Y HORARIOS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${(viewModel.currentStep * 50)}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { if (viewModel.currentStep == 1) 0.5f else 1.0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            }

            if (viewModel.currentStep == 1) {
                // ==================== PASO 1: INFORMACIÓN BÁSICA Y PORTAFOLIO ====================
                // CATEGORÍA
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "CATEGORÍA DEL TALENTO",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { if (!isLoading) expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCategoryName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
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
                }

                // MUNICIPIO DE ATENCIÓN
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coords ->
                            municipalityBounds = coords.boundsInWindow()
                        }
                ) {
                    Text(
                        text = "MUNICIPIO DE ATENCIÓN",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = municipalityExpanded,
                        onExpandedChange = { if (!isLoading) municipalityExpanded = !municipalityExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = municipality,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = municipalityExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
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

                // TÍTULO
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "TÍTULO DEL SERVICIO",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title, onValueChange = { title = it },
                        placeholder = { Text("Ej: Limpieza de muebles") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading, shape = RoundedCornerShape(12.dp)
                    )
                }

                // DESCRIPCIÓN
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "DESCRIPCIÓN DETALLADA",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description, onValueChange = { description = it },
                        placeholder = { Text("Describe qué incluye tu servicio...") },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                        enabled = !isLoading, shape = RoundedCornerShape(12.dp)
                    )
                }

                // PORTAFOLIO DE IMÁGENES
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "PORTAFOLIO (TRABAJOS REALIZADOS)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            enabled = !isLoading,
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            modifier = Modifier.size(90.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text("Añadir", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }

                        viewModel.selectedImages.take(2).forEach { bytes ->
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            ) {
                                AsyncImage(
                                    model = bytes,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        
                        if (viewModel.selectedImages.size > 2) {
                            Box(
                                modifier = Modifier.size(60.dp).align(Alignment.CenterVertically),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+${viewModel.selectedImages.size - 2}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    
                    if (viewModel.selectedImages.isNotEmpty()) {
                        TextButton(onClick = { viewModel.selectedImages = emptyList() }, modifier = Modifier.align(Alignment.End)) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Limpiar galería", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                    Text("Siguiente: Precio y Horarios ➔", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

            } else {
                // ==================== PASO 2: PRECIO, DURACIÓN Y DISPONIBILIDAD ====================
                // PRECIO Y DURACIÓN (Estructurada Compuesta: Número + Unidad)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PRECIO BASE",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = price, onValueChange = { price = it },
                            prefix = { Text("$") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            enabled = !isLoading, shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DURACIÓN ESTIMADA",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = viewModel.estimatedTimeNumber,
                                onValueChange = { viewModel.estimatedTimeNumber = it.filter { c -> c.isDigit() } },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = !isLoading,
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenuBox(
                                expanded = timeUnitExpanded,
                                onExpandedChange = { if (!isLoading) timeUnitExpanded = !timeUnitExpanded },
                                modifier = Modifier.weight(1.2f)
                            ) {
                                OutlinedTextField(
                                    value = viewModel.estimatedTimeUnit,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeUnitExpanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    enabled = !isLoading,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                                ExposedDropdownMenu(expanded = timeUnitExpanded, onDismissRequest = { timeUnitExpanded = false }) {
                                    viewModel.timeUnits.forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit) },
                                            onClick = {
                                                viewModel.estimatedTimeUnit = unit
                                                timeUnitExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // DISPONIBILIDAD (Day Picker UX con FlowRow)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coords ->
                            daysPickerBounds = coords.boundsInWindow()
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DÍAS DE PRESTACIÓN",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (viewModel.masterWorkingDays.isNotEmpty()) {
                            TextButton(
                                onClick = { selectedDays = viewModel.masterWorkingDays.toSet() },
                                enabled = !isLoading,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Cargar mi jornada maestra", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val days = listOf("L", "M", "M", "J", "V", "S", "D")
                        days.forEachIndexed { index, name ->
                            val dayNumber = index + 1
                            val isSelected = selectedDays.contains(dayNumber)
                            val occupiedByTitle = viewModel.occupiedDaysByOtherServices[dayNumber]
                            val isOccupiedByOther = occupiedByTitle != null
                            val isMasterAllowed = viewModel.masterWorkingDays.isEmpty() || viewModel.masterWorkingDays.contains(dayNumber)
                            
                            Box(
                                modifier = Modifier
                                    .sizeIn(minWidth = 44.dp, minHeight = 44.dp)
                                    .background(
                                        color = when {
                                            !isMasterAllowed -> Color.LightGray.copy(alpha = 0.15f)
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            isOccupiedByOther -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                                            else -> Color.LightGray.copy(alpha = 0.2f)
                                        },
                                        shape = CircleShape
                                    )
                                    .clip(CircleShape)
                                    .clickable(enabled = !isLoading && isMasterAllowed) {
                                        selectedDays = if (isSelected) selectedDays - dayNumber else selectedDays + dayNumber
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name,
                                    color = when {
                                        !isMasterAllowed -> Color.Gray.copy(alpha = 0.3f)
                                        isSelected -> Color.White
                                        isOccupiedByOther -> MaterialTheme.colorScheme.error
                                        else -> Color.Gray
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    if (viewModel.occupiedDaysByOtherServices.isNotEmpty()) {
                        val dayNames = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
                        val occupiedInfo = viewModel.occupiedDaysByOtherServices.entries.joinToString(", ") { (day, title) ->
                            "${dayNames[day - 1]}: $title"
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "ℹ️ Días asignados a otros de tus servicios: $occupiedInfo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                // SELECTORES DE HORA
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coords ->
                            scheduleBounds = coords.boundsInWindow()
                        }
                ) {
                    Text(
                        text = "RANGO HORARIO",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("Inicio") },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("08:00") },
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = { Text("Fin") },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("18:00") },
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // MATERIALES
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = materialsIncluded, onCheckedChange = { materialsIncluded = it }, enabled = !isLoading)
                            Text("¿Incluye materiales e insumos?", fontWeight = FontWeight.Medium)
                        }

                        if (!materialsIncluded) {
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = extraCost, onValueChange = { extraCost = it },
                                label = { Text("Costo extra de materiales") },
                                prefix = { Text("$") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = !isLoading, shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                val displayedError = errorMessage ?: currentValidationError

                if (!displayedError.isNullOrEmpty()) {
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
                                text = displayedError,
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
                        onClick = { viewModel.goToStep(1) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading
                    ) {
                        Text("⬅️ Volver", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.saveService(
                                serviceId = serviceId,
                                title = title,
                                description = description,
                                price = price,
                                categoryId = selectedCategoryId,
                                estimatedTime = viewModel.combinedEstimatedTime,
                                workingDays = selectedDays.toList().sorted(),
                                startTime = startTime,
                                endTime = endTime,
                                materialsIncluded = materialsIncluded,
                                extraCost = extraCost,
                                municipality = municipality
                            ) { success ->
                                if (success) {
                                    if (serviceId == null) {
                                        showSuccessDialog = true
                                    } else {
                                        onServiceCreated()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1.5f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading && currentValidationError == null
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White)
                        else Text(
                            text = if (serviceId == null) "Publicar 🚀" else "Guardar Cambios", 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // ORGANISMO ATÓMICO: Tutorial In-App (ShowOnce + Spotlight Cutout Sincronizado por Paso del Wizard)
    if (viewModel.currentStep == 1) {
        YayaTutorialOverlay(
            tutorialKey = TutorialManager.TUTORIAL_CREATE_SERVICE_STEP1,
            steps = listOf(
                TutorialStep(
                    title = "📍 Municipio de Cobertura",
                    description = "Selecciona el municipio donde prestarás este servicio para que los clientes de esa zona lo encuentren en su catálogo.",
                    targetBounds = municipalityBounds,
                    targetCornerRadius = 16.dp,
                    targetPadding = 2.dp
                )
            )
        )
    } else {
        YayaTutorialOverlay(
            tutorialKey = TutorialManager.TUTORIAL_CREATE_SERVICE_STEP2,
            steps = listOf(
                TutorialStep(
                    title = "📅 Días de Atención",
                    description = "Selecciona los días de la semana en los que estarás disponible. Puedes cargar tu jornada maestra de trabajo en un solo clic.",
                    targetBounds = daysPickerBounds,
                    targetCornerRadius = 20.dp,
                    targetPadding = 4.dp
                ),
                TutorialStep(
                    title = "⏱️ Rango Horario y Traslapes",
                    description = "Define la hora de inicio y fin. El sistema validará automáticamente que no existan cruces de horario con tus otros servicios activos.",
                    targetBounds = scheduleBounds,
                    targetCornerRadius = 16.dp,
                    targetPadding = 2.dp
                )
            )
        )
    }
}
