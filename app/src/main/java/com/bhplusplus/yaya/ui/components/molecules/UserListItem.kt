package com.bhplusplus.yaya.ui.components.molecules

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bhplusplus.yaya.data.models.UserProfile
import com.bhplusplus.yaya.ui.components.atoms.YayaAvatar

@Composable
fun UserListItem(
    profile: UserProfile,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(profile.full_name) },
        supportingContent = { Text("Rol: ${profile.role} | ID: ${profile.document_id ?: "N/A"}") },
        leadingContent = { 
            YayaAvatar(
                imageUrl = profile.avatar_url,
                size = 40.dp
            )
        },
        trailingContent = {
            if (profile.role == "admin") {
                Badge(containerColor = MaterialTheme.colorScheme.primary) { Text("Admin") }
            }
        }
    )
}
