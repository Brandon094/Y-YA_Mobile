package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.ui.components.atoms.YayaAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    userName: String,
    avatarUrl: String?,
    unreadMessagesCount: Int,
    notificationCount: Int,
    onProfileClick: () -> Unit,
    onChatListClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Column {
                Text(
                    text = stringResource(R.string.app_brand_yaya),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (userName.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.home_welcome_back, userName),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f)
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onProfileClick) {
                YayaAvatar(
                    imageUrl = avatarUrl,
                    size = 32.dp
                )
            }
        },
        actions = {
            IconButton(onClick = onChatListClick) {
                BadgedBox(
                    badge = {
                        if (unreadMessagesCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ) {
                                Text(unreadMessagesCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "Mis Chats",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }

            IconButton(onClick = onNotificationsClick) {
                BadgedBox(
                    badge = {
                        if (notificationCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ) {
                                Text(notificationCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            titleContentColor = MaterialTheme.colorScheme.onSecondary,
            navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
        )
    )
}
