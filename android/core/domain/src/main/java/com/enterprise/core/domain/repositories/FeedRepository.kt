package com.enterprise.core.domain.repositories

import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.UIComponent
import kotlinx.coroutines.flow.Flow

/** Dynamic feed repository contract (SSOT: UI observes local DB). */
interface FeedRepository {
    fun getDynamicFeed(): Flow<DomainResult<List<UIComponent>>>
    suspend fun refreshFeed(): DomainResult<List<UIComponent>>
    suspend fun getCachedFeed(): List<UIComponent>
}
