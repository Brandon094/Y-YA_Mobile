package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhplusplus.yaya.ui.components.atoms.YayaAvatar

@Composable
fun ProfileHeroHeader(
    name: String,
    email: String,
    avatarUrl: String?,
    role: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 32.dp, bottom = 40.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            YayaAvatar(
                imageUrl = avatarUrl,
                size = 120.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = name,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = email,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Badge de Rol
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (role == "provider") "PRESTADOR" else "CLIENTE",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
