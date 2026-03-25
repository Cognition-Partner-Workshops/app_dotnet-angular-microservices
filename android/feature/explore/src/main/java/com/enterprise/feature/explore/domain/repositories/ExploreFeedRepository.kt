package com.enterprise.feature.explore.domain.repositories

import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.UIComponent
import com.enterprise.core.domain.repositories.FeedRepository
import kotlinx.coroutines.flow.Flow

/** Extended feed repository with personalization and search. */
interface ExploreFeedRepository : FeedRepository {
    fun getPersonalizedFeed(userId: String): Flow<DomainResult<List<UIComponent>>>
    suspend fun searchFeed(query: String): DomainResult<List<UIComponent>>
}
