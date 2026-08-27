package com.bhplusplus.yaya.ui.components.molecules

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun RatingIndicator(
    rating: Double,
    totalRatings: Int,
    modifier: Modifier = Modifier
) {
    val ratingText = remember(rating, totalRatings) {
        if (totalRatings > 0) String.format(Locale.getDefault(), "%.1f (%d)", rating, totalRatings) 
        else "Sin reseñas"
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = if (totalRatings > 0) Color(0xFFFFB800) else Color.Gray.copy(alpha = 0.3f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = ratingText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (totalRatings > 0) MaterialTheme.colorScheme.onSurface else Color.Gray
        )
    }
}
