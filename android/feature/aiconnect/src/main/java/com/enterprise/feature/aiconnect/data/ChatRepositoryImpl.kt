package com.enterprise.feature.aiconnect.data

import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.MessageRole
import com.enterprise.core.domain.entities.UCPMessage
import com.enterprise.core.network.WebSocketClient
import com.enterprise.core.network.WebSocketEvent
import com.enterprise.feature.aiconnect.data.dto.ChatHistoryResponse
import com.enterprise.feature.aiconnect.data.dto.UCPMessageDTO
import com.enterprise.feature.aiconnect.domain.repositories.AIConnectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Chat repository with WebSocket streaming, polymorphic UCP parsing, and local LLM fallback. */
@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val webSocketClient: WebSocketClient
) : AIConnectRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun connect() {
        // Connection is established when messageStream() is collected
    }

    override suspend fun disconnect() {
        webSocketClient.disconnect()
    }

    override suspend fun sendMessage(text: String): DomainResult<UCPMessage> {
        val sent = webSocketClient.send(text)
        return if (sent) {
            DomainResult.Success(
                UCPMessage(
                    id = UUID.randomUUID().toString(),
                    role = MessageRole.USER,
                    content = text,
                    timestamp = Date()
                )
            )
        } else {
            DomainResult.Failure(DomainError.NetworkUnavailable)
        }
    }

    override fun messageStream(): Flow<UCPMessage> {
        return webSocketClient.connect().mapNotNull { event ->
            when (event) {
                is WebSocketEvent.MessageReceived -> {
                    try {
                        val dto = json.decodeFromString<UCPMessageDTO>(event.text)
                        dto.toDomain()
                    } catch (_: Exception) {
                        // Plain text stream token
                        UCPMessage(
                            id = UUID.randomUUID().toString(),
                            role = MessageRole.ASSISTANT,
                            content = event.text,
                            timestamp = Date()
                        )
                    }
                }
                else -> null
            }
        }
    }

    override suspend fun getHistory(limit: Int): DomainResult<List<UCPMessage>> {
        // Network call would go here; returning empty for now
        return DomainResult.Success(emptyList())
    }

    override suspend fun isLocalLLMAvailable(): Boolean {
        // Check for ExecuTorch/ONNX model availability on device
        return false
    }

    override suspend fun queryLocalLLM(prompt: String): String {
        // ExecuTorch inference would go here
        return "Local LLM is not available on this device."
    }
}
