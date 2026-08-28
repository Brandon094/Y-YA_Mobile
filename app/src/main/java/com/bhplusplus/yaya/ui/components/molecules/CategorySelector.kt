package com.bhplusplus.yaya.ui.components.molecules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bhplusplus.yaya.data.models.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategorySelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategoryId == null,
                onClick = { onCategorySelect(null) },
                label = { Text("Ver todo") },
                leadingIcon = if (selectedCategoryId == null) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                shape = RoundedCornerShape(12.dp)
            )
        }
        items(categories) { category ->
            FilterChip(
                selected = selectedCategoryId == category.id,
                onClick = { onCategorySelect(category.id) },
                label = { Text(category.name) },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}
