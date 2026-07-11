package com.bhplusplus.yaya.ui.screens.my_services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.data.models.Service
import java.text.NumberFormat
import java.util.Locale

/**
 * PANTALLA DE MIS SERVICIOS (VISTA PRESTADOR)
 * Permite al prestador administrar su catálogo de talentos: editar, pausar o eliminar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyServicesScreen(
    onBack: () -> Unit,
    onEditService: (String) -> Unit, // Navega a la pantalla de edición (CreateService con ID)
    viewModel: MyServicesViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Servicios Publicados", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (viewModel.services.isEmpty()) {
                EmptyServicesView()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewModel.services) { service ->
                        MyServiceItem(
                            service = service,
                            onToggleStatus = { viewModel.toggleServiceStatus(service.id!!, service.status) },
                            onDelete = { viewModel.deleteService(service.id!!) },
                            onEdit = { onEditService(service.id!!) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Representa un servicio individual con controles de gestión.
 */
@Composable
fun MyServiceItem(
    service: Service,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar servicio?") },
            text = { Text("Se borrará permanentemente de la plataforma YÁYA.") },
            confirmButton = {
                Button(onClick = { onDelete(); showDeleteDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }, // HACEMOS TODA LA TARJETA CLICKABLE PARA EDITAR (Mejor UX)
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = service.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = currencyFormatter.format(service.price), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
                
                // Switch de Activación (Pausar/Activar)
                val isPending = service.status == "pending_approval"
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Switch(
                        checked = service.status == "active",
                        onCheckedChange = { if (!isPending) onToggleStatus() },
                        enabled = !isPending, // BLOQUEADO SI ESTÁ PENDIENTE
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            disabledCheckedThumbColor = Color.Gray.copy(alpha = 0.5f)
                        )
                    )
                    Text(
                        text = when (service.status) {
                            "active" -> "Activo"
                            "pending_approval" -> "En Revisión"
                            else -> "Pausado"
                        },
                        fontSize = 10.sp,
                        color = when (service.status) {
                            "active" -> Color(0xFF4CAF50)
                            "pending_approval" -> Color(0xFFFF9800) // Naranja para aviso
                            else -> Color.Gray
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(text = service.description, fontSize = 14.sp, color = Color.Gray, maxLines = 2)
            
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Indicador visual de que se puede editar
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Editar información", fontSize = 12.sp)
                }

                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun EmptyServicesView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.WorkOutline, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Spacer(Modifier.height(16.dp))
        Text(text = "Aún no has publicado servicios", color = Color.Gray)
        Text(text = "¡Publica tu primer talento ahora!", fontSize = 14.sp, color = Color.LightGray)
    }
}
