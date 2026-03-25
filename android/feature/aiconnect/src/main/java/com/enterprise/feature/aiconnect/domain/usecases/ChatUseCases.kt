package com.enterprise.feature.aiconnect.domain.usecases

import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.UCPMessage
import com.enterprise.core.domain.repositories.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Sends a message via chat repository. */
class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(text: String): DomainResult<UCPMessage> {
        return chatRepository.sendMessage(text)
    }
}

/** Observes incoming chat message stream. */
class ObserveChatStreamUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(): Flow<UCPMessage> {
        return chatRepository.messageStream()
    }
}

/** Gets chat history from server. */
class GetChatHistoryUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(limit: Int = 50): DomainResult<List<UCPMessage>> {
        return chatRepository.getHistory(limit)
    }
}
