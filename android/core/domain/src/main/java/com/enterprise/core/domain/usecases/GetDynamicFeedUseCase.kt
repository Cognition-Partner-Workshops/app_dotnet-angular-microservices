package com.enterprise.core.domain.usecases

import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.UIComponent
import com.enterprise.core.domain.repositories.FeedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Returns reactive stream of feed components following SSOT pattern. */
class GetDynamicFeedUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    operator fun invoke(): Flow<DomainResult<List<UIComponent>>> {
        return feedRepository.getDynamicFeed()
    }
}
