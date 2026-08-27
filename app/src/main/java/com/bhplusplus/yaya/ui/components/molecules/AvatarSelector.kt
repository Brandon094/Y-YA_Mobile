package com.bhplusplus.yaya.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhplusplus.yaya.ui.components.atoms.YayaAvatar

@Composable
fun AvatarSelector(
    imageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable(enabled = !isLoading) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            YayaAvatar(
                imageUrl = imageUrl,
                size = 120.dp
            )
            
            if (imageUrl == null) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Toca para cambiar foto de perfil",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
