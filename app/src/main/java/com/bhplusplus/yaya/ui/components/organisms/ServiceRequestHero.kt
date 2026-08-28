package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bhplusplus.yaya.ui.components.atoms.YayaAvatar

@Composable
fun ServiceRequestHero(
    title: String,
    providerName: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                YayaAvatar(
                    imageUrl = avatarUrl,
                    size = 56.dp
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title, 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.ExtraBold, 
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Con: $providerName", 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
