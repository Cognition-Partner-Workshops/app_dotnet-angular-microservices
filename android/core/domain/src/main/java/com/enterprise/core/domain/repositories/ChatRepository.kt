package com.enterprise.core.domain.repositories

import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.UCPMessage
import kotlinx.coroutines.flow.Flow

/** AI chat repository contract with WebSocket streaming. */
interface ChatRepository {
    suspend fun connect()
    suspend fun disconnect()
    suspend fun sendMessage(text: String): DomainResult<UCPMessage>
    fun messageStream(): Flow<UCPMessage>
    suspend fun getHistory(limit: Int = 50): DomainResult<List<UCPMessage>>
}
