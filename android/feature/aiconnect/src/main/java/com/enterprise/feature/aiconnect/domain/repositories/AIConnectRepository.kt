package com.enterprise.feature.aiconnect.domain.repositories

import com.enterprise.core.domain.repositories.ChatRepository

/** Extended chat repository with local LLM fallback support. */
interface AIConnectRepository : ChatRepository {
    suspend fun isLocalLLMAvailable(): Boolean
    suspend fun queryLocalLLM(prompt: String): String
}
