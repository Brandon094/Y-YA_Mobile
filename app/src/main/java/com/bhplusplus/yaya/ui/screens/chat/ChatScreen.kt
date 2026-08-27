package com.bhplusplus.yaya.ui.screens.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bhplusplus.yaya.ui.components.molecules.ChatBubble
import com.bhplusplus.yaya.ui.components.molecules.ChatInputBar
import com.bhplusplus.yaya.ui.components.organisms.ChatHeader

/**
 * PANTALLA DE CHAT EN TIEMPO REAL (Atomic Design Refactor)
 * Arquitectura MVVM: La View solo renderiza el estado procesado del ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    receiverId: String,
    receiverName: String,
    onBack: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    var textValue by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val uiMessages = viewModel.uiMessages

    // Inicializar chat
    LaunchedEffect(receiverId) {
        viewModel.initializeChat(receiverId)
    }

    // Scroll automático al final cuando hay mensajes nuevos
    LaunchedEffect(uiMessages.size) {
        if (uiMessages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.ime,
        topBar = {
            // Organismo: Cabecera del chat
            ChatHeader(
                receiverName = receiverName,
                avatarUrl = viewModel.receiverProfile?.avatar_url,
                onBack = onBack
            )
        },
        bottomBar = {
            // Molécula: Barra de entrada de mensajes
            ChatInputBar(
                textValue = textValue,
                onValueChange = { textValue = it },
                onSendClick = {
                    if (textValue.isNotBlank()) {
                        viewModel.sendMessage(receiverId, textValue)
                        textValue = ""
                    }
                },
                enabled = !viewModel.isLoading
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (viewModel.isLoading && uiMessages.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 3.dp)
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    reverseLayout = true
                ) {
                    items(uiMessages, key = { it.id }) { state ->
                        // Molécula: Burbuja de mensaje
                        ChatBubble(state = state)
                    }
                }
            }
        }
    }
}
