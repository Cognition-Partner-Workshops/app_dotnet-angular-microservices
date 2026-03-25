package com.enterprise.feature.aiconnect.domain.entities

import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.UCPMessage

/** Chat state for the AI Connect MVI pattern. */
data class ChatState(
    val messages: List<UCPMessage> = emptyList(),
    val isConnected: Boolean = false,
    val isStreaming: Boolean = false,
    val currentStreamText: String = "",
    val error: DomainError? = null
)
