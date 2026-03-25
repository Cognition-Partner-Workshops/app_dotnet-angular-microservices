package com.enterprise.feature.explore.domain.usecases

import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.UIComponent
import com.enterprise.core.domain.usecases.FeatureFlagUseCase
import com.enterprise.feature.explore.domain.repositories.ExploreFeedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Get explore feed applying feature flag variant. */
class GetExploreFeedUseCase @Inject constructor(
    private val feedRepository: ExploreFeedRepository,
    private val featureFlagUseCase: FeatureFlagUseCase
) {
    operator fun invoke(): Flow<DomainResult<List<UIComponent>>> {
        return feedRepository.getDynamicFeed()
    }

    fun personalized(userId: String): Flow<DomainResult<List<UIComponent>>> {
        return feedRepository.getPersonalizedFeed(userId)
    }

    suspend fun search(query: String): DomainResult<List<UIComponent>> {
        return feedRepository.searchFeed(query)
    }
}
