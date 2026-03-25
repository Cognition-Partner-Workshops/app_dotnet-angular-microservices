package com.enterprise.feature.aiconnect.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.enterprise.core.uicomponents.ChatInputBar
import com.enterprise.core.uicomponents.MessageBubbleComposable

/** AI Connect chat screen with message list, typing indicator, and input bar. */
@Composable
fun AIConnectScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onDeepLink: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("chatMessagesList"),
        ) {
            items(state.messages, key = { it.id }) { message ->
                MessageBubbleComposable(
                    message = message,
                    onActionTapped = { targetUri ->
                        viewModel.processIntent(
                            ChatViewModel.Intent.AttachmentActionTapped(targetUri)
                        )
                        onDeepLink(targetUri)
                    }
                )
            }

            // Typing indicator
            if (state.isStreaming) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column {
                            Text(
                                text = "AI is thinking...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.testTag("typingIndicator")
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth(0.3f)
                                    .height(2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Sticky input bar
        ChatInputBar(
            text = state.inputText,
            onTextChanged = {
                viewModel.processIntent(ChatViewModel.Intent.InputTextChanged(it))
            },
            onSendTapped = {
                viewModel.processIntent(ChatViewModel.Intent.SendTapped)
            },
            onMicTapped = {
                viewModel.processIntent(ChatViewModel.Intent.MicTapped)
            },
            onCameraTapped = {
                viewModel.processIntent(ChatViewModel.Intent.CameraTapped)
            }
        )
    }
}
