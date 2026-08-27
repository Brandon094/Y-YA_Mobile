package com.bhplusplus.yaya.ui.components.molecules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhplusplus.yaya.data.models.Rating
import com.bhplusplus.yaya.utils.FormatterUtils

@Composable
fun YayaRatingItem(
    rating: Rating,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.wrapContentWidth()) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = Icons.Default.Star, 
                            contentDescription = null, 
                            modifier = Modifier.size(14.dp), 
                            tint = if (i <= rating.score) Color(0xFFFFB800) else Color.Gray.copy(alpha = 0.3f)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = FormatterUtils.formatDate(rating.created_at), 
                    fontSize = 11.sp, 
                    color = Color.Gray, 
                    modifier = Modifier.weight(1f), 
                    textAlign = TextAlign.End
                )
            }
            if (!rating.comment.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = rating.comment,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
