package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhplusplus.yaya.ui.components.atoms.YayaAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeader(
    receiverName: String,
    avatarUrl: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isModeration: Boolean = false
) {
    TopAppBar(
        modifier = modifier,
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                YayaAvatar(
                    imageUrl = if (isModeration) null else avatarUrl,
                    size = 40.dp,
                    isModeration = isModeration
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = receiverName, 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "En línea ahora", 
                        fontSize = 11.sp, 
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Volver"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            titleContentColor = MaterialTheme.colorScheme.onSecondary,
            navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
        )
    )
}
