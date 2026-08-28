package com.bhplusplus.yaya.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.ui.components.ChatContactItemShimmer
import com.bhplusplus.yaya.ui.components.molecules.ChatContactItem

/**
 * PANTALLA DE LISTADO DE CHATS
 * Orquestada bajo Atomic Design.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onBack: () -> Unit,
    onChatClick: (String, String) -> Unit,
    viewModel: ChatListViewModel = viewModel()
) {
    val pullToRefreshState = rememberPullToRefreshState()
    
    LaunchedEffect(Unit) {
        viewModel.loadChats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mensajes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = viewModel.isLoading,
            onRefresh = { viewModel.loadChats() },
            state = pullToRefreshState,
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (viewModel.isLoading && viewModel.chatSummaries.isEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(6) {
                            ChatContactItemShimmer()
                        }
                    }
                } else if (viewModel.chatSummaries.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No tienes conversaciones activas.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(viewModel.chatSummaries) { summary ->
                            // Molécula: Item de contacto en el chat
                            ChatContactItem(
                                summary = summary,
                                onClick = { onChatClick(summary.contact.id, summary.contact.full_name) }
                            )
                        }
                    }
                }
            }
        }
    }
}
