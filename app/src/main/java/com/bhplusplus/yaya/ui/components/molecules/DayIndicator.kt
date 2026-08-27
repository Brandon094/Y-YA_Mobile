package com.bhplusplus.yaya.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DayIndicator(
    workingDays: List<Int>,
    modifier: Modifier = Modifier
) {
    val dayNames = listOf("L", "M", "M", "J", "V", "S", "D")
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        dayNames.forEachIndexed { index, name ->
            val dayNumber = index + 1
            val isSelected = workingDays.contains(dayNumber)
            Box(
                modifier = Modifier
                    .sizeIn(minWidth = 24.dp, minHeight = 24.dp)
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) 
                                else Color.Transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.outlineVariant,
                    maxLines = 1
                )
            }
        }
    }
}
