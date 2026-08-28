package com.bhplusplus.yaya.ui.components.atoms

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PoweredByBH(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Powered by BH++",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .navigationBarsPadding()
            .padding(bottom = 8.dp)
    )
}
