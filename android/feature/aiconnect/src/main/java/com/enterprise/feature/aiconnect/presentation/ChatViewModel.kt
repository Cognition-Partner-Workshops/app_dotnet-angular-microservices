package com.enterprise.feature.aiconnect.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.MessageRole
import com.enterprise.core.domain.entities.UCPMessage
import com.enterprise.feature.aiconnect.domain.usecases.GetChatHistoryUseCase
import com.enterprise.feature.aiconnect.domain.usecases.ObserveChatStreamUseCase
import com.enterprise.feature.aiconnect.domain.usecases.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/** MVI ViewModel for the AI Connect chat feature. */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val observeChatStreamUseCase: ObserveChatStreamUseCase,
    private val getChatHistoryUseCase: GetChatHistoryUseCase
) : ViewModel() {

    data class State(
        val messages: List<UCPMessage> = emptyList(),
        val inputText: String = "",
        val isConnected: Boolean = false,
        val isStreaming: Boolean = false,
        val error: DomainError? = null
    )

    sealed class Intent {
        data object Connect : Intent()
        data object Disconnect : Intent()
        data class InputTextChanged(val text: String) : Intent()
        data object SendTapped : Intent()
        data object MicTapped : Intent()
        data object CameraTapped : Intent()
        data class AttachmentActionTapped(val targetUri: String) : Intent()
        data object LoadHistory : Intent()
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        processIntent(Intent.Connect)
        processIntent(Intent.LoadHistory)
    }

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.Connect -> connectToStream()
            is Intent.Disconnect -> disconnect()
            is Intent.InputTextChanged -> _state.update { it.copy(inputText = intent.text) }
            is Intent.SendTapped -> sendMessage()
            is Intent.MicTapped -> { /* Speech-to-text integration */ }
            is Intent.CameraTapped -> { /* Camera capture integration */ }
            is Intent.AttachmentActionTapped -> handleDeepLink(intent.targetUri)
            is Intent.LoadHistory -> loadHistory()
        }
    }

    private fun connectToStream() {
        viewModelScope.launch {
            _state.update { it.copy(isConnected = true) }
            observeChatStreamUseCase().collect { message ->
                _state.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages + message,
                        isStreaming = false
                    )
                }
            }
        }
    }

    private fun disconnect() {
        _state.update { it.copy(isConnected = false) }
    }

    private fun sendMessage() {
        val text = _state.value.inputText.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            // Optimistic UI update
            val userMessage = UCPMessage(
                id = UUID.randomUUID().toString(),
                role = MessageRole.USER,
                content = text,
                timestamp = Date()
            )
            _state.update {
                it.copy(
                    messages = it.messages + userMessage,
                    inputText = "",
                    isStreaming = true
                )
            }

            when (val result = sendMessageUseCase(text)) {
                is DomainResult.Success -> {
                    // Message sent successfully; response comes via stream
                }
                is DomainResult.Failure -> {
                    _state.update {
                        it.copy(
                            isStreaming = false,
                            error = result.error
                        )
                    }
                }
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            when (val result = getChatHistoryUseCase()) {
                is DomainResult.Success -> {
                    _state.update { it.copy(messages = result.data) }
                }
                is DomainResult.Failure -> {
                    _state.update { it.copy(error = result.error) }
                }
            }
        }
    }

    private fun handleDeepLink(targetUri: String) {
        // Route through GlobalNavigator
    }
}
